package io.mars.ai.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.mars.ai.domain.AiQuery;
import io.mars.ai.domain.AiResponse;
import io.mars.ai.services.AiServices;
import io.mars.support.MarsUserSession;
import io.mars.support.smarttypes.GeoPoint;
import io.mars.support.util.SecurityUtil;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.ai.llm.model.VLlmMessageStreamConfig;
import io.vertigo.ai.llm.model.VPersona;
import io.vertigo.ai.llm.model.VPrompt;
import io.vertigo.ai.llm.model.VPromptContext;
import io.vertigo.ai.llm.model.rag.VLlmDocument;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.geo.geocoder.GeoCoderManager;
import io.vertigo.geo.geocoder.GeoLocation;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Controller
@RequestMapping("/ai/extract/")
public class AiExtractController extends AbstractVSpringMvcController {

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private AiServices aiServices;

	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private LlmManager llmManager;

	@Inject
	private GeoCoderManager geoCoderManager;

	private static final ViewContextKey<AiQuery> aiQueryKey = ViewContextKey.of("aiQuery");
	private static final ViewContextKey<AiResponse> aiFileResponsesKey = ViewContextKey.of("aiFileResponses");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//---
		viewContext
				.publishDto(aiQueryKey, new AiQuery())
				.publishDtList(aiFileResponsesKey, new DtList<>(AiResponse.class))
				//---
				.toModeCreate();
	}

	@PostMapping("/_analyze")
	@ResponseBody
	public String doAnalyze(@RequestParam("fileUri") final FileInfoURI fileUri) {
		final var response = new AiResponse();
		response.setDocUri(fileUri);

		final var file = fileStoreManager.read(fileUri);
		final var docSource = llmManager.getTemporaryDocumentSource();
		docSource.addDocument(new VLlmDocument(file));

		response.setDescription(llmManager.askOnAllFiles(VPrompt.builder("Décrit moi en 10 mots maximum ce qu'est ce fichier").build(), docSource).getHtml());
		response.setSummary(llmManager.askOnAllFiles(VPrompt.builder("Décrit moi sans détail et en 60 mots max le contenu").build(), docSource).getHtml());

		final var fileAddress = llmManager.askOnAllFiles(
				VPrompt.builder(
						"Quelle est l'adresse postale principale du document (pas d'adresse web) ? répond uniquement l'adresse avec des caractères romain (traduit en français si ce n'est pas le cas) sans autre texte ni mise en forme. Répond uniquement sur le format suivant : '123 rue du Soleil 75000 Paris', si aucune adresse ne correspond à ce format, ne rien répondre, sans autre texte ni mise en forme")
						.build(),
				docSource);
		if (!StringUtil.isBlank(fileAddress.getText())) {
			response.setAddress(fileAddress.getText());

			try {
				final var geoLocation = geoCoderManager.findLocation(fileAddress.getText());
				if (geoLocation != GeoLocation.UNDEFINED) {
					final var point = new GeoPoint(geoLocation.getLongitude(), geoLocation.getLatitude());
					response.setGps(point);
				}
			} catch (final Exception e) {
				// Do nothing
			}
		}

		final String dateString = llmManager.askOnAllFiles(VPrompt.builder(
				"Quelle est la date d'effet du document ? répond sous la forme 2007-12-23 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme. Si il est précisé un mois, donne le premier jour du mois. Si il est précisé un trimestre, donne le premier jour du trimestre.")
				.build(),
				docSource).getText();
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		response.setTags(llmManager.askOnAllFiles(VPrompt.builder(
				"Donne moi entre 1 et 3 tags décrivant le mieux la nature du fichier. Un tag est un mot générique et unique en camelCase qualifiant la nature du fichier et non son contenu. Répond sous la forme 'tag1;tag2;tag3' sans autre texte ni mise en forme")
				.build(),
				docSource).getText());

		final var rawPersons = llmManager.askOnAllFiles(VPrompt.builder(
				"Donne moi la liste des personnes physiques citées dans le fichier. Répond sous la forme 'NOM Prénom;NOM Prénom;NOM Prénom' sans autre texte ni mise en forme. Si aucune personne n'est citée, ne rien répondre, sans autre texte ni mise en forme")
				.build(),
				docSource);
		if (!StringUtil.isBlank(rawPersons.getText())) {
			response.setPersons(rawPersons.getText());
		}

		return jsonEngine.toJson(response);
	}

	@PostMapping("/_initChat")
	@ResponseBody
	public String initChat(@RequestParam("fileUris") final List<FileInfoURI> fileUris, @RequestParam("persona") final String personaCode) {
		final VPromptContext context = new VPromptContext();
		final var persona = aiServices.getPersona(personaCode);
		if (persona != null) {
			context.setPersona(persona);
		}
		final var connectedUser = SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson();
		context.setUserPersona(new VPersona(connectedUser.getFullName(), null, null, null, null));

		final var docSource = llmManager.getTemporaryDocumentSource();
		fileUris.stream()
				.map(fileStoreManager::read)
				.forEach(file -> docSource.addDocument(new VLlmDocument(file)));

		return "{\"id\":\"" + llmManager.initChatOnAllFiles(context, docSource).getId() + "\"}"; // String because Java Long is too big for javascript numbers
	}

	@PostMapping("/_ask")
	@ResponseBody
	public String doChat(@RequestParam("prompt") final String instructions, @RequestParam("chatId") final UUID chatId) {
		return llmManager.getChat(chatId).chat(instructions).message().getHtml();
	}

	@GetMapping("/_askStream")
	public SseEmitter doChatStream(@RequestParam("prompt") final String instructions, @RequestParam("chatId") final UUID chatId) {
		final SseEmitter emitter = new SseEmitter();
		llmManager.getChat(chatId).chatStream(instructions, VLlmMessageStreamConfig.builder()
				.withPartialMessageHandler(chatMessage -> {
					try {
						emitter.send(Map.of("message", chatMessage.message().getHtml()));
					} catch (final IOException e) {
						throw WrappedException.wrap(e);
					}
				})
				.withMessageHandler(chatMessage -> {
					try {
						emitter.send(Map.of("message", chatMessage.message().getHtml(), "sources", chatMessage.message().getSources(), "end", true));
						emitter.complete();
					} catch (final IOException e) {
						throw WrappedException.wrap(e);
					}
				})
				.withErrorHandler(emitter::completeWithError)
				.build());
		return emitter;
	}

}

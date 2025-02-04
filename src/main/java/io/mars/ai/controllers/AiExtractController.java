package io.mars.ai.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.mars.ai.domain.AiQuery;
import io.mars.ai.domain.AiResponse;
import io.mars.support.smarttypes.GeoPoint;
import io.vertigo.ai.impl.llm.StandardPrompts;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.ai.llm.model.VPrompt;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfo;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.geo.geocoder.GeoCoderManager;
import io.vertigo.geo.geocoder.GeoLocation;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Controller
@RequestMapping("/ai/extract/")
public class AiExtractController extends AbstractVSpringMvcController {

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private LlmManager llmManager;

	@Inject
	private GeoCoderManager geoCoderManager;

	private static final ViewContextKey<AiQuery> aiQueryKey = ViewContextKey.of("aiQuery");
	private static final ViewContextKey<AiResponse> aiFileResponsesKey = ViewContextKey.of("aiFileResponses");
	private static final ViewContextKey<Long> chatIdKey = ViewContextKey.of("chatId");

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

		final var file = fileStoreManager.read(fileUri).getVFile();

		response.setDescription(llmManager.promptOnFiles(new VPrompt("Décrit moi en 10 mots maximum ce qu'est ce fichier", null, null), file).getHtml());
		response.setSummary(llmManager.promptOnFiles(new VPrompt(StandardPrompts.SUMMARY_PROMPT, null, null), file).getHtml());

		final var fileAddress = llmManager.promptOnFiles(
				new VPrompt(
						"Quelle est l'adresse postale principale du document (pas d'adresse web) ? répond uniquement l'adresse avec des caractères romain (traduit en français si ce n'est pas le cas) sans autre texte ni mise en forme. Répond uniquement sur le format suivant : '123 rue du Soleil 75000 Paris', si aucune adresse ne correspond à ce format, ne rien répondre, sans autre texte ni mise en forme",
						null, null),
				file);
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

		final String dateString = llmManager.promptOnFiles(new VPrompt(
				"Quelle est la date d'effet du document ? répond sous la forme 2007-12-23 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme. Si il est précisé un mois, donne le premier jour du mois. Si il est précisé un trimestre, donne le premier jour du trimestre.",
				null, null), file).getText();
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		response.setTags(llmManager.promptOnFiles(new VPrompt(
				"Donne moi entre 1 et 3 tags décrivant le mieux la nature du fichier. Un tag est un mot générique et unique en camelCase qualifiant la nature du fichier et non son contenu. Répond sous la forme 'tag1;tag2;tag3' sans autre texte ni mise en forme",
				null, null), file).getText());

		final var rawPersons = llmManager.promptOnFiles(new VPrompt(
				"Donne moi la liste des personnes physiques citées dans le fichier. Répond sous la forme 'NOM Prénom;NOM Prénom;NOM Prénom' sans autre texte ni mise en forme. Si aucune personne n'est citée, ne rien répondre, sans autre texte ni mise en forme",
				null, null), file);
		if (!StringUtil.isBlank(rawPersons.getText())) {
			response.setPersons(rawPersons.getText());
		}

		return jsonEngine.toJson(response);
	}

	@PostMapping("/_initChat")
	public ViewContext initChat(final ViewContext viewContext, @RequestParam("fileUris") final List<FileInfoURI> fileUris) {
		final var files = fileUris.stream()
				.map(fileStoreManager::read)
				.map(FileInfo::getVFile)
				.toList();

		return viewContext
				.publishRef(chatIdKey, llmManager.initChat(files).getId());
	}

	@PostMapping("/_ask")
	@ResponseBody
	public String doExtract(@RequestParam("prompt") final String prompt, @ViewAttribute("chatId") final Long chatId) {
		return llmManager.getChat(chatId).chat(new VPrompt(prompt, null, null)).getHtml();
	}

}

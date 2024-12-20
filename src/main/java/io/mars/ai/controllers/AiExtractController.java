package io.mars.ai.controllers;

import java.time.LocalDate;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.mars.ai.domain.AiQuery;
import io.mars.ai.domain.AiResponse;
import io.mars.ai.domain.Location;
import io.vertigo.ai.impl.llm.VPrompt;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.FileStoreManager;
import io.vertigo.datastore.filestore.model.FileInfoURI;
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

	private static final ViewContextKey<AiQuery> aiQueryKey = ViewContextKey.of("aiQuery");
	private static final ViewContextKey<AiResponse> aiResponseKey = ViewContextKey.of("aiResponse");
	private static final ViewContextKey<AiResponse> aiFileResponsesKey = ViewContextKey.of("aiFileResponses");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//---
		viewContext
				.publishDto(aiQueryKey, new AiQuery())
				.publishDto(aiResponseKey, new AiResponse())
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

		response.setDescription(llmManager.promptOnFiles(new VPrompt("Décrit moi en 8 mots max ce qu'est ce fichier", null), file));
		response.setSummary(llmManager.summarize(file));

		final var location = new Location();
		final var fileAddress = llmManager.promptOnFiles(
				new VPrompt("Quelle est l'adresse principale du document ? répond uniquement l'adresse sans autre texte ni mise en forme. Exemple : '123 rue du Soleil 75000 Paris'", null), file);
		location.setAddress(fileAddress);
		response.setLocation(location);

		final String dateString = llmManager.promptOnFiles(new VPrompt(
				"Quelle est la date d'effet du document ? répond sous la forme 2007-12-03 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme",
				null), file);
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		final var rawTags = llmManager.promptOnFiles(new VPrompt(
				"Donne moi entre 1 et 3 tags correspondant le mieux au fichier. Un tag est un mot générique et unique en camelCase qualifiant la nature du fichier. Répond sous la forme 'tag1;tag2;tag3' sans autre texte ni mise en forme",
				null), file);
		response.setTags(rawTags);

		final var rawPersons = llmManager.promptOnFiles(new VPrompt(
				"Donne moi la liste des personnes physiques citées dans le fichier. Répond sous la forme 'NOM Prénom;NOM Prénom;NOM Prénom' sans autre texte ni mise en forme. Si aucune personne n'est citée, ne rien répondre, sans autre texte ni mise en forme",
				null), file);
		if (!StringUtil.isBlank(rawPersons)) {
			response.setPersons(rawPersons);
		}

		return jsonEngine.toJson(response);
	}

	@PostMapping("/_ask")
	public void doExtract(final ViewContext viewContext, @ViewAttribute("aiQuery") final AiQuery aiQuery) {

	}

}

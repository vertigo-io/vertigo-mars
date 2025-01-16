package io.mars.ai.controllers;

import java.time.LocalDate;
import java.util.stream.Collectors;

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
import io.vertigo.ai.impl.llm.VPrompt;
import io.vertigo.ai.llm.LlmManager;
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
@RequestMapping("/ai/health/")
public class AiHealthController extends AbstractVSpringMvcController {

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
	private static final ViewContextKey<AiResponse> aiOrdoResponseKey = ViewContextKey.of("aiOrdoResponse");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//---
		viewContext
				.publishDto(aiQueryKey, new AiQuery())
				.publishDtList(aiFileResponsesKey, new DtList<>(AiResponse.class))
				.publishDto(aiOrdoResponseKey, new AiResponse())
				//---
				.toModeCreate();
	}

	@PostMapping("/_analyze")
	@ResponseBody
	public String doAnalyze(@RequestParam("fileUri") final FileInfoURI fileUri, final ViewContext viewContext, @ViewAttribute("aiFileResponses") final DtList<AiResponse> aiFileResponses) {
		// save fileUri for later
		final var aiResponse = new AiResponse();
		aiResponse.setDocUri(fileUri);
		aiFileResponses.add(aiResponse);
		viewContext.publishDtList(aiFileResponsesKey, aiFileResponses);

		// respond for display to user
		final var response = aiResponse;
		response.setDocUri(fileUri);

		final var file = fileStoreManager.read(fileUri).getVFile();

		response.setName(llmManager
				.promptOnFiles(new VPrompt("Donne moi le nom du médicament en français, traduit le si nécessaire. Répond uniquement son nom sans autre texte ni formatage", null, null), file));
		response.setSummary(llmManager.promptOnFiles(new VPrompt("A quoi sert ce médicament ? répond en français uniquement", null, null), file));
		response.setDescription(llmManager.promptOnFiles(new VPrompt("Quelle est la posologie du médicament ?", null, null), file));
		response.setDescription2(llmManager.promptOnFiles(new VPrompt("Quels sont ses effets indésirables ?", null, null), file));
		response.setDescription3(llmManager.promptOnFiles(new VPrompt("Quels sont ses contres indications ?", null, null), file));

		response.setCtx(viewContext.getId());
		return jsonEngine.toJson(response);
	}

	@PostMapping("/_analyzeOrdo")
	@ResponseBody
	public String doAnalyzeOrdo(@RequestParam("fileUri") final FileInfoURI fileUri, @ViewAttribute("aiFileResponses") final DtList<AiResponse> aiFileResponses) {
		final var response = new AiResponse();
		response.setDocUri(fileUri);

		// Ordo only
		final var file = fileStoreManager.read(fileUri).getVFile();
		response.setName(llmManager.promptOnFiles(new VPrompt("Quel est le nom du médecin. Répond uniquement son nom sans autre texte ni formatage", null, null), file));
		response.setCategory(llmManager.promptOnFiles(new VPrompt("Quel est le numéro RPPS du médecin ? Répond uniquement le numéro sans autre texte ni formatage", null, null), file));
		response.setPersons(llmManager.promptOnFiles(new VPrompt("Quel est le nom du patient ? Répond uniquement son nom sans autre texte ni formatage", null, null), file));
		final var fileAddress = llmManager.promptOnFiles(
				new VPrompt(
						"Quelle est l'adresse postale principale du document (pas d'adresse web) ? répond uniquement l'adresse avec des caractères romain (traduit en français si ce n'est pas le cas) sans autre texte ni mise en forme. Répond uniquement sur le format suivant : '123 rue du Soleil 75000 Paris', si aucune adresse ne correspond à ce format, ne rien répondre, sans autre texte ni mise en forme",
						null, null),
				file);
		if (!StringUtil.isBlank(fileAddress)) {
			response.setAddress(fileAddress);

			try {
				final var geoLocation = geoCoderManager.findLocation(fileAddress);
				if (geoLocation != GeoLocation.UNDEFINED) {
					final var point = new GeoPoint(geoLocation.getLongitude(), geoLocation.getLatitude());
					response.setGps(point);
				}
			} catch (final Exception e) {
				// Do nothing
			}
		}
		final String dateString = llmManager.promptOnFiles(new VPrompt(
				"Quelle est la date d'effet du document ? répond sous la forme 2007-12-03 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme",
				null, null), file);
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		// all docs
		final var files = aiFileResponses.stream()
				.map(AiResponse::getDocUri)
				.map(fileStoreManager::read)
				.map(FileInfo::getVFile)
				.collect(Collectors.toList());
		files.add(file);

		response.setSummary(llmManager.promptOnFiles(new VPrompt(
				"""
						Peux tu me donner un schéma de médication sous le format suivant, sans inclure aucune autre information dans la réponse :
						Matin :
						- Médicament 1 du matin
						- Médicament 2 du matin
						- ...
						Midi :
						- Médicament 1 du midi
						- Médicament 2 du midi
						- ...
						Soir :
						- Médicament 1 du soir
						- Médicament 2 du soir
						- ...
						""", null, null), files));
		response.setDescription(llmManager.promptOnFiles(new VPrompt("Quels sont les principaux effets indésirables les plus courant des médicaments de l'ordonnance ?", null, null), files));
		response.setDescription2(llmManager.promptOnFiles(new VPrompt("Quels sont les principales contre-indications des médicaments de l'ordonnance?", null, null), files));

		return jsonEngine.toJson(response);
	}

}

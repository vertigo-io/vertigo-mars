package io.mars.ai.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public String doAnalyze(@RequestParam("fileUri") final FileInfoURI fileUri) {
		final var response = new AiResponse();
		response.setDocUri(fileUri);

		final var file = fileStoreManager.read(fileUri).getVFile();

		response.setName(llmManager
				.askOnFiles(VPrompt.builder("Donne moi le nom du médicament en français, traduit le si nécessaire. Répond uniquement son nom sans autre texte ni formatage").build(), file)
				.getText());
		response.setSummary(llmManager.askOnFiles(VPrompt.builder("A quoi sert ce médicament ? Répond uniquement en français.").build(), file).getHtml());
		response.setDescription(llmManager.askOnFiles(VPrompt.builder("Quelle est la posologie du médicament ? Répond uniquement en français.").build(), file).getHtml());
		response.setDescription2(llmManager.askOnFiles(VPrompt.builder("Quels sont ses effets indésirables ? Répond uniquement en français.").build(), file).getHtml());
		response.setDescription3(llmManager.askOnFiles(VPrompt.builder("Quels sont ses contres indications ? Répond uniquement en français.").build(), file).getHtml());

		return jsonEngine.toJson(response);
	}

	@PostMapping("/_analyzeOrdo")
	@ResponseBody
	public String doAnalyzeOrdo(@RequestParam("fileUri") final FileInfoURI fileUri, @RequestParam("fileUris") final List<FileInfoURI> fileUris) {
		final var response = new AiResponse();
		response.setDocUri(fileUri);

		// Ordo only
		final var file = fileStoreManager.read(fileUri).getVFile();
		response.setName(
				llmManager.askOnFiles(VPrompt.builder("Quel est le nom/prénom/titre du médecin. Répond uniquement sous la forme 'Dr. Jean DUPONT' sans autre texte ni formatage").build(), file)
						.getText());
		response.setCategory(llmManager.askOnFiles(VPrompt.builder("Quel est le numéro RPPS du médecin ? Répond uniquement le numéro sans autre texte ni formatage").build(), file).getText());
		response.setPersons(llmManager.askOnFiles(VPrompt.builder("Quel est le nom du patient ? Répond uniquement son nom sans autre texte ni formatage").build(), file).getText());
		response.setTags(llmManager.askOnFiles(
				VPrompt.builder(
						"Donne moi la liste de médicaments prescrits, donne moi la molécule active et ajoute entre parenthèse le nom du médicament prescrit. Répond sous la forme 'Molecule 1 (Medicament1);Molecule 2 (Medicament2);Molecule 3 (Medicament3);...' sans autre texte ni mise en forme").build(),
				file).getText());

		final var fileAddress = llmManager.askOnFiles(
				VPrompt.builder(
						"Quelle est l'adresse postale principale du document (pas d'adresse web) ? répond uniquement l'adresse avec des caractères romain (traduit en français si ce n'est pas le cas) sans autre texte ni mise en forme. Répond uniquement sur le format suivant : '123 rue du Soleil 75000 Paris', si aucune adresse ne correspond à ce format, ne rien répondre, sans autre texte ni mise en forme").build(),
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
		final String dateString = llmManager.askOnFiles(VPrompt.builder(
				"Quelle est la date d'effet du document ? répond sous la forme 2007-12-03 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme").build(),
				file).getText();
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		// all docs
		final var files = Stream.concat(Stream.of(fileUri), fileUris.stream())
				.map(fileStoreManager::read)
				.map(FileInfo::getVFile)
				.collect(Collectors.toList());
		files.add(file);

		response.setSummary(llmManager.askOnFiles(VPrompt.builder(
				"""
						Reporte l'ordonnance prescrite mais ordonnée sous forme d'un planning journalier. Fait attention a bien reporter tous les médicaments dans les catégories "matin", "midi" et "soir" en fonction de l'ordonnance, à ne pas oublier de médicament et que le nombre total de prises sur la journée corresponde bien à l'ordonnance.
						Un médicament devant être pris 2 fois par jour sera mis dans les catégories "matin" et "soir" afin de les espacer au maximum.
						Répond uniquement sous le format suivant, sans inclure aucune autre information dans la réponse :
						Matin :
						- tous les médicaments du matin
						- ...
						Midi :
						- tous les médicaments du midi
						- ...
						Soir :
						- tous les médicaments du soir
						- ...
						""").build(),
				file).getHtml());

		final var effetsIndesirables = llmManager.askOnFiles(VPrompt.builder("Quels sont les principaux effets indésirables les plus courant des médicaments de l'ordonnance ?").build(), files);
		final var effetsIndesirablesHtml = new StringBuilder(effetsIndesirables.getHtml());
		if (!effetsIndesirables.getSources().isEmpty()) {
			effetsIndesirablesHtml.append("<hr class=\"q-separator q-separator--horizontal q-mb-sm\"><p><strong class=\"text-deep-purple\">> Sources :</strong></p><ul>");
			for (final var source : effetsIndesirables.getSources()) {
				effetsIndesirablesHtml
				.append("<li>")
				.append(source)
				.append("</li>");
			}
			effetsIndesirablesHtml.append("</ul>");
		}
		response.setDescription(effetsIndesirablesHtml.toString());

		final var contreIndications = llmManager.askOnFiles(VPrompt.builder("Quels sont les principales contre-indications des médicaments de l'ordonnance ?").build(), files);
		final var contreIndicationsHtml = new StringBuilder(contreIndications.getHtml());
		if (!contreIndications.getSources().isEmpty()) {
			contreIndicationsHtml.append("<hr class=\"q-separator q-separator--horizontal q-mb-sm\"><p><strong class=\"text-deep-purple\">> Sources :</strong></p><ul>");
			for (final var source : contreIndications.getSources()) {
				contreIndicationsHtml
				.append("<li>")
				.append(source)
				.append("</li>");
			}
			contreIndicationsHtml.append("</ul>");
		}
		response.setDescription2(contreIndicationsHtml.toString());

		return jsonEngine.toJson(response);
	}

}

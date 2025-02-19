package io.mars.ai.controllers;

import java.util.Random;

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
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Controller
@RequestMapping("/ai/transport/")
public class AiTransportController extends AbstractVSpringMvcController {

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private LlmManager llmManager;

	private static final Random RANDOM = new Random();

	private static final ViewContextKey<AiQuery> aiQueryKey = ViewContextKey.of("aiQuery");
	private static final ViewContextKey<AiResponse> aiResponsesKey = ViewContextKey.of("aiResponses");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//---
		viewContext
				.publishDto(aiQueryKey, new AiQuery())
				.publishDtList(aiResponsesKey, new DtList<>(AiResponse.class))
				//---
				.toModeCreate();
	}

	@PostMapping("/_analyze")
	@ResponseBody
	public String doAnalyze(@RequestParam("text") final String text) {
		final String prePrompt = "En prennant en compte le texte suivant :\n'\n" + text + "\n'\n";

		final var response = new AiResponse();
		response.setName("TI_" + "%04d".formatted(RANDOM.nextInt(10000)));

		response.setDescription(llmManager.promptOnFiles(VPrompt.builder(
				prePrompt + "Décrit moi en 10 mots maximum la raison de l'appel. N'ajoute pas de détail non précisé, par exemple si le type de vehicule n'est pas précisé, ne le donne pas.").build())
				.getText());

		response.setPersons(llmManager.promptOnFiles(VPrompt.builder(
				prePrompt
						+ "Donne moi la référence du vehicule ou du conducteur. Une référence est sous la forme XXX_1234 (des majuscules, un underscore et des chiffres). Répond uniquement la référence sans autre texte ni mise en forme").build())
				.getText());

		response.setCategory(llmManager.promptOnFiles(VPrompt.builder(prePrompt +
				"A quelle catégorie correspond ce texte parmi PANNE, SIGNALISATION, BOUCHON, DEVIATION. Exemple, un clognotant en passe sera une PANNE. Répond uniquement ce mot en majuscule sans autre texte ni mise en forme").build())
				.getText());

		response.setTags(llmManager.promptOnFiles(VPrompt.builder(prePrompt +
				"Quel est le niveau d'urgence de la demande parmi 'URGENT', 'ELEVE', 'NORMAL', 'FAIBLE'. Par exemple une panne sera urgente si elle immobilise le vehicule et normale sinon tandi qu'un défaut de signalisation sur la route sera faiblement urgent. Les bouchons ont une priorité ELEVE. Répond uniquement un des mots suivants 'URGENT', 'ELEVE', 'NORMAL', 'FAIBLE', en majuscule, sans lettre supplémentaire, sans accents et sans autre texte ni mise en forme").build())
				.getText());

		response.setAddress(llmManager.promptOnFiles(VPrompt.builder(prePrompt +
				"Quel est le lieux de l'événement ? Répond uniquement le lieux sans autre texte ni mise en forme. Si aucun lieu n'est précisé,répond 'Centre de maintenance des véhicules'.").build())
				.getText());

		// simulation of location on examples
		if (response.getAddress().toLowerCase().contains("a86") && response.getAddress().toLowerCase().contains("sortie 30")) {
			response.setGps(new GeoPoint(2.2370166094852078, 48.776258879279105));
		} else if (response.getAddress().toLowerCase().contains("centre de maintenance des véhicules")) {
			response.setGps(new GeoPoint(2.4644850574641155, 48.568360146207034));
		} else if (response.getAddress().toLowerCase().contains("d86")) {
			response.setGps(new GeoPoint(2.4483158146348307, 48.78507442960254));
		} else if (response.getAddress().toLowerCase().contains("boulogne")) {
			response.setGps(new GeoPoint(2.242392650990452, 48.83331519833575));
		} else if (response.getAddress().toLowerCase().contains("audibert")) {
			response.setGps(new GeoPoint(-1.5494992219468262, 47.207921353589654));
		} else if (response.getAddress().toLowerCase().contains("périphérique")) {
			response.setGps(new GeoPoint(2.357045233176081, 48.90060071919572));
		} else if (response.getAddress().toLowerCase().contains("18 avenue de l'opéra")) {
			response.setGps(new GeoPoint(2.3345325189562893, 48.86578233588141));
		}
		return jsonEngine.toJson(response);
	}

}

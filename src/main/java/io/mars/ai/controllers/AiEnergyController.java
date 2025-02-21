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
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.ai.llm.model.VPrompt;
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
@RequestMapping("/ai/energy/")
public class AiEnergyController extends AbstractVSpringMvcController {

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private FileStoreManager fileStoreManager;
	@Inject
	private LlmManager llmManager;

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

		final var file = fileStoreManager.read(fileUri).getVFile();

		response.setName(llmManager.askOnFiles(
				VPrompt.builder("Donne moi le numéro de facture. Répond uniquement le numéro de facture sans autre texte ni mise en forme.").build(),
				file).getText());

		response.setDescription(llmManager.askOnFiles(
				VPrompt.builder("Donne moi la période de consommation concernée. Répond sur le format suivant 'Consommation du 01 janvier au 31 mars 2024' sans autre texte ni mise en forme.").build(),
				file).getText());

		response.setDescription2(llmManager.askOnFiles(
				VPrompt.builder("Quel est le montant de la facture hors taxes. Répond sur le format suivant '1 234,56 € (HT)' sans autre texte ni mise en forme.").build(),
				file).getText());

		final String dateString = llmManager.askOnFiles(VPrompt.builder(
				"A quelle date a été envoyée la facture ? Ne donne pas la date de consommation mais bien la date d'émission de la facture. Répond sous la forme 2007-12-23 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme. Si il est précisé un mois, donne le premier jour du mois. Si il est précisé un trimestre, donne le premier jour du trimestre.").build(),
				file).getText();
		if (!StringUtil.isBlank(dateString) && !"NA".equals(dateString)) {
			try {
				response.setDate(LocalDate.parse(dateString));
			} catch (final Exception e) {
				response.setDate(null);
			}
		}

		final String dateString2 = llmManager.askOnFiles(VPrompt.builder(
				"Quelle est la date limite de paiement de la facture ? répond sous la forme 2007-12-23 sans aucun autre texte. Si aucune date n'est précisée dans le document ou que cela n'est pas clair, répondre 'NA' sans autre texte ni mise en forme. Si il est précisé un mois, donne le premier jour du mois. Si il est précisé un trimestre, donne le premier jour du trimestre.").build(),
				file).getText();
		if (!StringUtil.isBlank(dateString2) && !"NA".equals(dateString2)) {
			try {
				response.setDate2(LocalDate.parse(dateString2));
			} catch (final Exception e) {
				response.setDate2(null);
			}
		}

		response.setCategory(llmManager.askOnFiles(VPrompt.builder(
				"Si le document est en rapport avec l'électricité, répond ELEC. S'il est en rapport avec le gaz, répond GAZ. Sinon répond INCONNU. Répond uniquement ce mot en majuscule sans autre texte ni mise en forme").build(),
				file).getText());

		/*
		response.setTags(llmManager.promptOnFiles(new VPrompt(
				"Quels sont les différents moyens de paiement acceptés ? Répond sous la forme 'tag1;tag2;tag3' sans autre texte ni mise en forme"),
				, file).getText());
		*/
		return jsonEngine.toJson(response);
	}

	@PostMapping("/_ask")
	public void doExtract(final ViewContext viewContext, @ViewAttribute("aiQuery") final AiQuery aiQuery) {

	}

}

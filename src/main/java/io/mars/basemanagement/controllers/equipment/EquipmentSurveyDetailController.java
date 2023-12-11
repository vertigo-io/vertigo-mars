package io.mars.basemanagement.controllers.equipment;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentSurvey;
import io.mars.basemanagement.services.equipment.EquipmentSurveyServices;
import io.mars.catalog.domain.EquipmentCategory;
import io.mars.catalog.services.equipment.EquipmentCategoryServices;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.easyforms.metaformulaire.domain.MetaFormulaire;
import io.vertigo.easyforms.metaformulaire.domain.ModeleFormulaire;
import io.vertigo.easyforms.metaformulaire.services.MetaFormulaireServices;
import io.vertigo.easyforms.metaformulaire.services.MetaFormulaireUiUtil;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("Equipment$read")
@RequestMapping("/basemanagement/equipment/{equipmentId}/survey")
public class EquipmentSurveyDetailController extends AbstractVSpringMvcController {

	@Inject
	private EquipmentSurveyServices equipmentSurveyServices;
	@Inject
	private EquipmentDetailController equipmentDetailController;
	@Inject
	private MetaFormulaireServices metaFormulaireServices;
	@Inject
	private EquipmentCategoryServices equipmentCategoryServices;

	private static final ViewContextKey<Boolean> hasSurvey = ViewContextKey.of("hasSurvey");
	private static final ViewContextKey<EquipmentSurvey> surveyKey = ViewContextKey.of("survey");
	private static final ViewContextKey<ModeleFormulaire> modeleFormulaireKey = ViewContextKey.of("modeleFormulaire");
	private static final ViewContextKey<String> controlMandatoryKey = ViewContextKey.of("controlMandatory");
	private static final ViewContextKey<MetaFormulaireUiUtil> mfoUiUtilKey = ViewContextKey.of("mfoUiUtil");

	@GetMapping("/{esuId}")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId, @PathVariable("esuId") final Long esuId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);

		viewContext
				.publishDto(surveyKey, equipmentSurveyServices.getById(esuId))
				//---
				.toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);
		viewContext.publishRef(mfoUiUtilKey, new MetaFormulaireUiUtil());

		final EquipmentCategory equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromEquipmentId(equipmentId);
		final UID<MetaFormulaire> mfoUid = equipmentCategory.metaFormulaire().getUID();

		if (mfoUid == null) {
			viewContext.publishRef(hasSurvey, false)
					.toModeReadOnly();
		} else {
			viewContext.publishRef(hasSurvey, true);

			final var metaFormulaire = metaFormulaireServices.getMetaFormulaireById(mfoUid);

			final var controlMandatory = metaFormulaire.getModele().getChamps().stream()
					.filter(c -> !c.isOptionel())
					.map(c -> ("!vueData.reservation.formulaire['" + c.getCodeChamp() + "'] || vueData.reservation.formulaire['" + c.getCodeChamp() + "'] == ''"))
					.collect(Collectors.joining(" || "));

			viewContext
					.publishDto(surveyKey, new EquipmentSurvey())
					.publishRef(modeleFormulaireKey, metaFormulaire.getModele())
					.publishRef(controlMandatoryKey, controlMandatory)
					//---
					.toModeCreate();
		}
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("survey") final EquipmentSurvey equipmentSurvey, @ViewAttribute("equipment") final Equipment equipment) {
		equipmentSurvey.setEquipmentId(equipment.getEquipmentId());
		equipmentSurveyServices.save(equipmentSurvey);
		return "redirect:/basemanagement/equipment/" + equipment.getEquipmentId() + "/surveys/";
	}

}

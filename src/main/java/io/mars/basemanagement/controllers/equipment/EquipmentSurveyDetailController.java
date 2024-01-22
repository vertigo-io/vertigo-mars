package io.mars.basemanagement.controllers.equipment;

import java.util.LinkedHashMap;

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
import io.mars.hr.domain.Person;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.easyforms.domain.EasyForm;
import io.vertigo.easyforms.easyformsrunner.model.EasyFormsTemplate;
import io.vertigo.easyforms.impl.easyformsdesigner.services.EasyFormsDesignerServices;
import io.vertigo.easyforms.impl.easyformsrunner.util.EasyFormsUiUtil;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@Secured("Equipment$read")
@RequestMapping("/basemanagement/equipment/{equipmentId}/survey")
public class EquipmentSurveyDetailController extends AbstractVSpringMvcController {

	@Inject
	private EquipmentSurveyServices equipmentSurveyServices;
	@Inject
	private EquipmentDetailController equipmentDetailController;
	@Inject
	private EasyFormsDesignerServices easyFormsDesignerServices;
	@Inject
	private EquipmentCategoryServices equipmentCategoryServices;

	private static final ViewContextKey<Boolean> hasSurvey = ViewContextKey.of("hasSurvey");

	// creation
	private static final ViewContextKey<EquipmentSurvey> surveyKey = ViewContextKey.of("survey");
	private static final ViewContextKey<EasyFormsTemplate> modeleFormulaireKey = ViewContextKey.of("modeleFormulaire");
	private static final ViewContextKey<EasyFormsUiUtil> efoUiUtilKey = ViewContextKey.of("efoUiUtil");

	// reading
	private static final ViewContextKey<LinkedHashMap<String, String>> surveyDisplayKey = ViewContextKey.of("surveyDisplay");
	private static final ViewContextKey<Person> surveyRespondantKey = ViewContextKey.of("respondant");

	@GetMapping("/{esuId}")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId, @PathVariable("esuId") final Long esuId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);
		final var easyFormsUiUtil = new EasyFormsUiUtil();

		final EquipmentCategory equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromEquipmentId(equipmentId);
		final UID<EasyForm> mfoUid = equipmentCategory.easyForm().getUID();
		final var easyForm = easyFormsDesignerServices.getEasyFormById(mfoUid);
		final var survey = equipmentSurveyServices.getById(esuId);

		final LinkedHashMap<String, String> surveyDisplay = easyFormsUiUtil.getEasyForm(easyForm.getTemplate(), survey.getFormulaire());

		viewContext
				.publishRef(hasSurvey, true)
				.publishRef(surveyDisplayKey, surveyDisplay)
				.publishDto(surveyRespondantKey, survey.person().get())
				//---
				.toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);
		viewContext.publishRef(efoUiUtilKey, new EasyFormsUiUtil());

		final EquipmentCategory equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromEquipmentId(equipmentId);
		final UID<EasyForm> mfoUid = equipmentCategory.easyForm().getUID();

		if (mfoUid == null) {
			viewContext.publishRef(hasSurvey, false)
					.toModeReadOnly();
		} else {
			final var easyForm = easyFormsDesignerServices.getEasyFormById(mfoUid);

			viewContext
					.publishRef(hasSurvey, true)
					.publishDto(surveyKey, new EquipmentSurvey())
					.publishRef(modeleFormulaireKey, easyForm.getTemplate())
					//---
					.toModeCreate();
		}
	}

	@PostMapping("/_save")
	public String doSave(final UiMessageStack uiMessageStack,
			@ViewAttribute("survey") final EquipmentSurvey equipmentSurvey, @ViewAttribute("equipment") final Equipment equipment,
			@ViewAttribute("modeleFormulaire") final EasyFormsTemplate modeleFormulaire) {

		equipmentSurvey.setEquipmentId(equipment.getEquipmentId());
		equipmentSurveyServices.save(uiMessageStack, equipmentSurvey, modeleFormulaire);
		return "redirect:/basemanagement/equipment/" + equipment.getEquipmentId() + "/surveys/";
	}

}

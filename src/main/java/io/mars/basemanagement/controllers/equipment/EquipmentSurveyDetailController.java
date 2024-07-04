package io.mars.basemanagement.controllers.equipment;

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
import io.mars.support.MarsUserSession;
import io.mars.support.util.SecurityUtil;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.easyforms.domain.EasyForm;
import io.vertigo.easyforms.impl.runner.controllers.EasyFormsRunnerController;
import io.vertigo.easyforms.runner.model.template.EasyFormsTemplate;
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
	private EquipmentCategoryServices equipmentCategoryServices;

	@Inject
	private EasyFormsRunnerController easyFormsRunnerController;

	private static final ViewContextKey<Boolean> hasSurvey = ViewContextKey.of("hasSurvey");
	private static final ViewContextKey<Person> userKey = ViewContextKey.of("user");

	// creation
	private static final ViewContextKey<EquipmentSurvey> surveyKey = ViewContextKey.of("survey");
	private static final ViewContextKey<EasyFormsTemplate> modeleFormulaireKey = ViewContextKey.of("modeleFormulaire");

	// reading
	private static final ViewContextKey<Person> surveyRespondantKey = ViewContextKey.of("respondant");

	@GetMapping("/{esuId}")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId, @PathVariable("esuId") final Long esuId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);

		final EquipmentCategory equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromEquipmentId(equipmentId);
		final UID<EasyForm> efoUid = equipmentCategory.easyForm().getUID();

		final var survey = equipmentSurveyServices.getById(esuId);

		viewContext
				.publishRef(hasSurvey, true)
				.publishDto(userKey, SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson())
				.publishDto(surveyKey, survey)
				.publishDto(surveyRespondantKey, survey.person().get())
				//---
				.toModeReadOnly();

		easyFormsRunnerController.initReadContext(viewContext, efoUid, modeleFormulaireKey);
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);

		final EquipmentCategory equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromEquipmentId(equipmentId);
		final UID<EasyForm> efoUid = equipmentCategory.easyForm().getUID();

		if (efoUid == null) {
			viewContext.publishRef(hasSurvey, false)
					.toModeReadOnly();
		} else {
			easyFormsRunnerController.initEditContext(viewContext, efoUid, modeleFormulaireKey);

			final var survey = new EquipmentSurvey();
			survey.setFormulaire(easyFormsRunnerController.getDefaultDataValues(viewContext, modeleFormulaireKey));

			viewContext
					.publishRef(hasSurvey, true)
					.publishDto(userKey, SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson())
					.publishDto(surveyKey, survey)
					//---
					.toModeCreate();
		}
	}

	@PostMapping("/_save")
	public String doSave(final ViewContext viewContext,
			@ViewAttribute("survey") final EquipmentSurvey equipmentSurvey,
			@ViewAttribute("equipment") final Equipment equipment,
			@ViewAttribute("modeleFormulaire") final EasyFormsTemplate modeleFormulaire) {

		easyFormsRunnerController.checkForm(modeleFormulaire, equipmentSurvey, EquipmentSurvey::getFormulaire);

		equipmentSurvey.setEquipmentId(equipment.getEquipmentId());
		equipmentSurveyServices.save(equipmentSurvey);
		return "redirect:/basemanagement/equipment/" + equipment.getEquipmentId() + "/surveys/";
	}

}

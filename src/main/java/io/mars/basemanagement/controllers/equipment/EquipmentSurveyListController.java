package io.mars.basemanagement.controllers.equipment;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentSurveyDisplay;
import io.mars.basemanagement.services.equipment.EquipmentSurveyServices;
import io.mars.domain.DtDefinitions.EquipmentSurveyFields;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("Equipment$read")
@RequestMapping("/basemanagement/equipment/{equipmentId}/surveys")
public class EquipmentSurveyListController extends AbstractVSpringMvcController {

	@Inject
	private EquipmentSurveyServices equipmentSurveyServices;
	@Inject
	private EquipmentDetailController equipmentDetailController;

	private static final ViewContextKey<EquipmentSurveyDisplay> surveys = ViewContextKey.of("surveys");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("equipmentId") final Long equipmentId) {
		//--
		equipmentDetailController.initCommonContext(viewContext, equipmentId);

		final DtListState dtListState = DtListState.of(200, 0, EquipmentSurveyFields.dateAnswer.name(), true);
		viewContext
				.publishDtList(surveys, equipmentSurveyServices.getListByEquipment(equipmentId, dtListState))
				//---
				.toModeReadOnly();
	}

	@PostMapping("/_sort")
	public ViewContext sort(final ViewContext viewContext, final DtListState dtListState, @ViewAttribute("equipment") final Equipment equipment) {
		viewContext.publishDtList(surveys, equipmentSurveyServices.getListByEquipment(equipment.getEquipmentId(), dtListState));
		return viewContext;
	}
}

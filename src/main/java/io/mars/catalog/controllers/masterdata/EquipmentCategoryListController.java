package io.mars.catalog.controllers.masterdata;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.mars.catalog.domain.EquipmentCategory;
import io.mars.catalog.services.equipment.EquipmentCategoryServices;
import io.mars.domain.DtDefinitions.EquipmentCategoryFields;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("AdmMasterData")
@RequestMapping("/catalog/masterdata/equipmentCategoryList/")
public class EquipmentCategoryListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<EquipmentCategory> categories = ViewContextKey.of("categories");

	@Inject
	private EquipmentCategoryServices equipmentCategoryServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @RequestParam("renderer") final Optional<String> renderer) {
		final DtListState dtListState = DtListState.of(200, 0, EquipmentCategoryFields.label.name(), false);
		viewContext
				.publishDtList(categories, equipmentCategoryServices.getEquipmentCategories(dtListState));
	}

	@PostMapping("/_sort")
	public ViewContext sort(final ViewContext viewContext, final DtListState dtListState) {
		viewContext.publishDtList(categories, equipmentCategoryServices.getEquipmentCategories(dtListState));
		return viewContext;
	}
}

package io.mars.catalog.controllers.referential;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.catalog.domain.EquipmentCategory;
import io.mars.catalog.services.equipment.EquipmentCategoryServices;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.easyforms.metaformulaire.controllers.AbstractFormsController;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@Secured("AdmReferential")
@RequestMapping("/catalog/referential/equipmentCategory/")
public class EquipmentCategoryDetailController extends AbstractFormsController {

	private static final ViewContextKey<EquipmentCategory> categoryKey = ViewContextKey.of("category");

	@Inject
	private EquipmentCategoryServices equipmentCategoryServices;

	@GetMapping("/{categoryId}")
	public void initContext(final ViewContext viewContext, @PathVariable("categoryId") final Long categoryId) {
		final var equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromId(categoryId);
		viewContext
				.publishDto(categoryKey, equipmentCategory)
				.toModeReadOnly();

		super.initContext(viewContext, Optional.ofNullable(equipmentCategory.metaFormulaire().getUID())); // init easyforms context
	}

	@PostMapping("/_edit")
	public void doEdit(final ViewContext viewContext) {
		viewContext.toModeEdit();
	}

	@PostMapping("/_cancel")
	public void doCancel(final ViewContext viewContext) {
		viewContext.toModeReadOnly();

	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("category") final EquipmentCategory category) {
		equipmentCategoryServices.saveEquipmentCategory(category);
		return "redirect:/catalog/referential/equipmentCategory/" + category.getEquipmentCategoryId();
	}

}

package io.mars.catalog.controllers.masterdata;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.catalog.domain.EquipmentCategory;
import io.mars.catalog.services.equipment.EquipmentCategoryServices;
import io.mars.support.MarsUserSession;
import io.mars.support.util.SecurityUtil;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.easyforms.designer.controllers.EasyFormsDesignerController;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("AdmMasterData")
@RequestMapping("/catalog/masterdata/equipmentCategory/")
public class EquipmentCategoryDetailController extends AbstractVSpringMvcController {

	private static final ViewContextKey<EquipmentCategory> categoryKey = ViewContextKey.of("category");

	@Inject
	private EquipmentCategoryServices equipmentCategoryServices;

	@Inject
	private EasyFormsDesignerController easyformsController;

	@GetMapping("/{categoryId}")
	public void initContext(final ViewContext viewContext, @PathVariable("categoryId") final Long categoryId) {
		final var equipmentCategory = equipmentCategoryServices.getEquipmentCategoryFromId(categoryId);
		viewContext
				.publishDto(categoryKey, equipmentCategory)
				.toModeReadOnly();

		easyformsController.initContext(viewContext,
				Optional.ofNullable(equipmentCategory.easyForm().getUID()),
				Map.of("connectedUser", SecurityUtil.<MarsUserSession>getUserSession().getLoggedPerson())); // init easyforms context
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
	public String doSave(final ViewContext viewContext, @ViewAttribute("category") final EquipmentCategory category) {
		equipmentCategoryServices.saveEquipmentCategory(category, () -> easyformsController.save(viewContext));
		return "redirect:/catalog/masterdata/equipmentCategory/" + category.getEquipmentCategoryId();
	}

}

package io.mars.basemanagement.controllers.equipment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/basemanagement/equipmentsMap")
public class EquipmentMapController extends AbstractVSpringMvcController {

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		//nothing
	}

}

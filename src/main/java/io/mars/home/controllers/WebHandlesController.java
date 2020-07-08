package io.mars.home.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.social.handle.HandleManager;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/hw")
public class WebHandlesController extends AbstractVSpringMvcController {

	@Inject
	private HandleManager handleManager;

	@GetMapping("/{definitionName}/{code}")
	public String forward(@PathVariable("definitionName") final String definitionName, @PathVariable("code") final String code) {
		Assertion.check()
				.isNotBlank(definitionName)
				.isNotBlank(code);
		//---
		final String handle = definitionName + "/" + code;
		final UID uid = handleManager.getHandleByCode(handle).getUid();
		switch (uid.getDefinition().getName()) {
			case "DtBase":
				return "redirect:/basemanagement/base/information/" + uid.getId();
			case "DtEquipment":
				return "redirect:/basemanagement/equipment/" + uid.getId();
			default:
				throw new VSystemException("handle {0} is not linkable to a web resource", handle);
		}

	}

}

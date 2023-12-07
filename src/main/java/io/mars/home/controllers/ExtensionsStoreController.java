package io.mars.home.controllers;

import java.io.Serializable;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.commons.app.AppManager;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/extensionsStore")
public class ExtensionsStoreController extends AbstractVSpringMvcController {

	@Inject
	private AppManager appManager;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishRef(() -> "skills", (Serializable) appManager.getCurrentNode().getSkills());
	}

}

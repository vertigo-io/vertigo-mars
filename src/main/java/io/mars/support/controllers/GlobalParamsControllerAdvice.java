/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2018, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mars.support.controllers;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import io.vertigo.core.param.ParamManager;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = { AbstractVSpringMvcController.class })
public final class GlobalParamsControllerAdvice {

	@Inject
	private ParamManager paramManager;

	@ModelAttribute
	public void initContext(final ViewContext viewContext, final HttpServletRequest request) {
		viewContext.publishRef(() -> "chatbotUrl", paramManager.getParam("chatbotUrl").getValueAsString());
		viewContext.publishRef(() -> "apiManagementUrl", paramManager.getParam("apiManagementUrl").getValueAsString());
		viewContext.publishRef(() -> "requestURI", request.getRequestURI());
	}

}

package io.mars.basemanagement.controllers.base;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.BaseType;
import io.mars.basemanagement.domain.Geosector;
import io.mars.basemanagement.services.base.BaseServices;
import io.mars.domain.DtDefinitions.BaseFields;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("ViewBases")
@RequestMapping("/basemanagement/bases")
public class BaseListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Base> bases = ViewContextKey.of("bases");
	private static final ViewContextKey<Geosector> geosectors = ViewContextKey.of("geosectors");
	private static final ViewContextKey<BaseType> baseTypes = ViewContextKey.of("baseTypes");

	@Inject
	private BaseServices baseServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		final DtListState dtListState = DtListState.of(200, 0, BaseFields.code.name(), false);
		viewContext
				.publishMdl(geosectors, Geosector.class, null) //all
				.publishMdl(baseTypes, BaseType.class, null) //all
				.publishDtList(bases, baseServices.getBases(dtListState));
	}

	@PostMapping("/_sort")
	public ViewContext sort(final ViewContext viewContext, final DtListState dtListState) {
		return viewContext
				.publishDtList(bases, baseServices.getBases(dtListState));
	}

}

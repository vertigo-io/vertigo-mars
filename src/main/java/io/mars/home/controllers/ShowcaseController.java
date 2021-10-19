package io.mars.home.controllers;

import java.time.Instant;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.BaseType;
import io.mars.basemanagement.domain.Geosector;
import io.mars.basemanagement.domain.Tag;
import io.mars.basemanagement.services.base.BaseServices;
import io.mars.domain.DtDefinitions.BaseFields;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.orchestra.monitoring.domain.uidefinitions.OProcessUi;
import io.vertigo.orchestra.monitoring.domain.uiexecutions.OProcessExecutionUi;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/showcase")
public class ShowcaseController extends AbstractVSpringMvcController {

	public static final ViewContextKey<Base> baseKey = ViewContextKey.of("base");
	public static final ViewContextKey<Base> basesKey = ViewContextKey.of("bases");
	public static final ViewContextKey<Base> emptyBaseKey = ViewContextKey.of("emptyBase");
	public static final ViewContextKey<BaseType> baseTypesKey = ViewContextKey.of("baseTypes");
	public static final ViewContextKey<OProcessUi> jobKey = ViewContextKey.of("job");
	public static final ViewContextKey<OProcessExecutionUi> jobExecutionKey = ViewContextKey.of("jobExecution");
	private static final ViewContextKey<Geosector> geosectorsKey = ViewContextKey.of("geosectors");
	private static final ViewContextKey<Tag> tagsKey = ViewContextKey.of("tags");

	@Inject
	private BaseServices baseServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack) {
		final Base base = baseServices.getBases(DtListState.of(1)).get(0);
		viewContext
				.publishDto(baseKey, base)
				.publishDtList(basesKey, baseServices.getBases(DtListState.of(20)));
		final Base emptyBase = new Base();
		viewContext
				.publishDto(emptyBaseKey, emptyBase)
				.publishMdl(baseTypesKey, BaseType.class, null) //all
				.publishDtList(geosectorsKey, baseServices.getAllGeosectors())
				.publishMdl(tagsKey, Tag.class, null); //all

		final OProcessUi processUi = new OProcessUi();
		processUi.setActive(Boolean.TRUE);
		viewContext.publishDto(jobKey, processUi);

		final OProcessExecutionUi processExecutionUi = new OProcessExecutionUi();
		processExecutionUi.setBeginTime(Instant.now());
		viewContext.publishDto(jobExecutionKey, processExecutionUi);

		//add error
		uiMessageStack.error("Some error message", emptyBase, BaseFields.assetsValue.name());

		toModeEdit();
	}

}

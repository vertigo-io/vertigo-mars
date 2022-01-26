package io.mars.basemanagement.controllers.base;

import java.time.Instant;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.basemanagement.domain.Event;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/planning/select")
public class SelectPlanningController extends AbstractVSpringMvcController {
	private static final ViewContextKey<Instant> todayDate = ViewContextKey.of("todayDate");
	private static final ViewContextKey<Event> events = ViewContextKey.of("events");
	private static final ViewContextKey<Event> selectedEvent = ViewContextKey.of("selectedEvent");

	@Inject
	private BaseDetailController baseDetailController;

	@GetMapping("/{baseId}")
	public void initContext(final ViewContext viewContext, @PathVariable("baseId") final Long baseId) {
		baseDetailController.initCommonContext(viewContext, baseId);
		viewContext.publishRef(todayDate, Instant.now());
		viewContext.publishDtList(events, new DtList<>(Event.class));
		viewContext.publishDto(selectedEvent, new Event());
		toModeReadOnly();
	}

}

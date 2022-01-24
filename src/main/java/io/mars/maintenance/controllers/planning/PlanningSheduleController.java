package io.mars.maintenance.controllers.planning;

import java.time.Instant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.hr.domain.Person;
import io.mars.maintenance.domain.Event;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/maintenance/planning/shedule")
public class PlanningSheduleController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Instant> todayDate = ViewContextKey.of("todayDate");
	private static final ViewContextKey<Event> events = ViewContextKey.of("events");
	private static final ViewContextKey<Person> resources = ViewContextKey.of("resources");

	@GetMapping("/{equipementId}")
	public void initContext(final ViewContext viewContext, @PathVariable("equipementId") final Long equipementId) {
		viewContext.publishRef(todayDate, Instant.now());
		viewContext.publishDtListModifiable(events, new DtList<>(Event.class));
		viewContext.publishDtListModifiable(resources, new DtList<>(Person.class));
		//---
		toModeReadOnly();
	}

}

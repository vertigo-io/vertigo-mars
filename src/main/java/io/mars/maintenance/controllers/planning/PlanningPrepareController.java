package io.mars.maintenance.controllers.planning;

import java.time.Instant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.maintenance.domain.Event;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/maintenance/planning/prepare")
public class PlanningPrepareController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Instant> todayDate = ViewContextKey.of("todayDate");
	private static final ViewContextKey<Event> events = ViewContextKey.of("events");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishRef(todayDate, Instant.now());
		//final Long personId = loginServices.getLoggedPerson().getPersonId();
		//final Long baseId = loginServices.getActiveProfile().getBaseId();

		//viewContext.publishDtListModifiable(events, planningServices.getEvents(baseId, personId));
		viewContext.publishDtListModifiable(events, new DtList<>(Event.class));
		//---
		toModeReadOnly();
	}

}

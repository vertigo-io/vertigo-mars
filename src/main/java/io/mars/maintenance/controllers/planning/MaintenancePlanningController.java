package io.mars.maintenance.controllers.planning;

import java.time.Instant;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.mars.hr.services.login.LoginServices;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.services.planning.PlanningServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/maintenance/planning")
public class MaintenancePlanningController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Instant> todayDate = ViewContextKey.of("todayDate");
	private static final ViewContextKey<Event> events = ViewContextKey.of("events");

	@Inject
	private PlanningServices planningServices;
	@Inject
	private LoginServices loginServices;

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

	@PostMapping("/_addFree")
	@ResponseBody
	public Event doAddFree(@RequestParam("dateTime") final Instant dateTime, @RequestParam("duration") final int duration) {
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final Long baseId = loginServices.getActiveProfile().getBaseId();
		final Event event = new Event();
		event.setDateTime(dateTime);
		event.setDurationMinutes(duration);
		event.setBaseId(baseId);
		event.setPersonId(personId);
		event.setEventStatusId("FREE");
		planningServices.addFreeEvent(event);
		return event;
	}

}

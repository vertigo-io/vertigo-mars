package io.mars.maintenance.controllers.planning;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.hr.services.login.LoginServices;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.services.planning.PlanningServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/maintenance/planning")
public class MaintenancePlanningController extends AbstractVSpringMvcController {

	private static final ViewContextKey<String> todayDate = ViewContextKey.of("todayDate");
	private static final ViewContextKey<Event> events = ViewContextKey.of("events");

	@Inject
	private PlanningServices planningServices;
	@Inject
	private LoginServices loginServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishRef(todayDate, "2022-01-21");
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final Long baseId = loginServices.getActiveProfile().getBaseId();

		viewContext.publishDtListModifiable(events, planningServices.getEvents(baseId, personId));
		//---
		toModeReadOnly();
	}

	@PostMapping("/_addFree")
	public String doAddFree(@ViewAttribute("event") final Event event) {
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final Long baseId = loginServices.getActiveProfile().getBaseId();
		event.setBaseId(baseId);
		event.setPersonId(personId);
		event.setEventStatusId("FREE");
		planningServices.addFreeEvent(event);
		return "OK";
	}

}

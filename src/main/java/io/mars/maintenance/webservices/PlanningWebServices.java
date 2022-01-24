package io.mars.maintenance.webservices;

import java.time.Instant;

import javax.inject.Inject;

import io.mars.hr.services.login.LoginServices;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.services.planning.PlanningServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;

public class PlanningWebServices implements WebServices {

	@Inject
	private PlanningServices planningServices;
	@Inject
	private LoginServices loginServices;

	@GET("/planning/event/my")
	public DtList<Event> getMyEvents() {
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final Long baseId = loginServices.getActiveProfile().getBaseId();
		return planningServices.getEvents(baseId, personId);
	}

	@POST("/planning/_addFree")
	public Event doAddFree(@InnerBodyParam("dateTime") final Instant dateTime, @InnerBodyParam("duration") final int duration) {
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

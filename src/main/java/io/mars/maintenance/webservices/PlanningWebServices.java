package io.mars.maintenance.webservices;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import io.mars.hr.domain.Person;
import io.mars.hr.services.login.LoginServices;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.services.planning.PlanningServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.DELETE;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;

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

	@POST("/planning/event/my/_addFree")
	public Event doAddFree(@InnerBodyParam("dateTime") final Instant dateTime, @InnerBodyParam("duration") final int duration) {
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final Long baseId = loginServices.getActiveProfile().getBaseId();
		final Event event = new Event();
		final ZonedDateTime zDateTime = dateTime.atZone(ZoneOffset.UTC);
		final ZonedDateTime lastQuarter = zDateTime.truncatedTo(ChronoUnit.HOURS)
				.plusSeconds(60 * 30 * (zDateTime.get(ChronoField.MINUTE_OF_HOUR) / 30));

		event.setDateTime(lastQuarter.toInstant());
		event.setDurationMinutes(duration);
		event.setBaseId(baseId);
		event.setPersonId(personId);
		event.setEventStatusId("FREE");
		planningServices.addFreeEvent(event);
		return event;
	}

	@DELETE("/planning/event/{baseId}/{eventId}")
	public Long doRemoveEvent(@PathParam("baseId") final Long baseId, @PathParam("eventId") final Long eventId) {
		planningServices.removeEvent(baseId, eventId);
		return eventId;
	}

	@GET("/planning/event/{baseId}/free")
	public DtList<Event> getFreeEvent(@PathParam("baseId") final Long baseId) {
		//final DtList<Person> persons = planningServices.getPerson(baseId);
		return planningServices.getFreeEvents(baseId);
	}

	@GET("/planning/resource/{baseId}")
	public DtList<Person> getResource(@PathParam("baseId") final Long baseId) {
		return planningServices.getPerson(baseId);
	}

}

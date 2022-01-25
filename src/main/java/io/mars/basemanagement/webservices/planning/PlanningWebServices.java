package io.mars.basemanagement.webservices.planning;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Event;
import io.mars.basemanagement.domain.EventStatusEnum;
import io.mars.basemanagement.services.planning.PlanningServices;
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

	@GET("/planning/event/{baseId}")
	public DtList<Event> getEvents(@PathParam("baseId") final Long baseId) {
		return planningServices.getEvents(baseId);
	}

	@POST("/planning/event/{baseId}/_addFree")
	public Event doAddFree(@PathParam("baseId") final Long baseId, @InnerBodyParam("dateTime") final Instant dateTime, @InnerBodyParam("duration") final int duration) {
		final Event event = new Event();
		final ZonedDateTime zDateTime = dateTime.atZone(ZoneOffset.UTC);
		final ZonedDateTime lastQuarter = zDateTime.truncatedTo(ChronoUnit.HOURS)
				.plusSeconds(60 * 30 * (zDateTime.get(ChronoField.MINUTE_OF_HOUR) / 30));

		event.setDateTime(lastQuarter.toInstant());
		event.setDurationMinutes(duration);
		event.setBaseId(baseId);
		event.eventStatus().setEnumValue(EventStatusEnum.free);
		planningServices.addFreeEvent(event);
		return event;
	}

	@POST("/planning/event/{baseId}/_select")
	public Event doSelect(@PathParam("baseId") final Long baseId, final Event event) {
		return planningServices.selectEvent(baseId, event);
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

}

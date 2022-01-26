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
import io.vertigo.vega.webservice.stereotype.ExcludedFields;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;

public class PlanningWebServices implements WebServices {

	@Inject
	private PlanningServices planningServices;

	@GET("/planning/event/{baseId}")
	@ExcludedFields({ "Base" })
	public DtList<Event> getEvents(@PathParam("baseId") final Long baseId) {
		return planningServices.getEvents(baseId);
	}

	@POST("/planning/event/{baseId}/_addFree")
	@ExcludedFields({ "Base" })
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
	@ExcludedFields({ "Base" })
	public Event doSelect(@PathParam("baseId") final Long baseId, final Event event) {
		return planningServices.selectEvent(baseId, event);
	}

	@POST("/planning/event/{baseId}/_unreserve")
	@ExcludedFields({ "Base" })
	public Event doUnReserve(@PathParam("baseId") final Long baseId, final Event event) {
		return planningServices.unReserveEvent(baseId, event);
	}

	@DELETE("/planning/event/{baseId}/{eventId}")
	public Long doRemoveEvent(@PathParam("baseId") final Long baseId, @PathParam("eventId") final Long eventId) {
		planningServices.removeEvent(baseId, eventId);
		return eventId;
	}

	@GET("/planning/event/{baseId}/free")
	@ExcludedFields({ "Base" })
	public DtList<Event> getFreeEvent(@PathParam("baseId") final Long baseId) {
		//final DtList<Person> persons = planningServices.getPerson(baseId);
		return planningServices.getFreeEvents(baseId);
	}

	@GET("/planning/event/{baseId}/my")
	@ExcludedFields({ "Base" })
	public DtList<Event> getMyEvent(@PathParam("baseId") final Long baseId) {
		//final DtList<Person> persons = planningServices.getPerson(baseId);
		final DtList<Event> events = planningServices.getMyEvents(baseId);
		events.addAll(planningServices.getFreeEvents(baseId));
		return events;
	}

	@GET("/planning/event/{baseId}/{eventId}/validate")
	@ExcludedFields({ "Base" })
	public Event doValidateEvent(@PathParam("baseId") final Long baseId, @PathParam("eventId") final Long eventId) {
		return planningServices.validateEvent(baseId, eventId);
	}

}

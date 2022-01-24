package io.mars.maintenance.services.planning;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.mars.hr.domain.Person;
import io.mars.hr.services.person.PersonServices;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.domain.EventStatusEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class PlanningServices implements Component {

	private long seqEvent = 0;
	private final Map<Long, DtList<Event>> eventStore = new HashMap<>();

	@Inject
	private PersonServices personServices;

	public DtList<Event> getFreeEvents(final Long baseId) {
		final DtList<Event> baseEvents = eventStore.get(baseId);
		return baseEvents.stream()
				.filter(e -> EventStatusEnum.free == e.eventStatus().getEnumValue())
				.collect(VCollectors.toDtList(Event.class));
	}

	public DtList<Event> getEvents(final Long baseId, final Long personId) {
		//eventStore.clear();
		if (eventStore.isEmpty()) {
			generateEvents(baseId);
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		return baseEvents.stream()
				.filter(e -> e.getPersonId().equals(personId))
				.collect(VCollectors.toDtList(Event.class));
	}

	public void addFreeEvent(final Event event) {
		final DtList<Event> baseEvents = eventStore.get(event.getBaseId());
		if (event.getEventId() == null) {
			event.setEventId(seqEvent++);
		}
		baseEvents.add(event);
	}

	public void removeEvent(final Long baseId, final Long eventId) {
		final DtList<Event> baseEvents = eventStore.get(baseId);
		baseEvents.removeIf(e -> e.getEventId().equals(eventId));
	}

	private void generateEvents(final Long baseId) {
		final DtList<Event> baseEvents = new DtList<>(Event.class);
		eventStore.put(baseId, baseEvents);
		for (int p = 0; p < 12; p++) {
			for (int d = 0; d < 15; d++) {
				final LocalDate day = LocalDate.now().plusDays(d);
				for (int n = 15; n < 15 + 21; n++) {
					if (Math.random() < 0.2) {
						final Event event = new Event();
						event.setEventId(seqEvent++);
						event.setBaseId(baseId);
						event.setPersonId(1000L + p);
						event.eventStatus().setEnumValue(EventStatusEnum.free);
						event.setDateTime(day.atTime(n / 2, n % 2 * 30).atZone(ZoneId.of("Europe/Paris")).toInstant());
						//event.setDurationMinutes(Math.random() < 0.7 ? 30 : 60);
						event.setDurationMinutes(59);
						n++;
						baseEvents.add(event);
					}
				}
			}
		}

	}

	public DtList<Person> getPerson(final Long baseId) {
		if (eventStore.isEmpty()) {
			generateEvents(baseId);
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		return baseEvents.stream()
				.filter(e -> "FREE".equals(e.getEventStatusId()))
				.map(e -> personServices.getPerson(e.getPersonId()))
				.collect(VCollectors.toDtList(Person.class));
	}

}

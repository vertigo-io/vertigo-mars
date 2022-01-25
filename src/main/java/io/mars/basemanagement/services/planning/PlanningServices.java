package io.mars.basemanagement.services.planning;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.domain.Event;
import io.mars.basemanagement.domain.EventStatusEnum;
import io.mars.domain.DtDefinitions;
import io.mars.hr.services.login.LoginServices;
import io.vertigo.account.account.Account;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;

@Transactional
public class PlanningServices implements Component {
	@Inject
	private LoginServices loginServices;
	@Inject
	private NotificationManager notificationManager;
	@Inject
	private SmartTypeManager smartTypeManager;

	private long seqEvent = 0;
	private final Map<Long, DtList<Event>> eventStore = new HashMap<>();

	public DtList<Event> getFreeEvents(final Long baseId) {
		if (!eventStore.containsKey(baseId)) {
			generateEvents(baseId);
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		return baseEvents.stream()
				.filter(e -> EventStatusEnum.free == e.eventStatus().getEnumValue())
				.collect(VCollectors.toDtList(Event.class));
	}

	public DtList<Event> getEvents(final Long baseId) {
		//eventStore.clear();
		if (!eventStore.containsKey(baseId)) {
			generateEvents(baseId);
		}
		return eventStore.get(baseId);
	}

	public void addFreeEvent(final Event event) {
		AuthorizationUtil.assertOperations(event, SecuredEntities.EventOperations.manage);
		final DtList<Event> baseEvents = eventStore.get(event.getBaseId());
		if (event.getEventId() == null) {
			event.setEventId(seqEvent++);
		}
		baseEvents.add(event);
	}

	public void removeEvent(final Long baseId, final Long eventId) {
		final DtList<Event> baseEvents = eventStore.get(baseId);
		baseEvents.removeIf(e -> {
			if (e.getEventId().equals(eventId)) {
				AuthorizationUtil.assertOperations(e, SecuredEntities.EventOperations.write);
				return true;
			}
			return false;
		});
	}

	private void generateEvents(final Long baseId) {
		final DtList<Event> baseEvents = new DtList<>(Event.class);
		eventStore.put(baseId, baseEvents);
		for (int d = 0; d < 15; d++) {
			final LocalDate day = LocalDate.now().plusDays(d);
			for (int n = 15; n < 15 + 21; n++) {
				if (Math.random() < 0.2) {
					final Event event = new Event();
					event.setEventId(seqEvent++);
					event.setBaseId(baseId);
					event.setDateTime(day.atTime(n / 2, n % 2 * 30).atZone(ZoneId.of("Europe/Paris")).toInstant());
					//event.setDurationMinutes(Math.random() < 0.7 ? 30 : 60);
					event.setDurationMinutes(59);
					if (Math.random() < 0.9) {
						event.eventStatus().setEnumValue(EventStatusEnum.free);
					} else {
						event.eventStatus().setEnumValue(EventStatusEnum.blocked);
					}
					n++;
					baseEvents.add(event);
				}
			}
		}
	}

	public Event selectEvent(final Long baseId, final Event selectedEvent) {
		AuthorizationUtil.assertOperations(selectedEvent, SecuredEntities.EventOperations.select);
		if (!selectedEvent.getBaseId().equals(baseId)) {
			throw new VUserException("This event is already reserved", null);
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		for (final Event event : baseEvents) {
			if (event.getEventId().equals(selectedEvent.getEventId())) {
				final Long personId = loginServices.getLoggedPerson().getPersonId();
				event.setAffectedLabel(loginServices.getLoggedPerson().getFullName());
				event.setAffectedType(selectedEvent.getAffectedType());
				event.setAffectedUrl("/x/accounts/api/" + personId + "/photo");
				event.setPersonId(personId);
				event.eventStatus().setEnumValue(EventStatusEnum.pending);

				//notification
				final String dateTime = smartTypeManager.valueToString(DtObjectUtil.findDtDefinition(event).getField(DtDefinitions.EventFields.dateTime).getSmartTypeDefinition(), event.getDateTime());
				event.base().load();
				final Notification notification = Notification.builder()
						.withTitle("Rendez vous " + dateTime)
						.withSender("Planning")
						.withType("calendar")
						.withTargetUrl(event.getUID().urn())
						.withContent(event.base().get().getName())
						.build();
				notificationManager.send(notification, Collections.singleton(UID.of(Account.class, String.valueOf(personId))));
				return event;
			}
		}
		return selectedEvent;
	}

}

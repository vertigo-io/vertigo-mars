package io.mars.basemanagement.services.planning;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import freemarker.template.utility.StringUtil;
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

	public DtList<Event> getMyEvents(final Long baseId) {
		if (!eventStore.containsKey(baseId)) {
			generateEvents(baseId);
		}
		final Long personId = loginServices.getLoggedPerson().getPersonId();
		final DtList<Event> baseEvents = eventStore.get(baseId);
		return baseEvents.stream()
				.filter(e -> personId.equals(e.getPersonId()))
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
		for (int d = 0; d < 21; d++) {
			final LocalDate day = LocalDate.now().plusDays(d);
			if (day.getDayOfWeek().equals(DayOfWeek.SUNDAY) || day.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
				continue;
			}
			for (int n = 17; n < 17 + 19; n++) {
				if (Math.random() < 0.2) {
					final Event event = new Event();
					event.setEventId(seqEvent++);
					event.setBaseId(baseId);
					event.setDateTime(day.atTime(n / 2, n % 2 * 30).atZone(ZoneId.of("Europe/Paris")).toInstant());
					//event.setDurationMinutes(Math.random() < 0.7 ? 30 : 60);
					event.setDurationMinutes(59);
					if (Math.random() < 0.9) {
						if (Math.random() < 0.5) {
							final long personId = Math.round(1000 + Math.random() * 10);
							event.setAffectedLabel(generateFullName());
							event.setAffectedType(null);
							event.setAffectedUrl("/x/accounts/api/" + personId + "/photo");
							event.setPersonId(personId);
							event.eventStatus().setEnumValue(EventStatusEnum.pending);
							event.eventStatus().setEnumValue(EventStatusEnum.reserved);
						} else {
							event.eventStatus().setEnumValue(EventStatusEnum.free);
						}
					} else {
						event.eventStatus().setEnumValue(EventStatusEnum.blocked);
					}
					n++;
					baseEvents.add(event);
				}
			}
		}
	}

	private static String generateFullName() {
		final String[] names = { "Martin", "Bernard", "Thomas", "Petit", "Robert", "Richard", "Durand", "Dubois", "Moreau", "Laurent", "Simon", "Michel", "Lefebvre", "Leroy", "Roux", "David", "Bertrand", "Morel", "Fournier", "Girard", "Bonnet", "Dupont", "Lambert", "Fontaine", "Rousseau", "Vincent", "Muller", "Lefevre", "Faure", "Andre", "Mercier", "Blanc", "Guerin", "Boyer", "Garnier", "Chevalier", "Francois", "Legrand", "Gauthier", "Garcia", "Perrin", "Robin", "Clement", "Morin", "Nicolas", "Henry", "Roussel", "Mathieu", "Gautier", "Masson", "Marchand", "Duval", "Denis", "Dumont", "Marie", "Lemaire", "Noel", "Meyer", "Dufour", "Meunier", "Brun", "Blanchard", "Giraud", "Joly", "Riviere", "Lucas", "Brunet", "Gaillard", "Barbier", "Arnaud", "Martinez", "Gerard", "Roche", "Renard", "Schmitt", "Roy", "Leroux", "Colin", "Vidal", "Caron", "Picard", "Roger", "Fabre", "Aubert", "Lemoine", "Renaud", "Dumas", "Lacroix", "Olivier", "Philippe", "Bourgeois", "Pierre", "Benoit", "Rey", "Leclerc",
				"Payet", "Rolland", "Leclercq", "Guillaume", "Lecomte", "Lopez", "Jean", "Dupuy", "Guillot", "Hubert", "Berger", "Carpentier", "Sanchez", "Dupuis", "Moulin", "Louis", "Deschamps", "Huet", "Vasseur", "Perez", "Boucher", "Fleury", "Royer", "Klein", "Jacquet", "Adam", "Paris", "Poirier", "Marty", "Aubry", "Guyot", "Carre", "Charles", "Renault", "Charpentier", "Menard", "Maillard", "Baron", "Bertin", "Bailly", "Herve", "Schneider", "Fernandez", "Le Gall", "Collet", "Leger", "Bouvier", "Julien", "Prevost", "Millet", "Perrot", "Daniel", "Le Roux", "Cousin", "Germain", "Breton", "Besson", "Langlois", "Remy", "Le Goff", "Pelletier", "Leveque", "Perrier", "Leblanc", "Barre", "Lebrun", "Marchal", "Weber", "Mallet", "Hamon", "Boulanger", "Jacob", "Monnier", "Michaud", "Rodriguez", "Guichard", "Gillet", "Etienne", "Grondin", "Poulain", "Tessier", "Chevallier", "Collin", "Chauvin", "Da Silva", "Bouchet", "Gay", "Lemaitre", "Benard", "Marechal", "Humbert", "Reynaud", "Antoine",
				"Hoarau", "Perret", "Barthelemy", "Cordier", "Pichon", "Lejeune", "Gilbert", "Lamy", "Delaunay", "Pasquier", "Carlier", "Laporte", };
		final String[] firstnames = { "Marie", "thomas", "camille", "nicolas", "léa", "julien", "manon", "quentin", "chloé", "maxime", "laura", "alexandre", "julie", "antoine", "sarah", "kevin", "pauline", "clement", "mathilde", "romain", "marine", "pierre", "emma", "lucas", "marion", "florian", "lucie", "guillaume", "anaïs", "valentin", "océane", "jérémy", "justine", "hugo", "morgane", "alexis", "clara", "anthony", "charlotte", "theo", "juliette", "paul", "emilie", "mathieu", "lisa", "benjamin", "mélanie", "adrien", "elodie", "vincent", "claire", "alex", "inès", "arthur", "margaux", "louis", "alice", "baptiste", "amandine", "dylan", "audrey", "corentin", "louise", "thibault", "noémie", "jordan", "clémence", "nathan", "maéva", "simon", "melissa", "axel", "amélie", "matthieu", "eva", "léo", "caroline", "sebastien", "céline", "aurélien", "célia", "victor", "fanny", "loïc", "elise", "rémi", "sophie", "arnaud", "margot", "tom", "elisa", "david", "aurélie", "jonathan", "jade", "damien",
				"estelle", "enzo", "romane", "bastien", "jeanne", "raphael", "ophélie", "mickael", "laurine", "françois", "alexandra", "robin", "valentine", "martin", "solène", "dorian", "lola", "gabriel", "coralie", "tristan", "laëtitia", "mathis", "alexia", "samuel", "aurore", "thibaut", "cécile", "charles", "alicia", "benoit", "zoé", "fabien", "agathe", "florent", "julia", "maxence", "anna", "cédric", "emeline", "marc", "léna", "yann", "laurie", "jérôme", "lou", "steven", "nina", "mehdi", "coline", "gaëtan", "jessica", "erwan", "maëlle", "cyril", "elsa", "jean", "lucile", "max", "laure", "rémy", "salomé", "yanis", "axelle", "tony", "andréa", "jules", "charlène", "william", "gaelle", "olivier", "helene", "laurent", "clementine", "christopher", "victoria", "sylvain", "myriam", "ludovic", "éloïse", "xavier", "heloise", "stephane", "cindy", "tanguy", "marina", "mael", "cassandra", "morgan", "sara", "adam", "carla", "franck", "ambre", "grégory", "ludivine", "christophe", "anaelle",
				"alan", "sabrina", "antonin", "angélique", "mohamed", "sandra", "philippe" };
		final String name = names[(int) Math.floor(Math.random() * names.length)].toUpperCase();
		final String firstname = StringUtil.capitalize(firstnames[(int) Math.floor(Math.random() * firstnames.length)]);
		return name + " " + firstname;
	}

	public Event selectEvent(final Long baseId, final Event selectedEvent) {
		AuthorizationUtil.assertOperations(selectedEvent, SecuredEntities.EventOperations.select);
		if (!selectedEvent.getBaseId().equals(baseId)) {
			throw new VUserException("This event is already reserved");
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		for (final Event event : baseEvents) {
			if (event.getEventId().equals(selectedEvent.getEventId())) {
				final Long personId = loginServices.getLoggedPerson().getPersonId();
				if (selectedEvent.getAffectedLabel() == null) {
					event.setAffectedLabel(loginServices.getLoggedPerson().getFullName());
				} else {
					event.setAffectedLabel(selectedEvent.getAffectedLabel());
				}
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
						.withType("Calendar")
						.withTargetUrl("/mars/planning/select/" + baseId + "/validate/" + selectedEvent.getEventId()) //ajouter le lien pour valider le rendezvous
						.withContent(event.getAffectedLabel() + " - " + event.base().get().getName())
						.build();
				notificationManager.send(notification, Collections.singleton(UID.of(Account.class, String.valueOf(personId))));
				return event;
			}
		}
		return selectedEvent;
	}

	public Event unReserveEvent(final Long baseId, final Event selectedEvent) {
		AuthorizationUtil.assertOperations(selectedEvent, SecuredEntities.EventOperations.write);
		if (!selectedEvent.getBaseId().equals(baseId)) {
			throw new VUserException("Event altered");
		}
		final DtList<Event> baseEvents = eventStore.get(baseId);
		for (final Event event : baseEvents) {
			if (event.getEventId().equals(selectedEvent.getEventId())) {
				final Long personId = loginServices.getLoggedPerson().getPersonId();
				event.setAffectedLabel(null);
				event.setAffectedType(null);
				event.setAffectedUrl(null);
				event.setPersonId(null);
				event.eventStatus().setEnumValue(EventStatusEnum.free);

				notificationManager.removeAll("Calendar", "/mars/planning/select/" + baseId + "/validate/" + selectedEvent.getEventId());
				notificationManager.removeAll("Calendar", "#event/" + baseId + "/" + selectedEvent.getEventId() + "/reserved");

				//notification
				final String dateTime = smartTypeManager.valueToString(DtObjectUtil.findDtDefinition(event).getField(DtDefinitions.EventFields.dateTime).getSmartTypeDefinition(), event.getDateTime());
				event.base().load();
				final Notification notification = Notification.builder()
						.withTitle("Rendez vous " + dateTime + " ANNULE")
						.withSender("Planning")
						.withType("Calendar")
						.withTargetUrl("/mars/planning/select/" + baseId)
						.withContent(event.base().get().getName())
						.withTTLInSeconds(60)
						.build();
				notificationManager.send(notification, Collections.singleton(UID.of(Account.class, String.valueOf(personId))));
				return event;
			}
		}
		return selectedEvent;
	}

	public Event validateEvent(final Long baseId, final Long eventId) {
		final DtList<Event> baseEvents = eventStore.get(baseId);
		for (final Event event : baseEvents) {
			if (event.getEventId().equals(eventId)) {
				final Long personId = loginServices.getLoggedPerson().getPersonId();
				event.eventStatus().setEnumValue(EventStatusEnum.reserved);

				//notification
				final String dateTime = smartTypeManager.valueToString(DtObjectUtil.findDtDefinition(event).getField(DtDefinitions.EventFields.dateTime).getSmartTypeDefinition(), event.getDateTime());
				event.base().load();
				notificationManager.removeAll("Calendar", "/mars/planning/select/" + baseId + "/validate/" + eventId);

				final Notification notification = Notification.builder()
						.withTitle("Rendez vous " + dateTime + " reserved")
						.withSender("Planning")
						.withType("Calendar")
						.withTargetUrl("#event/" + baseId + "/" + eventId + "/reserved")
						.withContent(event.getAffectedLabel() + " - " + event.base().get().getName())
						.build();
				notificationManager.send(notification, Collections.singleton(UID.of(Account.class, String.valueOf(personId))));
				return event;
			}
		}
		throw new VUserException("Event not found");
	}

}

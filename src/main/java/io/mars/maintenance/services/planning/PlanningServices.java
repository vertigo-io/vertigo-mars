package io.mars.maintenance.services.planning;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.services.equipment.EquipmentServices;
import io.mars.domain.DtDefinitions.TicketFields;
import io.mars.maintenance.dao.TicketDAO;
import io.mars.maintenance.domain.Event;
import io.mars.maintenance.domain.Ticket;
import io.mars.maintenance.domain.TicketStatusEnum;
import io.mars.maintenance.services.ticket.TicketEvent;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.eventbus.EventBusManager;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class PlanningServices implements Component {

	@Inject
	private TicketDAO ticketDAO;
	@Inject
	private VTransactionManager transactionManager;
	@Inject
	private EventBusManager eventBusManager;
	@Inject
	private EquipmentServices equipmentServices;

	public Ticket getTicketFromIdWithEquipment(final Long ticketId) {
		final Ticket ticket = ticketDAO.get(ticketId);
		AuthorizationUtil.assertOperationsWithLoadIfNeeded(ticket.equipment(), SecuredEntities.EquipmentOperations.readTickets);
		//---
		return ticket;
	}

	public void updateTicket(final Ticket ticket) {
		Assertion.check().isNull(ticket.getTicketId(), "Id must be provided for updating a ticket");
		//---
		AuthorizationUtil.assertOperationsWithLoadIfNeeded(ticket.equipment(), SecuredEntities.EquipmentOperations.editTickets);
		//---
		ticketDAO.update(ticket);
	}

	public void createTicket(final Ticket ticket) {
		Assertion.check().isNull(ticket.getTicketId(), "No id should be provided for a new ticket");
		//---
		AuthorizationUtil.assertOperationsWithLoadIfNeeded(ticket.equipment(), SecuredEntities.EquipmentOperations.readTickets);
		//---
		ticket.setDateCreated(LocalDate.now());
		ticket.ticketStatus().setEnumValue(TicketStatusEnum.open);
		ticketDAO.save(ticket);
		transactionManager.getCurrentTransaction()
				.addAfterCompletion(txCommited -> {
					if (txCommited) {
						eventBusManager.post(new TicketEvent(TicketEvent.Type.CREATE, ticket));
					}
				});
	}

	public void closeTicket(final Ticket ticket) {
		AuthorizationUtil.assertOperationsWithLoadIfNeeded(ticket.equipment(), SecuredEntities.EquipmentOperations.editTickets);
		//---
		ticket.ticketStatus().setEnumValue(TicketStatusEnum.closed);
		ticketDAO.update(ticket);
	}

	public DtList<Ticket> getLastestTicketsByBase(final Long baseId) {
		Assertion.check().isNotNull(baseId);
		//---
		return ticketDAO.getLastTicketsByBaseId(baseId,
				AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.readTickets));
	}

	public DtList<Ticket> getLastestTickets() {
		return ticketDAO.getLastTickets(AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.readTickets));
	}

	public DtList<Ticket> getOpenedTicketsByEquipment(final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//---
		final Equipment equipment = equipmentServices.get(equipmentId);
		AuthorizationUtil.assertOperations(equipment, SecuredEntities.EquipmentOperations.readTickets);
		//---
		return ticketDAO.findAll(
				Criterions.isEqualTo(TicketFields.equipmentId, equipmentId)
						.and(Criterions.isNotEqualTo(TicketFields.ticketStatusId, (String) TicketStatusEnum.closed.getEntityUID().getId())),
				DtListState.defaultOf(Ticket.class));
	}

	public DtList<Ticket> getClosedTicketsByEquipment(final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//---
		final Equipment equipment = equipmentServices.get(equipmentId);
		AuthorizationUtil.assertOperations(equipment, SecuredEntities.EquipmentOperations.readTickets);
		//---
		return ticketDAO.findAll(
				Criterions.isEqualTo(TicketFields.equipmentId, equipmentId)
						.and(Criterions.isEqualTo(TicketFields.ticketStatusId, (String) TicketStatusEnum.closed.getEntityUID().getId())),
				DtListState.defaultOf(Ticket.class));
	}

	private long seqEvent = 0;
	private final Map<Long, DtList<Event>> eventStore = new HashMap<>();

	public DtList<Event> getEvents(final Long baseId, final Long personId) {
		eventStore.clear();
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
		baseEvents.add(event);
	}

	private void generateEvents(final Long baseId) {
		final DtList<Event> baseEvents = new DtList<>(Event.class);
		eventStore.put(baseId, baseEvents);
		for (int p = 0; p < 12; p++) {
			for (int d = 0; d < 15; d++) {
				final LocalDate day = LocalDate.now().plusDays(d);
				for (int n = 15; n < 15 + 22; n++) {
					if (Math.random() < 0.2) {
						final Event event = new Event();
						event.setEventId(seqEvent++);
						event.setBaseId(baseId);
						event.setPersonId(1000L + p);
						event.setEventStatusId("FREE");
						event.setDateTime(day.atTime(n / 2, n % 2 * 30).atZone(ZoneId.of("Europe/Paris")).toInstant());
						event.setDurationMinutes(Math.random() < 0.7 ? 30 : 60);
						baseEvents.add(event);
					}
				}
			}
		}

	}

}

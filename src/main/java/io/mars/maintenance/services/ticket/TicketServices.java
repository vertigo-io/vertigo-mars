package io.mars.maintenance.services.ticket;

import java.time.LocalDate;

import javax.inject.Inject;

import io.mars.domain.DtDefinitions.TicketFields;
import io.mars.maintenance.dao.TicketDAO;
import io.mars.maintenance.domain.Ticket;
import io.mars.maintenance.domain.TicketStatusEnum;
import io.vertigo.commons.eventbus.EventBusManager;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class TicketServices implements Component {

	@Inject
	private TicketDAO ticketDAO;
	@Inject
	private VTransactionManager transactionManager;
	@Inject
	private EventBusManager eventBusManager;

	public Ticket getTicketFromId(final Long ticketId) {
		return ticketDAO.get(ticketId);
	}

	public void save(final Ticket ticket) {
		ticketDAO.save(ticket);
	}

	public void createTicket(final Ticket ticket) {
		Assertion.check().isNull(ticket.getTicketId(), "No id should be provided for a new ticket");
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
		ticket.ticketStatus().setEnumValue(TicketStatusEnum.closed);
		ticketDAO.update(ticket);
	}

	public DtList<Ticket> getLastestTicketsByBase(final Long baseId) {
		Assertion.check().isNotNull(baseId);
		//---
		return ticketDAO.getLastTicketsByBaseId(baseId);
	}

	public DtList<Ticket> getLastestTickets() {
		return ticketDAO.getLastTickets();
	}

	public DtList<Ticket> getOpenedTicketsByEquipment(final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//---
		return ticketDAO.findAll(
				Criterions.isEqualTo(TicketFields.equipmentId, equipmentId)
						.and(Criterions.isNotEqualTo(TicketFields.ticketStatusId, (String) TicketStatusEnum.closed.getEntityUID().getId())),
				DtListState.defaultOf(Ticket.class));
	}

	public DtList<Ticket> getClosedTicketsByEquipment(final Long equipmentId) {
		Assertion.check().isNotNull(equipmentId);
		//---
		return ticketDAO.findAll(
				Criterions.isEqualTo(TicketFields.equipmentId, equipmentId)
						.and(Criterions.isEqualTo(TicketFields.ticketStatusId, (String) TicketStatusEnum.closed.getEntityUID().getId())),
				DtListState.defaultOf(Ticket.class));
	}

}

package io.mars.maintenance.services.ticket;

import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import io.mars.maintenance.domain.Ticket;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.connectors.ifttt.IftttClient;
import io.vertigo.connectors.ifttt.IftttConnector;
import io.vertigo.connectors.ifttt.MakerEvent;
import io.vertigo.core.node.component.Component;

public class SlackTicketEventSubscriber implements Component {

	private final IftttClient iftttClient;

	@Inject
	public SlackTicketEventSubscriber(final IftttConnector iftttConnector) {
		iftttClient = iftttConnector.getClient();
	}

	@EventBusSubscribed
	public void onTicketEvent(final TicketEvent ticketEvent) {
		if (ticketEvent.getType() == TicketEvent.Type.CREATE) {
			final Ticket ticket = ticketEvent.getTicket();

			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");

			final MakerEvent event = new MakerEvent("mars_ticket_created");

			// Put the ticket code in IFTTT value1
			event.getEventMetadatas().setValue1(ticket.getCode());
			// Put the tocket title in IFTTT value2
			event.getEventMetadatas().setValue2(ticket.getTitle());
			// Put the ticket creation date in IFTTT value 3
			event.getEventMetadatas().setValue3(ticket.getDateCreated().format(formatter));

			iftttClient.sendMakerEvent(event);
		}
	}

}

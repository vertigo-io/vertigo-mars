package io.mars.maintenance.services.ticket;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.mars.hr.services.person.PersonServices;
import io.mars.maintenance.domain.Ticket;
import io.vertigo.account.account.Account;
import io.vertigo.audit.ledger.LedgerManager;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;

public class BlockchainTicketEventSubscriber implements Component {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Inject
	private LedgerManager ledgerManager;

	@Inject
	private PersonServices personServices;

	@Inject
	private NotificationManager notificationManager;

	@EventBusSubscribed
	public void onTicketEvent(final TicketEvent ticketEvent) {
		if (ticketEvent.getType() == TicketEvent.Type.CREATE) {
			final Ticket ticket = ticketEvent.getTicket();

			final String strDateCreated = ticket.getDateCreated().format(FORMATTER);

			final StringBuilder sbSerializedTicket = new StringBuilder();
			sbSerializedTicket.append("Creating ticket ")
					.append(ticket.getCode())
					.append('.')
					.append(ticket.getTitle())
					.append(". Ticket created at ")
					.append(strDateCreated);

			final String message = sbSerializedTicket.toString();
			ledgerManager.sendDataAsync(message, () -> {
				final Notification notification = Notification.builder()
						.withSender("System")
						.withTitle("New ticket sucessfully written to the ledger")
						.withContent(message)
						.withTTLInSeconds(600)
						.withType("MARS-TICKET-LEDGER") //should prefix by app, in case of multi-apps notifications
						.withTargetUrl("#")
						.build();
				sendNotificationToAll(notification);
			});

			final Notification notification = Notification.builder()
					.withSender("System")
					.withTitle("Writing new ticket to the ledger")
					.withContent(sbSerializedTicket.toString())
					.withTTLInSeconds(600)
					.withType("MARS-TICKET-LEDGER") //should prefix by app, in case of multi-apps notifications
					.withTargetUrl("#")
					.build();
			sendNotificationToAll(notification);
		}
	}

	private void sendNotificationToAll(final Notification notification) {
		final Set<UID<Account>> accountUIDs = personServices.getPersons(DtListState.of(null))
				.stream()
				.map((person) -> UID.of(Account.class, String.valueOf(person.getPersonId())))
				.collect(Collectors.toSet());
		notificationManager.send(notification, accountUIDs);
	}

}

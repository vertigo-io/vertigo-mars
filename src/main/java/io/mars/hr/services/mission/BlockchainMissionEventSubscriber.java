package io.mars.hr.services.mission;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Business;
import io.mars.hr.domain.Mission;
import io.mars.hr.domain.Person;
import io.mars.hr.services.person.PersonServices;
import io.vertigo.audit.ledger.LedgerManager;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.core.node.component.Component;
import io.vertigo.social.notification.Notification;
import io.vertigo.social.notification.NotificationManager;

public class BlockchainMissionEventSubscriber implements Component {

	@Inject
	private LedgerManager ledgerManager;

	@Inject
	private PersonServices personServices;
	@Inject
	private NotificationManager notificationManager;

	@EventBusSubscribed
	public void onTicketEvent(final MissionEvent missionEvent) {
		final Mission mission = missionEvent.getMission();
		final Base base = mission.base().get();
		final Person person = mission.person().get();
		final Business business = mission.business().get();
		final StringBuilder sbSerializedTicket = new StringBuilder();
		sbSerializedTicket.append(person.getFullName())
				.append(" has been affected to base ")
				.append(base.getName())
				.append(" with the role ")
				.append(mission.role().get().getLabel())
				.append(" for the company ")
				.append(business.getName());

		final String message = sbSerializedTicket.toString();
		ledgerManager.sendDataAsync(message, () -> {
			final Notification notification = Notification.builder()
					.withSender("System")
					.withTitle("New assignement sucessfully written to the ledger")
					.withContent(message)
					.withTTLInSeconds(600)
					.withType("MARS-MISSION-LEDGER") //should prefix by app, in case of multi-apps notifications
					.withTargetUrl("#")
					.build();
			sendNotificationToAll(notification);
		});

		final Notification notification = Notification.builder()
				.withSender("System")
				.withTitle("Writing new assignement to the ledger")
				.withContent(sbSerializedTicket.toString())
				.withTTLInSeconds(600)
				.withType("MARS-MISSION-LEDGER") //should prefix by app, in case of multi-apps notifications
				.withTargetUrl("#")
				.build();
		sendNotificationToAll(notification);
	}

	private void sendNotificationToAll(final Notification notification) {
		notificationManager.send(notification, personServices.getAllPersonsUID());
	}

}

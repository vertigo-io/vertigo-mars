package io.mars.maintenance.services.workorder;

import javax.inject.Inject;

import io.mars.maintenance.domain.WorkOrder;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.connectors.ifttt.IftttClient;
import io.vertigo.connectors.ifttt.MakerEvent;
import io.vertigo.core.node.component.Component;

public class TrelloWorkOrderEventSubscriber implements Component {
	private final IftttClient iftttClient;

	@Inject
	public TrelloWorkOrderEventSubscriber(final IftttClient iftttClient) {
		this.iftttClient = iftttClient;
	}

	@EventBusSubscribed
	public void onTicketEvent(final WorkOrderEvent workOrderEvent) {
		if (workOrderEvent.getType() == WorkOrderEvent.Type.CREATE) {
			final WorkOrder workOrder = workOrderEvent.getWorkOrder();

			final MakerEvent event = new MakerEvent("work_order_created");

			event.getEventMetadatas().setValue1(workOrder.getCode());
			event.getEventMetadatas().setValue2(workOrder.getName());
			event.getEventMetadatas().setValue3(workOrder.getDescription());

			iftttClient.sendMakerEvent(event);

		}
	}

}

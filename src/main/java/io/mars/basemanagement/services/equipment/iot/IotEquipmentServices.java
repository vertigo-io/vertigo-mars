package io.mars.basemanagement.services.equipment.iot;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.commons.eventbus.EventBusManager;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TimeSeriesManager;

public class IotEquipmentServices implements Component {
	private static final Logger LOGGER = LogManager.getLogger(IotEquipmentServices.class);

	@Inject
	private EventBusManager eventBusManager;
	@Inject
	private TimeSeriesManager timeSeriesManager;

	@EventBusSubscribed
	public void onInput(final InputEvent inputEvent) {
		Assertion.check().isNotNull(inputEvent);
		//---
		LOGGER.info("Actuator is triggered {}", inputEvent.getType());

		final OutputEvent outputEvent;
		if (inputEvent.getPayloadOpt().isPresent()) {
			outputEvent = new OutputEvent(inputEvent.getType(), inputEvent.getTopic(), inputEvent.getPayloadOpt().get());
		} else {
			outputEvent = new OutputEvent(inputEvent.getType(), inputEvent.getTopic());
		}

		eventBusManager.post(outputEvent);
	}

	@EventBusSubscribed
	public void onMeasure(final MeasureEvent measureEvent) {
		Assertion.check().isNotNull(measureEvent);
		//---
		timeSeriesManager.insertMeasure("mars-test", measureEvent.getMeasure());
		LOGGER.info("Added measure to mars-test");
	}
}

package io.mars.basemanagement.services.equipment.iot;

import io.vertigo.commons.eventbus.Event;
import io.vertigo.core.lang.Assertion;
import io.vertigo.database.timeseries.Measure;

/**
 * This class defines the event that is emitted when an measure is sent.
 *
 * @author rfelten
 */
public final class MeasureEvent implements Event {
	private final Measure measure;

	/**
	 * Constructor.
	 * @param measure the measure
	 */
	public MeasureEvent(final Measure measure) {
		Assertion.check().isNotNull(measure);
		//-----
		this.measure = measure;
	}

	public Measure getMeasure() {
		return measure;
	}
}

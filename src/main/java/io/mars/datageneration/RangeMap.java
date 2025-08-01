package io.mars.datageneration;

import java.util.HashMap;
import java.util.Map;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;

final class RangeMap<T extends Comparable<T>, V> {

	private final Map<MyRange<T>, V> myMap = new HashMap<>();

	public void addSegment(final T minValue, final T maxValue, final V value) {
		Assertion.check()
				.isNotNull(minValue)
				.isNotNull(maxValue)
				.isNotNull(value);
		//
		final MyRange<T> myRange = new MyRange<>(minValue, maxValue);
		if (isOverlappingExistingSegments(myRange)) {
			throw new IllegalArgumentException("Range " + minValue + "-" + maxValue + " is overlapping previously added segments");
		}
		myMap.put(myRange, value);
	}

	public V getValue(final T pointInKey) {
		Assertion.check().isNotNull(pointInKey);
		//
		final MyRange<T> goodRange = myMap.keySet().stream()
				.filter(range -> pointInKey.compareTo(range.getMinValue()) >= 0 && pointInKey.compareTo(range.getMaxValue()) < 0)
				.findFirst()
				.orElseThrow(() -> new VSystemException("Unable to find enclosing range for value {0}", pointInKey));

		return myMap.get(goodRange);

	}

	private boolean isOverlappingExistingSegments(final MyRange<T> range) {
		for (final MyRange<T> rangeCursor : myMap.keySet()) {
			if (range.isIntersecting(rangeCursor)) {
				return true;
			}
		}
		return false;
	}

	private class MyRange<R extends Comparable<R>> {
		private final R myMinValue;
		private final R myMaxValue;

		public MyRange(final R minValue, final R maxValue) {
			Assertion.check()
					.isNotNull(minValue)
					.isNotNull(maxValue)
					.isTrue(minValue.compareTo(maxValue) < 0, "The range minValue must be strictly inferior to maxValue");
			//
			myMinValue = minValue;
			myMaxValue = maxValue;
		}

		public R getMinValue() {
			return myMinValue;
		}

		public R getMaxValue() {
			return myMaxValue;
		}

		public boolean isIntersecting(final MyRange<R> connectedRange) {
			final int lowerCmp = myMinValue.compareTo(connectedRange.myMinValue);
			final int upperCmp = myMaxValue.compareTo(connectedRange.myMaxValue);
			final int lowerToHigerCmp = myMinValue.compareTo(connectedRange.myMaxValue);
			final int higherToLowerCmp = myMaxValue.compareTo(connectedRange.myMinValue);

			if (lowerCmp <= 0) {
				// this.myMinValue < connectedRange.myMinvalue
				if (upperCmp >= 0 || higherToLowerCmp > 0) {
					return true;
				}
			} else {
				// connectedRange.myMinvalue < this.myMinValue
				if (upperCmp <= 0 || lowerToHigerCmp < 0) {
					return true;
				}
			}
			return false;
		}
	}
}

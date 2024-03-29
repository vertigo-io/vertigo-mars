package io.mars.basemanagement.services.equipment.iot;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.vertigo.commons.eventbus.EventBusManager;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.connectors.mqtt.MosquittoConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.config.discovery.NotDiscoverable;
import io.vertigo.database.timeseries.Measure;

@NotDiscoverable
public class MqttShield implements Component {

	private static final Logger LOGGER = LogManager.getLogger(MqttShield.class);

	@Inject
	private EventBusManager eventBusManager;

	private final Map<String, MosquittoConnector> connectorsByName = new HashMap<>();

	private MqttClient mqttClient;
	private MqttClient mqttClientPub;

	@Inject
	public MqttShield(final List<MosquittoConnector> mqttConnectors) {
		for (final MosquittoConnector mqttConnector : mqttConnectors) {
			final String name = mqttConnector.getName();
			final MosquittoConnector previous = connectorsByName.put(name, mqttConnector);
			Assertion.check().isNull(previous, "MqttConnector {0}, was already registered", name);
		}

		try {
			mqttClient = connectorsByName.get("Subscriber").getClient();
			mqttClient.setCallback(callback);
			mqttClientPub = connectorsByName.get("Publisher").getClient();
			mqttClient.connect();
			mqttClientPub.connect();
			subscribe();
		} catch (final MqttException me) {
			throw WrappedException.wrap(me);
		}
	}

	private final MqttCallback callback = new MqttCallback() {
		@Override
		public void connectionLost(final Throwable cause) {
			LOGGER.info("Connection lost", cause);
			try {
				if (!mqttClient.isConnected()) {
					LOGGER.info("Trying to reconnect mqttClient");
					mqttClient.reconnect();
					Thread.sleep(5 * 1000L);
					subscribe();
					LOGGER.info("mqttClient Reconnected, status : {}", mqttClient.isConnected());
				}
				if (!mqttClientPub.isConnected()) {
					mqttClientPub.reconnect();
					LOGGER.info("mqttClientPub Reconnected");
				}

			} catch (final MqttException me) {
				throw WrappedException.wrap(me);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}

		@Override
		public void messageArrived(final String topic, final MqttMessage message) {
			Assertion.check()
					.isNotNull(topic)
					.isNotNull(message);
			//
			handleMessage(message, topic);
		}

		@Override
		public void deliveryComplete(final IMqttDeliveryToken token) {
			// nothing for now
		}
	};

	private void subscribe() throws MqttException {
		//final String topicPub = "alpha/MQTT";
		final String topicSub = "#";
		// Subscribing to topics
		mqttClient.subscribe(topicSub, 0);
		LOGGER.info("Subscribed to channel: " + topicSub);

	}

	private void handleMessage(final MqttMessage message, final String topic) {
		Assertion.check()
				.isNotNull(message)
				.isNotNull(topic);
		//---
		final String[] data = parseMqttMessage(message);
		final String[] parsedTopic = parseTopic(topic);
		//Maybe change if condition by switch case ?
		if (parsedTopic[1].equals("fireAlarm")) {
			//TODO: change ss01 by an automatic redistribution of the message in the dedicated topic
			final InputEvent actuatorEvent = createActuatorEvent(message, "ss01" + "/" + "relay");
			eventBusManager.post(actuatorEvent);
			addMeasure(parsedTopic, data);
		} else if (parsedTopic[1].equals("message")) {
			//TODO: change ss01 by an automatic redistribution of the message in the dedicated topic
			final InputEvent messageEvent = createActuatorEvent(message, "ss01" + "/" + "display");
			eventBusManager.post(messageEvent);
			//we don't want Influx save the data in database
		} else if (parsedTopic[1].equals("turnFan")) {
			//TODO: change fs01 by an automatic redistribution of the message in the dedicated topic
			final InputEvent fanEvent = createActuatorEvent(message, "fs01" + "/" + "fan");
			eventBusManager.post(fanEvent);
		} else if (parsedTopic[1].equals("actionShutters")) {
			//TODO: change ms01 by an automatic redistribution of the message in the dedicated topic
			final InputEvent shuttersEvent = createActuatorEvent(message, "ms01" + "/" + "shutters");
			eventBusManager.post(shuttersEvent);
		} else if (parsedTopic[1].equals("light")) {
			//command shutters function of lightning
			if (Integer.parseInt(data[1]) < 100) {
				final String turnAction = "0";
				final InputEvent sendAction = new InputEvent(InputEvent.Type.of(1), "base/actionShutters", turnAction);
				eventBusManager.post(sendAction);
				addMeasure(parsedTopic, data);
			} else {
				final String turnAction = "180";
				final InputEvent sendAction = new InputEvent(InputEvent.Type.of(1), "base/actionShutters", turnAction);
				eventBusManager.post(sendAction);
				addMeasure(parsedTopic, data);
			}
		} else if (parsedTopic[1].equals("display")) {
			//we don't want Influx save the data in database
		} else if (parsedTopic[1].equals("relay")) {
			//we don't want Influx save the data in database
		} else if (parsedTopic[1].equals("fan")) {
			//we don't want Influx save the data in database
		} else if (parsedTopic[1].equals("shutters")) {
			//we don't want Influx save the data in database
		} else {
			addMeasure(parsedTopic, data);
		}
	}

	private static String[] parseMqttMessage(final MqttMessage message) {
		Assertion.check().isNotNull(message);
		//
		final String[] dataParsed = message.toString().split(" ");
		return dataParsed;
	}

	private static String[] parseTopic(final String topic) {
		Assertion.check().isNotNull(topic);
		//
		final String[] dataParsed = topic.split("/");
		return dataParsed;
	}

	private static InputEvent createActuatorEvent(final MqttMessage message, final String topic) {
		Assertion.check().isNotNull(message);
		//---
		final String[] parsedMessage = parseMqttMessage(message);
		final Integer actuatorValue = Integer.parseInt(parsedMessage[1]);
		final Integer parsedMessageLength = parsedMessage.length;

		LOGGER.info("inputEvent : {}", message);
		if (parsedMessageLength >= 3) {
			final String payload = Arrays.stream(parsedMessage).collect(Collectors.joining(" "));
			return new InputEvent(InputEvent.Type.of(actuatorValue), topic, payload);
		}
		LOGGER.info("inputEvent Case else");
		return new InputEvent(InputEvent.Type.of(actuatorValue), topic);
	}

	@EventBusSubscribed
	public void onOutput(final OutputEvent outputEvent) throws MqttException {
		Assertion.check().isNotNull(outputEvent);
		LOGGER.info("outputEvent : {}", outputEvent);
		//---
		if (outputEvent.getPayloadOpt().isPresent()) {
			mqttClientPub.publish(outputEvent.getTopic(), createMessage(outputEvent.getValue() + " " + outputEvent.getPayloadOpt().get()));
		} else {
			mqttClientPub.publish(outputEvent.getTopic(), createMessage(outputEvent.getValue()));
		}

	}

	private static MqttMessage createMessage(final String message) {
		return new MqttMessage((Instant.now().getEpochSecond() + " " + message).getBytes(StandardCharsets.UTF_8));
	}

	private void addMeasure(final String[] parsedTopic, final String[] data) {
		final Measure measure = Measure.builder(parsedTopic[1])
				.time(Instant.now())
				.addField("equipment", parsedTopic[0])
				.addField("value", Double.parseDouble(data[1]))
				.tag("equipment", parsedTopic[0])
				.build();
		eventBusManager.post(new MeasureEvent(measure));
	}
}

package io.mars.support.theme;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;

public class ThemeManager implements Component, Activeable {

	@Inject
	private ParamManager paramManager;

	private Map<String, Object> properties;

	@Override
	public void start() {
		final var themeName = paramManager.getOptionalParam("theme").map(Param::getValueAsString).orElse("neutral");

		final Map<String, Object> themeProperties = new HashMap<>();
		themeProperties.put("name", themeName);

		// Load theme properties file
		final Properties prop = new Properties();
		try (var is = ThemeManager.class.getClassLoader().getResourceAsStream("io/mars/webapp/static/theme/" + themeName + "/theme.properties")) {
			prop.load(is);
		} catch (final IOException | NullPointerException e) {
			throw new VSystemException("Theme '{0}' not found", themeName);
		}

		prop.forEach((k, v) -> addPropertiesToMap((String) k, (String) v, themeProperties));

		properties = Collections.unmodifiableMap(themeProperties);
	}

	@Override
	public void stop() {
		// Nothing
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	private static void addPropertiesToMap(final String key, final String value, final Map<String, Object> map) {
		if (key.contains(".")) {
			final var pointIndex = key.indexOf(".");
			final var prefix = key.substring(0, pointIndex);
			final var sufix = key.substring(pointIndex + 1);

			addPropertiesToMap(sufix, value, (Map<String, Object>) map.computeIfAbsent(prefix, k -> new HashMap<String, Object>()));
		} else {
			map.put(key, value);
		}
	}

}

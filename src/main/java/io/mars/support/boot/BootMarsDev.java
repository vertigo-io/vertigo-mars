package io.mars.support.boot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class BootMarsDev {

	private static final Logger LOG = LogManager.getLogger(BootMarsDev.class);

	public static void main(final String[] args) {
		LOG.info("Using dev parameters (env-dev.properties).");
		BootMars.loadClasspathFileProperties("file:./src/dev/env-dev.properties");

		// Manually set log levels for specific packages as the loading of variables happens too late for log4j to pick them up automatically
		//Configurator.setLevel("sql", Level.INFO);
		Configurator.setLevel("io.mars", Level.TRACE);

		BootMars.main(args); // args not used
	}

}

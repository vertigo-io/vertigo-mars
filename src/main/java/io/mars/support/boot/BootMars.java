package io.mars.support.boot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.OptionalInt;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.ui.boot.JettyBoot;
import io.vertigo.ui.boot.JettyBootParams;

public final class BootMars {

	private static final Logger LOG = LogManager.getLogger(BootMars.class);

	public static void loadClasspathFileProperties(final String file) {
		try (var input = openInputStream(file)) {

			final var prop = new Properties();
			prop.load(input);
			prop.forEach((k, v) -> System.setProperty((String) k, (String) v));
		} catch (final Exception e) {
			WrappedException.wrap(e, "Impossible de charger le fichier de configuration {0}", file);
		}
	}

	private static InputStream openInputStream(final String file) throws IOException {
		final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
		if (is != null) {
			return is;
		}
		return new URL(file).openStream();
	}

	public static void main(final String[] args) {
		try {
			startServer(false);
		} catch (final Exception e) {
			LOG.error("Erreur lors du démarrage du serveur", e);
			System.exit(1);
		}
	}

	public static void startServer(final boolean noJoin) throws Exception {
		// bridge JUL to Slf4j (at least for liquibase)
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.INFO); // dont bridge below INFO for perfs

		// Boot
		var contextPath = getFromParamOrEnv("CONTEXT_PATH");
		if (contextPath != null && !contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		final var jettyBootParamsBuilder = JettyBootParams
				.builder("io/mars/webapp/", MarsVSpringWebApplicationInitializer.class)
				.withContextPath(contextPath);
		// SSL
		final var sslDisabled = getFromParamOrEnv("SSL_DISABLED");
		final var isHttp = sslDisabled != null && Boolean.parseBoolean(sslDisabled);
		if (isHttp) {
			jettyBootParamsBuilder.noSsl();
		} else {
			jettyBootParamsBuilder.withSsl(
					"file:" + getFromParamOrEnv("KEYSTORE_URL"),
					getFromParamOrEnv("KEYSTORE_PASSWORD"),
					getFromParamOrEnv("SSL_KEYSTORE_ALIAS"));

			final var sniHostCheck = getFromParamOrEnv("SNI_HOST_CHECK");
			final var noSniHostCheck = !(sniHostCheck == null || Boolean.parseBoolean(sniHostCheck));
			if (noSniHostCheck) {
				jettyBootParamsBuilder.noSniHostCheck();
			}
		}
		// port
		final var paramPort = getFromParamOrEnv("EXPOSED_PORT");
		if (!StringUtil.isBlank(paramPort)) {
			jettyBootParamsBuilder.withPort(Integer.parseInt(paramPort));
		}
		if (noJoin) {
			// for junit tests
			jettyBootParamsBuilder.noJoin();
		}

		// worker name
		final var nodeName = getFromParamOrEnv("NODE_NAME");
		if (!StringUtil.isBlank(nodeName)) {
			jettyBootParamsBuilder.withJettyNodeName(nodeName);
		}

		// multipart

		final var maxFileSize = getIntFromParamOrEnv("MAX_FILE_SIZE").orElse(5); // default to 5 Mo, maxFileSize the maximum size allowed for uploaded files
		final var maxRequestSize = getIntFromParamOrEnv("MAX_REQUEST_SIZE").orElse(maxFileSize); // default to maxFileSize, maxRequestSize the maximum size allowed for multipart/form-data requests
		final var fileSizeThreshold = getIntFromParamOrEnv("FILE_SIZE_MEMORY_THRESHOLD").orElse(1); // default to 1 Mo fileSizeThreshold the size threshold after which files will be written to disk
		jettyBootParamsBuilder
				.withMaxPartSize(maxFileSize)
				.withMaxRequestSize(maxRequestSize)
				.withMaxPartSizeInMemory(fileSizeThreshold);

		// start Jetty
		JettyBoot.startServer(jettyBootParamsBuilder.build(), context -> List.of());

	}

	private static OptionalInt getIntFromParamOrEnv(final String paramName) {
		final var value = getFromParamOrEnv(paramName);
		if (value != null) {
			return OptionalInt.of(Integer.parseInt(value));
		}
		return OptionalInt.empty();
	}

	private static String getFromParamOrEnv(final String paramName) {
		final var envValue = System.getProperty(paramName);
		if (envValue != null) {
			return envValue;
		}
		return System.getenv(paramName);
	}

}

package io.mars.support.boot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
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
		try (InputStream input = openInputStream(file)) {

			final Properties prop = new Properties();
			prop.load(input);
			prop.forEach((k, v) -> System.setProperty((String) k, (String) v));
		} catch (final Exception e) {
			WrappedException.wrap(e, "Impossible de charger le fichier de configuration {0}", file);
		}
	}

	private static InputStream openInputStream(final String file) throws IOException {
		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
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
		final String paramPort = getFromParamOrEnv("EXPOSED_PORT");
		if (!StringUtil.isBlank(paramPort)) {
			jettyBootParamsBuilder.withPort(Integer.parseInt(paramPort));
		}
		if (noJoin) {
			// for junit tests
			jettyBootParamsBuilder.noJoin();
		}

		// worker name
		final String nodeName = getFromParamOrEnv("NODE_NAME");
		if (!StringUtil.isBlank(nodeName)) {
			jettyBootParamsBuilder.withJettyNodeName(nodeName);
		}

		// start Jetty
		JettyBoot.startServer(jettyBootParamsBuilder.build(), (context) -> {
			final var multipartConfigInjectionHandler = new MultipartConfigInjectionHandler();
			multipartConfigInjectionHandler.setHandler(context);
			return List.of(multipartConfigInjectionHandler);
		});

	}

	private static String getFromParamOrEnv(final String paramName) {
		final String envValue = System.getProperty(paramName);
		if (envValue != null) {
			return envValue;
		}
		return System.getenv(paramName);
	}

}

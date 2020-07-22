package io.mars.support.mda;

import java.util.Arrays;
import java.util.List;

import io.vertigo.core.node.AutoCloseableApp;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.studio.StudioFeatures;
import io.vertigo.studio.mda.MdaConfig;
import io.vertigo.studio.mda.MdaManager;
import io.vertigo.studio.notebook.Notebook;
import io.vertigo.studio.source.NotebookSource;
import io.vertigo.studio.source.NotebookSourceManager;

public class Studio {

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.build())
				// ---StudioFeature
				.addModule(new StudioFeatures()
						.withSource()
						.withVertigoSource()
						.withMda()
						.withVertigoMda()
						.build())
				.build();

	}

	public static void main(final String[] args) {
		try (final AutoCloseableApp studioApp = new AutoCloseableApp(buildNodeConfig())) {
			final NotebookSourceManager notebookSourceManager = studioApp.getComponentSpace().resolve(NotebookSourceManager.class);
			final MdaManager mdaManager = studioApp.getComponentSpace().resolve(MdaManager.class);
			//-----

			final MdaConfig mdaConfig = MdaConfig.builder("io.mars")
					.addProperty("vertigo.domain.java", "true")
					.addProperty("vertigo.domain.java.generateDtResources", "false")
					.addProperty("vertigo.domain.sql", "true")
					.addProperty("vertigo.domain.sql.targetSubDir", "javagen/sqlgen")
					.addProperty("vertigo.domain.sql.baseCible", "H2")
					.addProperty("vertigo.domain.sql.generateDrop", "true")
					.addProperty("vertigo.domain.sql.generateMasterData", "true")
					.addProperty("vertigo.task", "true")
					.addProperty("vertigo.search", "true")
					.build();

			mdaManager.clean(mdaConfig);
			final List<NotebookSource> resources = Arrays.asList(
					NotebookSource.of("kpr", "io/mars/model.kpr"),
					NotebookSource.of("kpr", "io/mars/tasks.kpr"),
					//new MetamodelResource("kpr", "io/mars/support/support_file.kpr"),
					NotebookSource.of("kpr", "io/mars/search.kpr"),
					NotebookSource.of("staticMasterData", "io/mars/support/masterDataValues.json"));
			final Notebook notebook = notebookSourceManager.read(resources);
			mdaManager.generate(notebook, mdaConfig).displayResultMessage(System.out);

		}
	}

}

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
import io.vertigo.studio.metamodel.MetamodelResource;
import io.vertigo.studio.metamodel.StudioMetamodelManager;

public class Studio {

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.build())
				// ---StudioFeature
				.addModule(new StudioFeatures()
						.withMetamodel()
						.withVertigoMetamodel()
						.withMda()
						.withVertigoMda()
						.build())
				.build();

	}

	public static void main(final String[] args) {
		try (final AutoCloseableApp studioApp = new AutoCloseableApp(buildNodeConfig())) {
			final StudioMetamodelManager studioMetamodelManager = studioApp.getComponentSpace().resolve(StudioMetamodelManager.class);
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
			final List<MetamodelResource> resources = Arrays.asList(
					MetamodelResource.of("kpr", "io/mars/model.kpr"),
					MetamodelResource.of("kpr", "io/mars/tasks.kpr"),
					//new MetamodelResource("kpr", "io/mars/support/support_file.kpr"),
					MetamodelResource.of("kpr", "io/mars/search.kpr"),
					MetamodelResource.of("staticMasterData", "io/mars/support/masterDataValues.json"));
			mdaManager.generate(studioMetamodelManager.parseResources(resources), mdaConfig).displayResultMessage(System.out);
		}
	}

}

package io.mars.support.mda;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.AutoCloseableApp;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.datastore.DataStoreFeatures;
import io.vertigo.studio.StudioFeatures;
import io.vertigo.studio.mda.MdaConfig;
import io.vertigo.studio.mda.MdaManager;
import io.vertigo.studio.metamodel.MetamodelResource;
import io.vertigo.studio.metamodel.StudioMetamodelManager;

public class Studio {

	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.beginBoot()
				.withLocales("fr_FR")
				.addPlugin(ClassPathResourceResolverPlugin.class)
				.endBoot()
				.addModule(new CommonsFeatures()
						.withScript()
						.withJaninoScript()
						.build())
				.addModule(new DataStoreFeatures()
						.withCache()
						.withMemoryCache()
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

			final Properties mdaProperties = new Properties();
			mdaProperties.put("vertigo.domain.java", "true");
			mdaProperties.put("vertigo.domain.java.generateDtResources", "false");
			mdaProperties.put("vertigo.domain.sql", "true");
			mdaProperties.put("vertigo.domain.sql.targetSubDir", "javagen/sqlgen");
			mdaProperties.put("vertigo.domain.sql.baseCible", "H2");
			mdaProperties.put("vertigo.domain.sql.generateDrop", "true");
			mdaProperties.put("vertigo.domain.sql.generateMasterData", "true");
			mdaProperties.put("vertigo.task", "true");
			mdaProperties.put("vertigo.search", "true");
			final MdaConfig mdaConfig = MdaConfig.of("io.mars", mdaProperties);

			mdaManager.clean(mdaConfig);
			final List<MetamodelResource> resources = Arrays.asList(
					new MetamodelResource("kpr", "io/mars/model.kpr"),
					new MetamodelResource("kpr", "io/mars/tasks.kpr"),
					//new MetamodelResource("kpr", "io/mars/support/support_file.kpr"),
					new MetamodelResource("kpr", "io/mars/search.kpr"),
					new MetamodelResource("staticMasterData", "io/mars/support/masterDataValues.json"));
			mdaManager.generate(studioMetamodelManager.parseResources(resources), mdaConfig).displayResultMessage(System.out);
		}
	}

}

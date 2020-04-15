package io.mars.support.mda;

import java.util.Arrays;
import java.util.List;

import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.AutoCloseableApp;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.datastore.DataStoreFeatures;
import io.vertigo.studio.StudioFeatures;
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
						.withMasterData()
						.withMetamodel()
						.withVertigoMetamodel()
						.withMda(
								Param.of("projectPackageName", "io.mars"))
						.withJavaDomainGenerator(
								Param.of("generateDtResources", "false"))
						.withTaskGenerator()
						.withSearchGenerator()
						//.withFileGenerator()
						.withSqlDomainGenerator(
								Param.of("targetSubDir", "javagen/sqlgen"),
								Param.of("baseCible", "H2"),
								Param.of("generateDrop", "true"),
								Param.of("generateMasterData", "true"))
						.withJsonMasterDataValuesProvider(
								Param.of("fileName", "io/mars/support/masterDataValues.json"))

						.build())
				.build();

	}

	public static void main(final String[] args) {
		try (final AutoCloseableApp studioApp = new AutoCloseableApp(buildNodeConfig())) {
			final StudioMetamodelManager studioMetamodelManager = studioApp.getComponentSpace().resolve(StudioMetamodelManager.class);
			final MdaManager mdaManager = studioApp.getComponentSpace().resolve(MdaManager.class);
			//-----
			mdaManager.clean();
			final List<MetamodelResource> resources = Arrays.asList(
					new MetamodelResource("kpr", "io/mars/model.kpr"),
					new MetamodelResource("kpr", "io/mars/tasks.kpr"),
					//new MetamodelResource("kpr", "io/mars/support/support_file.kpr"),
					new MetamodelResource("kpr", "io/mars/search.kpr"));
			mdaManager.generate(studioMetamodelManager.parseResources(resources)).displayResultMessage(System.out);
		}
	}

}

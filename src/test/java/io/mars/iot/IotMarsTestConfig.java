/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2018, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mars.iot;

import io.mars.basemanagement.services.equipment.iot.IotEquipmentServices;
import io.mars.basemanagement.services.equipment.iot.MqttShield;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.ModuleConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.config.NodeConfigBuilder;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;
import io.vertigo.database.DatabaseFeatures;

public final class IotMarsTestConfig {

	public static NodeConfigBuilder createNodeConfigBuilder() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.addPlugin(URLResourceResolverPlugin.class)
						.build())
				.addModule(new CommonsFeatures()
						.build())
				.addModule(new DatabaseFeatures()
						.withTimeSeriesDataBase()
						.withInfluxDb(
								Param.of("host", "http://mars.dev.klee.lan.net:8086"),
								Param.of("token", "CZGelEOQ9SwDUVrB6IL6hFAXCjev5pyDDN7ESYtvajWu3ck_wnCrIp94hs4vOLt9vSgXGT6BJ6kAr6UsJ9FaCQ=="),
								Param.of("org", "vertigo"),
								Param.of("dbNames", "mars-test"))
						.build())
				.addModule(ModuleConfig.builder("mars-iot")
						.addComponent(IotEquipmentServices.class)
						.addComponent(MqttShield.class)
						.build());
	}

	public static NodeConfig config() {
		return createNodeConfigBuilder().build();
	}

}

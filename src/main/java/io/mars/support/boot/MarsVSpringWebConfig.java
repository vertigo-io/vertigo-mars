package io.mars.support.boot;

import java.util.Optional;

import io.vertigo.core.node.Node;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.ui.impl.springmvc.config.VSpringWebConfig;

public class MarsVSpringWebConfig extends VSpringWebConfig {

	@Override
	protected boolean isDevMode() {
		final ParamManager paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		final Optional<Param> devModeOpt = paramManager.getOptionalParam("devMode");
		if (devModeOpt.isPresent()) {
			return devModeOpt.get().getValueAsBoolean();
		}
		return true;
	}

}

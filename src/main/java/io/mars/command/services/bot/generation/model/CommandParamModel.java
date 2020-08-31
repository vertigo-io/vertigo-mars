package io.mars.command.services.bot.generation.model;

import io.vertigo.commons.command.definitions.CommandParam;
import io.vertigo.core.lang.Assertion;

public class CommandParamModel {

	private final CommandParam commandParam;

	public CommandParamModel(final CommandParam commandParam) {
		Assertion.check().isNotNull(commandParam);
		//---
		this.commandParam = commandParam;
	}

	public CommandParam getCommandParam() {
		return commandParam;
	}

}

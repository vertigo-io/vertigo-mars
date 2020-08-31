package io.mars.command.services.bot.generation.model;

import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.commons.command.definitions.CommandDefinition;
import io.vertigo.core.lang.Assertion;

public class CommandModel {

	private final CommandDefinition commandDefinition;
	private final String commandNameSnakeCase;
	private final List<CommandParamModel> commandParamsModel;

	public CommandModel(final CommandDefinition commandDefinition) {
		Assertion.check().isNotNull(commandDefinition);
		//---
		this.commandDefinition = commandDefinition;
		commandParamsModel = commandDefinition.getParams()
				.stream()
				.map(CommandParamModel::new)
				.collect(Collectors.toList());

		commandNameSnakeCase = commandDefinition.getCommand().substring(1).replace('/', '_');
	}

	public String getCommandName() {
		return commandDefinition.getCommand();
	}

	public List<CommandParamModel> getParams() {
		return commandParamsModel;
	}

	public String getCommandNameSnakeCase() {
		return commandNameSnakeCase;
	}

}

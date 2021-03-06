package io.mars.command.webservices;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import io.vertigo.commons.command.CommandManager;
import io.vertigo.commons.command.CommandResponse;
import io.vertigo.commons.command.definitions.CommandDefinition;
import io.vertigo.commons.command.definitions.CommandParam;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.util.ClassUtil;
import io.vertigo.datafactory.collections.CollectionsManager;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListURIForMasterData;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.stereotype.SessionLess;

@PathPrefix("/vertigo/commands")
public class CommandWebServices implements WebServices {

	@Inject
	private CommandManager commandManager;
	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private VTransactionManager transactionManager;
	@Inject
	private CollectionsManager collectionsManager;

	@POST("/_search")
	public List<CommandUi> searchCommands(@InnerBodyParam("prefix") final Optional<String> prefixOpt) {
		return commandManager.searchCommands(prefixOpt.orElse(""))
				.stream()
				.map(CommandWebServices::toCommandUi)
				.collect(Collectors.toList());
	}

	@POST("/_execute")
	@AnonymousAccessAllowed
	@SessionLess
	public CommandResponse executeCommand(@InnerBodyParam("command") final String command, @InnerBodyParam("params") final String[] params) {
		return commandManager.executeCommand(command, params);
	}

	@POST("/_evaluateExecute")
	@AnonymousAccessAllowed
	@SessionLess
	public CommandResponse evaluateAndExecuteCommand(@InnerBodyParam("command") final String command, @InnerBodyParam("params") final String[] params) {
		final CommandDefinition commandDefinition = commandManager.findCommand(command);
		final String[] evaluatedParams = IntStream.range(0, commandDefinition.getParams().size())
				.mapToObj(i -> {
					final Type paramType = commandDefinition.getParams().get(i).getType();
					if (paramType instanceof ParameterizedType) {
						return evaluateParam(params[i], (Class) ((ParameterizedType) paramType).getActualTypeArguments()[0]);
					}
					return params[i];
				})
				.toArray(String[]::new);
		return commandManager.executeCommand(command, evaluatedParams);
	}

	private final String evaluateParam(final String rawValue, final Class entityClass) {
		final DtList<Entity> results = autocompleteParam(rawValue, DtObjectUtil.findDtDefinition(entityClass));
		Assertion.check().isTrue(results.size() == 1, "Impossible to evaluate param '{0}' as a '{1}'", rawValue, entityClass);
		return results.get(0).getUID().urn();
	}

	private static CommandUi toCommandUi(final CommandDefinition commandDefinition) {
		final CommandUi commandUi = new CommandUi();
		commandUi.setCommandName(commandDefinition.getCommand());
		commandUi.setDescpription(commandDefinition.getDescription());
		commandUi.setCommandParams(commandDefinition.getParams());
		return commandUi;
	}

	public static class CommandUi {

		private String commandName;
		private String descpription;
		private List<CommandParam> commandParams;

		public String getCommandName() {
			return commandName;
		}

		public void setCommandName(final String commandName) {
			this.commandName = commandName;
		}

		public String getDescpription() {
			return descpription;
		}

		public void setDescpription(final String descpription) {
			this.descpription = descpription;
		}

		public List<CommandParam> getCommandParams() {
			return commandParams;
		}

		public void setCommandParams(final List<CommandParam> commandParams) {
			this.commandParams = commandParams;
		}

	}

	@GET("/params/_autocomplete")
	public List<Map<String, String>> autocompleteMdList(@QueryParam("terms") final String terms, @QueryParam("entityClass") final String entityClass) {
		final DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(ClassUtil.classForName(entityClass, Entity.class));
		final DtField labelDtField = dtDefinition.getDisplayField().get();
		return autocompleteParam(terms, dtDefinition)
				.stream()
				.map(element -> {
					final Map<String, String> asMap = new HashMap<>();
					asMap.put("urn", element.getUID().urn());
					asMap.put("label", String.valueOf(labelDtField.getDataAccessor().getValue(element)));
					return asMap;
				}).collect(Collectors.toList());
	}

	private final DtList<Entity> autocompleteParam(final String terms, final DtDefinition dtDefinition) {
		final DtListURIForMasterData dtListURIForMasterData = new DtListURIForMasterData(dtDefinition, null);
		Assertion.check().isTrue(entityStoreManager.getMasterDataConfig().containsMasterData(dtListURIForMasterData.getDtDefinition()), "Autocomplete can't be use with {0}, it's not a MasterDataList.",
				dtListURIForMasterData.getDtDefinition().getName());

		//-----
		final DtField labelDtField = dtDefinition.getDisplayField().get();

		final Collection<DtField> searchedFields = Collections.singletonList(labelDtField);
		final DtList<Entity> results;
		try (final VTransactionWritable transaction = transactionManager.createCurrentTransaction()) { //Open a transaction because all fields are indexed. If there is a MDL it was load too.
			final DtList dtList = entityStoreManager.findAll(dtListURIForMasterData);
			final UnaryOperator<DtList<DtObject>> fullTextFilter = collectionsManager.createIndexDtListFunctionBuilder()
					.filter(terms != null ? terms : "", 20, searchedFields)
					.build();
			results = fullTextFilter.apply(dtList);
		}
		return results;
	}

}

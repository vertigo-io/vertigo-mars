package io.mars.command.services.bot.generation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.mars.command.services.bot.generation.TrainingConfiguration.CommandParamTrainingConfiguration;
import io.vertigo.commons.command.definitions.CommandDefinition;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.definitions.DataAccessor;
import io.vertigo.datamodel.data.definitions.DataDefinition;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public class TrainSetProvider implements Component {

	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private VTransactionManager transactionManager;

	public List<String[]> getTrainSet(final CommandDefinition commandDefinition, final TrainingConfiguration trainingConfiguration) {
		if (trainingConfiguration.containsKey(commandDefinition.getCommand())) {
			if (commandDefinition.getParams().isEmpty()) {
				return Collections.singletonList(new String[0]);
			}

			return ((List<List<String>>) product(trainingConfiguration.get(commandDefinition.getCommand()).entrySet()
					.stream()
					.map(entry -> retrievePossibleValues(entry.getValue()))
					.toArray(List[]::new)))
							.stream()
							.map(listOfValues -> listOfValues.toArray(new String[listOfValues.size()]))
							.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private static List<?> product(final List<?>... a) {
		if (a.length >= 2) {
			List<?> product = a[0];
			for (int i = 1; i < a.length; i++) {
				product = binaryProduct(product, a[i]);
			}
			return product;
		} else if (a.length == 1) {
			return a[0].stream()
					.map(Collections::singletonList)
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	private static List<?> binaryProduct(final List<?> a, final List<?> b) {
		return a.stream()
				.map(e1 -> b.stream().map(e2 -> List.of(e1, e2)).collect(Collectors.toList()))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private final List<String> retrievePossibleValues(final CommandParamTrainingConfiguration commandParamTrainingConfiguration) {
		Assertion.check().isNotBlank(commandParamTrainingConfiguration.getType());

		switch (commandParamTrainingConfiguration.getType()) {
			case "fromDb":
				try (final VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
					final DataDefinition dtDefinition = Node.getNode().getDefinitionSpace().resolve(commandParamTrainingConfiguration.getDtDefinition(), DataDefinition.class);
					final DataAccessor dtFieldDataAccessor = dtDefinition.getField(commandParamTrainingConfiguration.getDataField()).getDataAccessor();
					return entityStoreManager.find(dtDefinition, Criterions.alwaysTrue(), DtListState.of(commandParamTrainingConfiguration.getLimit()))
							.stream()
							.map(entity -> String.valueOf(dtFieldDataAccessor.getValue(entity)))
							.collect(Collectors.toList());
				}
			case "static":
				Assertion.check().isNotNull(commandParamTrainingConfiguration.getValues());
				return commandParamTrainingConfiguration.getValues();
			default:
				throw new VSystemException("Unsupported training type : ", commandParamTrainingConfiguration.getType());
		}
	}
}

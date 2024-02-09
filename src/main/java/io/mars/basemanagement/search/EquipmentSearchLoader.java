package io.mars.basemanagement.search;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentIndex;
import io.mars.basemanagement.services.equipment.EquipmentServices;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.node.Node;
import io.vertigo.datafactory.impl.search.loader.AbstractSqlSearchLoader;
import io.vertigo.datafactory.search.definitions.SearchChunk;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.model.SearchIndex;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datamodel.task.TaskManager;

public final class EquipmentSearchLoader extends AbstractSqlSearchLoader<Equipment, EquipmentIndex> {

	private final EquipmentServices myEquipmentServices;

	@Inject
	public EquipmentSearchLoader(final TaskManager taskManager, final VTransactionManager transactionManager, final EquipmentServices equipmentServices) {
		super(taskManager, transactionManager);
		myEquipmentServices = equipmentServices;
	}

	@Override
	public List<SearchIndex<Equipment, EquipmentIndex>> loadData(final SearchChunk<Equipment> searchChunk) {
		final SearchIndexDefinition indexDefinition = Node.getNode().getDefinitionSpace().resolve("IdxEquipment", SearchIndexDefinition.class);
		final List<Long> equipmentIds = new ArrayList<>();
		for (final UID<Equipment> uid : searchChunk.getAllUIDs()) {
			equipmentIds.add((Long) uid.getId());
		}
		final DtList<EquipmentIndex> equipmentIndexes = myEquipmentServices.getEquipmentIndex(equipmentIds);
		final List<SearchIndex<Equipment, EquipmentIndex>> equipmentSearchIndexes = new ArrayList<>(searchChunk.getAllUIDs().size());
		for (final EquipmentIndex equipmentIndex : equipmentIndexes) {
			equipmentSearchIndexes.add(SearchIndex.<Equipment, EquipmentIndex>createIndex(indexDefinition,
					UID.of(indexDefinition.getKeyConceptDtDefinition(), equipmentIndex.getEquipmentId()), equipmentIndex));
		}
		return equipmentSearchIndexes;
	}

}

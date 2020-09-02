package io.mars.basemanagement.search;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.services.base.BaseServices;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.datafactory.impl.search.loader.AbstractSqlSearchLoader;
import io.vertigo.datafactory.search.SearchManager;
import io.vertigo.datafactory.search.definitions.SearchChunk;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.model.SearchIndex;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.task.TaskManager;

public final class BaseSearchLoader extends AbstractSqlSearchLoader<Long, Base, BaseIndex> implements Activeable {

	private final BaseServices myBaseServices;
	private final SearchManager searchManager;
	private SearchIndexDefinition indexDefinition;

	@Inject
	public BaseSearchLoader(final TaskManager taskManager, final SearchManager searchManager, final VTransactionManager transactionManager, final BaseServices baseServices) {
		super(taskManager, transactionManager);
		this.searchManager = searchManager;
		myBaseServices = baseServices;
	}

	@Override
	public void start() {
		indexDefinition = searchManager.findFirstIndexDefinitionByKeyConcept(Base.class);
	}

	@Override
	public void stop() {
		indexDefinition = null;
	}

	@Override
	public List<SearchIndex<Base, BaseIndex>> loadData(final SearchChunk<Base> searchChunk) {
		final List<Long> baseIds = new ArrayList<>();
		for (final UID<Base> uid : searchChunk.getAllUIDs()) {
			baseIds.add((Long) uid.getId());
		}
		final DtList<BaseIndex> baseIndexes = myBaseServices.getBaseIndex(baseIds);
		final List<SearchIndex<Base, BaseIndex>> baseSearchIndexes = new ArrayList<>(searchChunk.getAllUIDs().size());
		for (final BaseIndex baseIndex : baseIndexes) {
			baseSearchIndexes.add(
					SearchIndex.<Base, BaseIndex> createIndex(indexDefinition,
							UID.of(indexDefinition.getKeyConceptDtDefinition(), baseIndex.getBaseId()), baseIndex));
		}
		return baseSearchIndexes;
	}

}

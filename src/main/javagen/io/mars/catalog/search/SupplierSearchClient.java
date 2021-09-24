package io.mars.catalog.search;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.lang.Generated;
import io.vertigo.core.lang.ListBuilder;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datafactory.collections.definitions.FacetDefinition.FacetOrder;
import io.vertigo.datafactory.collections.definitions.FacetRangeDefinitionSupplier;
import io.vertigo.datafactory.collections.definitions.FacetTermDefinitionSupplier;
import io.vertigo.datafactory.collections.definitions.FacetedQueryDefinitionSupplier;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.collections.model.SelectedFacetValues;
import io.vertigo.datafactory.search.SearchManager;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinitionSupplier;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datafactory.search.model.SearchQueryBuilder;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.mars.catalog.domain.Supplier;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class SupplierSearchClient implements Component, DefinitionProvider {

	private final SearchManager searchManager;
	private final VTransactionManager transactionManager;

	/**
	 * Contructeur.
	 * @param searchManager Search Manager
	 * @param transactionManager Transaction Manager
	 */
	@Inject
	public SupplierSearchClient(final SearchManager searchManager, final VTransactionManager transactionManager) {
		this.searchManager = searchManager;
		this.transactionManager = transactionManager;
	}

	/**
	 * Création d'une SearchQuery de type : Supplier.
	 * @param criteria Critères de recherche
	 * @param selectedFacetValues Liste des facettes sélectionnées à appliquer
	 * @return SearchQueryBuilder pour ce type de recherche
	 */
	public SearchQueryBuilder createSearchQueryBuilderSupplier(final String criteria, final SelectedFacetValues selectedFacetValues) {
		return SearchQuery.builder("QrySupplier")
				.withCriteria(criteria)
				.withFacet(selectedFacetValues);
	}

	/**
	 * Récupération du résultat issu d'une requête.
	 * @param searchQuery critères initiaux
	 * @param listState Etat de la liste (tri et pagination)
	 * @return Résultat correspondant à la requête (de type Supplier)
	 */
	public FacetedQueryResult<Supplier, SearchQuery> loadListIdxSupplier(final SearchQuery searchQuery, final DtListState listState) {
		final SearchIndexDefinition indexDefinition = io.vertigo.core.node.Node.getNode().getDefinitionSpace().resolve("IdxSupplier",SearchIndexDefinition.class);
		return searchManager.loadList(indexDefinition, searchQuery, listState);
	}
		
	/**
	 * Récupération du résultat issu d'une requête.
	 * @param searchQuery critères initiaux
	 * @param listState Etat de la liste (tri et pagination)
	 * @return Résultat correspondant à la requête (de type Supplier)
	 */
	public FacetedQueryResult<Supplier, SearchQuery> loadList(final SearchQuery searchQuery, final DtListState listState) {
		final List<SearchIndexDefinition> indexDefinitions = List.of( 
				io.vertigo.core.node.Node.getNode().getDefinitionSpace().resolve("IdxSupplier",SearchIndexDefinition.class));
		return searchManager.loadList(indexDefinitions, searchQuery, listState);
	}

	/**
	 * Mark an entity as dirty. Index of these elements will be reindexed if Tx commited.
	 * Reindexation isn't synchrone, strategy is dependant of plugin's parameters.
	 *
	 * @param entityUID Key concept's UID
	 */
	public void markAsDirty(final UID entityUID) {
		transactionManager.getCurrentTransaction().addAfterCompletion((final boolean txCommitted) -> {
			if (txCommitted) {// reindex only is tx successful
				searchManager.markAsDirty(Arrays.asList(entityUID));
			}
		});
	}

	/**
	 * Mark an entity as dirty. Index of these elements will be reindexed if Tx commited.
	 * Reindexation isn't synchrone, strategy is dependant of plugin's parameters.
	 *
	 * @param entity Key concept
	 */
	public void markAsDirty(final io.mars.catalog.domain.Supplier entity) {
		markAsDirty(UID.of(entity));
	}
	

	/** {@inheritDoc} */
	@Override
	public List<DefinitionSupplier> get(final DefinitionSpace definitionSpace) {
		return new ListBuilder<DefinitionSupplier>()
				//---
				// SearchIndexDefinition
				//-----
				.add(new SearchIndexDefinitionSupplier("IdxSupplier")
						.withIndexDtDefinition("DtSupplier")
						.withKeyConcept("DtSupplier")
						.withLoaderId("SupplierSearchLoader"))
				
				//---
				// FacetTermDefinition
				//-----
				.add(new FacetTermDefinitionSupplier("FctSupplierActivitePrincipale")
						.withDtDefinition("DtSupplier")
						.withFieldName("activitePrincipale")
						.withLabel("Core business")
						.withOrder(FacetOrder.count))
				.add(new FacetTermDefinitionSupplier("FctSupplierCategorieEntreprise")
						.withDtDefinition("DtSupplier")
						.withFieldName("categorieEntreprise")
						.withLabel("Business category")
						.withOrder(FacetOrder.count))
				.add(new FacetTermDefinitionSupplier("FctSupplierCategorieJuridique")
						.withDtDefinition("DtSupplier")
						.withFieldName("categorieJuridique")
						.withLabel("Legal category")
						.withOrder(FacetOrder.count))
				.add(new FacetRangeDefinitionSupplier("FctSupplierDateCreation")
						.withDtDefinition("DtSupplier")
						.withFieldName("dateCreation")
						.withLabel("Creation date")
						.withRange("r1", "dateCreation:[01/01/2000 TO 01/01/2005]", "2000-2005")
						.withRange("r2", "dateCreation:[01/01/2005 TO 01/01/2010]", "2005-2010")
						.withRange("r2", "dateCreation:[01/01/2010 TO 01/01/2015]", "2010-2015")
						.withRange("r3", "dateCreation:[01/01/2015 TO *]", "depuis 2015")
						.withOrder(FacetOrder.definition))
				.add(new FacetTermDefinitionSupplier("FctSupplierTrancheEffectifs")
						.withDtDefinition("DtSupplier")
						.withFieldName("trancheEffectifs")
						.withLabel("Workforce range")
						.withOrder(FacetOrder.count))

				//---
				// FacetedQueryDefinition
				//-----
				.add(new FacetedQueryDefinitionSupplier("QrySupplier")
						.withFacet("FctSupplierTrancheEffectifs")
						.withFacet("FctSupplierCategorieEntreprise")
						.withFacet("FctSupplierCategorieJuridique")
						.withFacet("FctSupplierActivitePrincipale")
						.withFacet("FctSupplierDateCreation")
						.withListFilterBuilderClass(io.vertigo.datafactory.impl.search.dsl.DslListFilterBuilder.class)
						.withListFilterBuilderQuery("[nom,denomination]:#+query*#")
						.withCriteriaSmartType("STyLabel"))
				.build();
	}
}

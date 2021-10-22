package io.mars.catalog.controllers.supplier;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.mars.catalog.domain.Supplier;
import io.mars.catalog.services.supplier.SupplierServices;
import io.mars.domain.DtDefinitions.SupplierFields;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.collections.model.SelectedFacetValues;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/catalog/suppliers")
public class SupplierSearchController extends AbstractVSpringMvcController {

	private static final ViewContextKey<String> criteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<FacetedQueryResult<Supplier, SearchQuery>> suppliers = ViewContextKey.of("suppliers");

	@Inject
	private SupplierServices supplierServices;

	@GetMapping("/")
	public void initContext(
			final ViewContext viewContext) {
		final FacetedQueryResult<Supplier, SearchQuery> facetedQueryResult = supplierServices.searchSuppliers("", SelectedFacetValues.empty().build(), DtListState.defaultOf(Supplier.class));
		viewContext
				.publishRef(criteriaKey, "")
				.publishFacetedQueryResult(suppliers, SupplierFields.siren, facetedQueryResult, criteriaKey);
	}

	@PostMapping("/_search")
	public ViewContext doSearch(
			final ViewContext viewContext,
			@ViewAttribute("criteria") final String criteria,
			@ViewAttribute("suppliers") final SelectedFacetValues selectedFacetValues,
			final DtListState dtListState) {
		final FacetedQueryResult<Supplier, SearchQuery> facetedQueryResult = supplierServices.searchSuppliers(criteria, selectedFacetValues, dtListState);
		return viewContext.publishFacetedQueryResult(suppliers, SupplierFields.siren, facetedQueryResult, criteriaKey);
	}

}

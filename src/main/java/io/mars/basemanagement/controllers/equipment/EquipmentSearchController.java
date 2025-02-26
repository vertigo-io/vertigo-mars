package io.mars.basemanagement.controllers.equipment;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentIndex;
import io.mars.basemanagement.domain.GeoSearchEquipmentCriteria;
import io.mars.basemanagement.services.equipment.EquipmentServices;
import io.mars.domain.DtDefinitions.EquipmentIndexFields;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.ai.impl.llm.FacetPromptUtil;
import io.vertigo.ai.impl.llm.FacetPromptUtil.FacetPromptResult;
import io.vertigo.ai.llm.LlmManager;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datafactory.collections.model.FacetedQueryResult;
import io.vertigo.datafactory.collections.model.SelectedFacetValues;
import io.vertigo.datafactory.search.model.SearchQuery;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@Secured("Equipment$read")
@RequestMapping("/basemanagement/equipments")
public class EquipmentSearchController extends AbstractVSpringMvcController {

	private static final ViewContextKey<String> listRenderer = ViewContextKey.of("listRenderer");
	private static final ViewContextKey<GeoSearchEquipmentCriteria> criteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<FacetedQueryResult<EquipmentIndex, SearchQuery>> equipments = ViewContextKey.of("equipments");

	@Inject
	private EquipmentServices equipmentServices;

	@Inject
	private LlmManager llmManager;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @RequestParam("criteria") final Optional<String> optCriteria, @RequestParam("renderer") final Optional<String> optRenderer) {
		final GeoSearchEquipmentCriteria geoCriteria = new GeoSearchEquipmentCriteria();
		final SelectedFacetValues selectedFacets;
		if (optCriteria.isPresent()) {
			final FacetPromptResult aiCriteria = doAiSearch(optCriteria.get());
			geoCriteria.setCriteria(aiCriteria.criteria());
			selectedFacets = aiCriteria.selectedFacetValues();
		} else {
			selectedFacets = SelectedFacetValues.empty().build();
		}

		if (geoCriteria.getCriteria() == null) {
			geoCriteria.setCriteria("");
		}

		viewContext
				.publishDto(criteriaKey, geoCriteria)
				.publishRef(listRenderer, optRenderer.orElse("table"));

		final String listRendererValue = viewContext.getString(listRenderer);
		final FacetedQueryResult<EquipmentIndex, SearchQuery> facetedQueryResult = switch (listRendererValue) {
			case "table" -> equipmentServices.searchEquipments(geoCriteria.getCriteria(), selectedFacets, DtListState.defaultOf(Equipment.class));
			case "map" -> equipmentServices.searchGeoClusterEquipments(geoCriteria, selectedFacets, DtListState.of(3));
			default -> throw new VUserException("Unsupported list renderer ({0})", listRendererValue);
		};
		viewContext.publishFacetedQueryResult(equipments, EquipmentIndexFields.equipmentId, facetedQueryResult, criteriaKey);

	}

	@PostMapping("/_search")
	public ViewContext doSearch(
			final ViewContext viewContext,
			@ViewAttribute("criteria") final GeoSearchEquipmentCriteria criteria,
			@ViewAttribute("equipments") final SelectedFacetValues selectedFacetValues,
			final DtListState dtListState) {
		final String listRendererValue = viewContext.getString(listRenderer);
		final FacetedQueryResult<EquipmentIndex, SearchQuery> facetedQueryResult = switch (listRendererValue) {
			case "table" -> equipmentServices.searchEquipments(Optional.ofNullable(criteria.getCriteria()).orElse(""), selectedFacetValues, dtListState);
			case "map" -> equipmentServices.searchGeoClusterEquipments(criteria, selectedFacetValues, DtListState.of(3));
			default -> throw new VUserException("Unsupported list renderer ({0})", listRendererValue);
		};
		viewContext.publishFacetedQueryResult(equipments, EquipmentIndexFields.equipmentId, facetedQueryResult, criteriaKey);
		return viewContext;
	}

	@PostMapping("/_searchAi")
	public ViewContext doSearchAi(
			final ViewContext viewContext,
			@RequestParam("search") final String search,
			final DtListState dtListState) {

		// example of search :
		// - les batiments / les véhicules de 2015 / buildings of 2020
		// - esa satellite 655 /  cnsa satellite S-E-655 / les mines et drones cnsa de 2012
		// - production d'énergie / ce qui produit de l'énergie / ce qui produit de l'électricité

		final FacetPromptResult aiCriteria = doAiSearch(search);
		final var criteria = new GeoSearchEquipmentCriteria();
		criteria.setCriteria(aiCriteria.criteria());

		if (criteria.getCriteria() == null) {
			criteria.setCriteria("");
		}

		final String listRendererValue = viewContext.getString(listRenderer);
		final FacetedQueryResult<EquipmentIndex, SearchQuery> facetedQueryResult = switch (listRendererValue) {
			case "table" -> equipmentServices.searchEquipments(criteria.getCriteria(), aiCriteria.selectedFacetValues(), dtListState);
			case "map" -> equipmentServices.searchGeoClusterEquipments(criteria, aiCriteria.selectedFacetValues(), DtListState.of(3));
			default -> throw new VUserException("Unsupported list renderer ({0})", listRendererValue);
		};
		viewContext
				.publishFacetedQueryResult(equipments, EquipmentIndexFields.equipmentId, facetedQueryResult, criteriaKey)
				.publishDto(criteriaKey, criteria);
		return viewContext;
	}

	private FacetPromptResult doAiSearch(final String criteria) {
		// empty search to list all facet values as input for the LLM
		final var emptySearch = equipmentServices.searchEquipments("", SelectedFacetValues.empty().build(), DtListState.defaultOf(Equipment.class));

		return llmManager.ask(FacetPromptUtil.createFacetPrompt(criteria == null ? "" : criteria, emptySearch,
				Optional.of(
						"""
								For date facet (FctEquipmentPurchaseDate), select only if the user is asking for a date criteria in a 4 digits form.
								Select tags only if the user is asking specifically for them or if the term exists only in the tags.
								Put strictly in 'criteria' the equipment reference in the form 'A-A-123', or '123'. Don't put anything else and put null if nothing correpsond. Example, if user ask for 'satellites from 2020' you should select the year 2020 in the date facet and put null in the criteria and if he ask for 'satellite 145' you should put '145' in the criteria and null in the date facet.
								""")),
				FacetPromptResult.class);
	}

}

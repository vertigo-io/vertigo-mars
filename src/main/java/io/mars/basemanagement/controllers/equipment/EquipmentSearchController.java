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

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @RequestParam("criteria") final Optional<String> optCriteria, @RequestParam("renderer") final Optional<String> optRenderer) {
		final GeoSearchEquipmentCriteria geoCriteria = new GeoSearchEquipmentCriteria();
		geoCriteria.setCriteria(optCriteria.orElse(""));

		viewContext
				.publishDto(criteriaKey, geoCriteria)
				.publishRef(listRenderer, optRenderer.orElse("table"));
		final String listRendererValue = viewContext.getString(listRenderer);
		final FacetedQueryResult<EquipmentIndex, SearchQuery> facetedQueryResult = switch (listRendererValue) {
			case "table" -> equipmentServices.searchEquipments(geoCriteria.getCriteria(), SelectedFacetValues.empty().build(), DtListState.defaultOf(Equipment.class));
			case "map" -> equipmentServices.searchGeoClusterEquipments(geoCriteria, SelectedFacetValues.empty().build(), DtListState.of(3));
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

	@PostMapping("/_searchGeo")
	public ViewContext doSearchGeo(
			final ViewContext viewContext,
			@ViewAttribute("criteria") final GeoSearchEquipmentCriteria criteria,
			@ViewAttribute("equipments") final SelectedFacetValues selectedFacetValues,
			final DtListState dtListState) {
		final String listRendererValue = viewContext.getString(listRenderer);
		final FacetedQueryResult<EquipmentIndex, SearchQuery> facetedQueryResult = switch (listRendererValue) {
			case "table" -> equipmentServices.searchEquipments(criteria.getCriteria(), selectedFacetValues, dtListState);
			case "map" -> equipmentServices.searchGeoClusterEquipments(criteria, selectedFacetValues, DtListState.of(250));
			default -> throw new VUserException("Unsupported list renderer ({0})", listRendererValue);
		};
		viewContext.publishFacetedQueryResult(equipments, EquipmentIndexFields.equipmentId, facetedQueryResult, criteriaKey);
		return viewContext;
	}

}

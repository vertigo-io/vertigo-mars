package io.mars.basemanagement.webservices;

import java.util.Collections;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.Picture;
import io.mars.basemanagement.search.BaseIndex;
import io.mars.basemanagement.services.base.BaseServices;
import io.mars.basemanagement.services.equipment.EquipmentServices;
import io.mars.catalog.domain.EquipmentType;
import io.mars.catalog.services.equipment.EquipmentTypeServices;
import io.mars.domain.DtDefinitions.BaseIndexFields;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.geo.services.geocoder.GeoLocation;
import io.vertigo.geo.services.geosearch.GeoSearchServices;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.QueryParam;

public class BaseManagementWebServices implements WebServices {

	@Inject
	private BaseServices baseServices;

	@Inject
	private EquipmentServices equipmentServices;

	@Inject
	private EquipmentTypeServices equipmentTypeServices;

	@Inject
	private GeoSearchServices geoSearchServices;

	@AnonymousAccessAllowed
	@GET("/bases")
	public DtList<Base> bases() {
		return baseServices.getBases(DtListState.defaultOf(Base.class));
	}

	@AnonymousAccessAllowed
	@PUT("/base/")
	public Base saveBase(final Base base) {
		baseServices.save(base, Collections.emptyList(), new DtList<>(Picture.class));
		return baseServices.get(base.getBaseId());
	}

	@AnonymousAccessAllowed
	@GET("/base/{code}/equipments")
	public DtList<Equipment> equimentsForBase(@PathParam("code") final String code) {
		return equipmentServices.getEquipmentByBase(code);
	}

	@AnonymousAccessAllowed
	@GET("/equipmentTypes")
	public DtList<EquipmentType> equimentTypes() {
		return equipmentTypeServices.getEquipmentTypes(DtListState.defaultOf(EquipmentType.class));
	}

	@AnonymousAccessAllowed
	@GET("/_geoSearch")
	public DtList<BaseIndex> geoSearch(@QueryParam("lat1") final Double lat1, @QueryParam("lon1") final Double lon1, @QueryParam("lat2") final Double lat2, @QueryParam("lon2") final Double lon2) {
		return geoSearchServices.searchInBoundingBox(new GeoLocation(lat1, lon1), new GeoLocation(lat2, lon2), "IdxBase", BaseIndex.class, BaseIndexFields.geoLocation);
	}

}

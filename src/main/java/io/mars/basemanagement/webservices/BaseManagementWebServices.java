package io.mars.basemanagement.webservices;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.BaseIndex;
import io.mars.basemanagement.domain.Equipment;
import io.mars.basemanagement.domain.EquipmentIndex;
import io.mars.basemanagement.domain.Picture;
import io.mars.basemanagement.services.base.BaseServices;
import io.mars.basemanagement.services.equipment.EquipmentServices;
import io.mars.catalog.domain.EquipmentType;
import io.mars.catalog.services.equipment.EquipmentTypeServices;
import io.mars.domain.DtDefinitions.BaseIndexFields;
import io.mars.domain.DtDefinitions.EquipmentIndexFields;
import io.mars.support.smarttypes.GeoPoint;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.geo.geocoder.GeoLocation;
import io.vertigo.geo.geosearch.GeoSearchManager;
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
	private GeoSearchManager geoSearchManager;

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
	@GET("/bases/_geoSearch")
	public DtList<BaseIndex> geoSearch(@QueryParam("topLeft") final GeoPoint topLeft, @QueryParam("bottomRight") final GeoPoint bottomRight) {
		return geoSearchManager.searchInBoundingBox(new GeoLocation(topLeft.getLat(), topLeft.getLon()), new GeoLocation(bottomRight.getLat(), bottomRight.getLon()), "IdxBase", BaseIndex.class, BaseIndexFields.geoLocation, Optional.empty());
	}

	@AnonymousAccessAllowed
	@GET("/equipments/_geoSearch")
	public DtList<EquipmentIndex> geoSearchEquipment(@QueryParam("topLeft") final GeoPoint topLeft, @QueryParam("bottomRight") final GeoPoint bottomRight) {
		return geoSearchManager.searchInBoundingBox(new GeoLocation(topLeft.getLat(), topLeft.getLon()), new GeoLocation(bottomRight.getLat(), bottomRight.getLon()), "IdxEquipment", EquipmentIndex.class, EquipmentIndexFields.geoLocation, Optional.empty());
	}

}

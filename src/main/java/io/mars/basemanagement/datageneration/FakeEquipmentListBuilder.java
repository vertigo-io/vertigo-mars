package io.mars.basemanagement.datageneration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import io.mars.basemanagement.domain.Base;
import io.mars.basemanagement.domain.Business;
import io.mars.basemanagement.domain.Equipment;
import io.mars.catalog.domain.EquipmentType;
import io.mars.datageneration.DataGenerator;
import io.mars.support.smarttypes.GeoPoint;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Builder;
import io.vertigo.datamodel.structure.model.DtList;

@SuppressWarnings("rawtypes")
public class FakeEquipmentListBuilder implements Builder {

	private int myMaxValues;
	private static final int MIN_EQUIPMENT_VALUE = 100;
	private static final int MAX_EQUIPMENT_VALUE = 3000;
	private static final int MIN_RENTING_FEE = 50;
	private static final int MAX_RENTING_FEE = 300;

	private DtList<Business> myBusinessList;
	private DtList<EquipmentType> myEquipmentTypes;
	private List<Base> myBases;
	private List<Long> myGeosectorIds;

	public FakeEquipmentListBuilder() {
	}

	/*
		public FakeEquipmentListBuilder withRandomSeed(final Long randomSeed) {
			rnd = new Random(randomSeed);
			return this;
		}
	*/
	public FakeEquipmentListBuilder withMaxValues(final int maxValues) {
		myMaxValues = maxValues;
		return this;
	}

	public FakeEquipmentListBuilder withSaveCSVPath(final String path) {
		// TODO implement saving to csv
		return this;
	}

	public FakeEquipmentListBuilder withEquipmentTypeList(final DtList<EquipmentType> equipmentTypes) {
		Assertion.check().isNotNull(equipmentTypes);
		//---
		myEquipmentTypes = equipmentTypes;
		return this;
	}

	public FakeEquipmentListBuilder withBusinessList(final DtList<Business> businesses) {
		Assertion.check().isNotNull(businesses);
		//---
		myBusinessList = businesses;
		return this;
	}

	public FakeEquipmentListBuilder withBases(final List<Base> bases) {
		Assertion.check().isNotNull(bases);
		//---
		myBases = bases;
		return this;
	}

	public FakeEquipmentListBuilder withGeosectorIdList(final List<Long> geosectorIds) {
		Assertion.check().isNotNull(geosectorIds);
		//---
		myGeosectorIds = geosectorIds;
		return this;
	}

	private static String getTags() {
		return "";
	}

	private static String getCode(final String equipmentTypeName, final String businessName, final int equipmentIndex) {
		return equipmentTypeName.substring(0, 1) + "-" + businessName.substring(0, 1) + "-" + equipmentIndex;
	}

	private static String getDescription() {
		return "";
	}

	private static String getEquipmentName(final String equipmentTypeName, final String businessName, final int equipmentIndex) {
		return equipmentTypeName + " " + equipmentTypeName.substring(0, 1) + "-" + businessName.substring(0, 1) + "-" + equipmentIndex;
	}

	private static int getHealthLevel() {
		return DataGenerator.RND.nextInt(101);
	}

	private static BigDecimal getEquipmentValue() {
		final Double randomDouble = myNextDouble(MIN_EQUIPMENT_VALUE, MAX_EQUIPMENT_VALUE);
		return BigDecimal.valueOf(randomDouble);
	}

	private static LocalDate getPurchaseDate() {
		final LocalDate today = LocalDate.now();
		return today.minus(31L + DataGenerator.RND.nextInt(3650), ChronoUnit.DAYS);
	}

	private static BigDecimal getRentingFee() {
		final Double randomDouble = myNextDouble(MIN_RENTING_FEE, MAX_RENTING_FEE);
		return BigDecimal.valueOf(randomDouble);
	}

	private static Equipment createEquipment(
			final Base base,
			final Business business,
			final EquipmentType equipmentType,
			final Long geoSectorId,
			final String code,
			final BigDecimal equipmentValue,
			final Integer healthLevel,
			final String name,
			final String description,
			final LocalDate purchaseDate,
			final BigDecimal rentingFee,
			final String tags,
			final GeoPoint geoLocation) {
		final Equipment equipment = new Equipment();
		equipment.base().setId(base.getBaseId());
		equipment.business().set(business);
		equipment.equipmentType().set(equipmentType);
		equipment.geosector().setId(geoSectorId);
		equipment.setCode(code);
		equipment.setEquipmentValue(equipmentValue);
		equipment.setGeoLocation(geoLocation);
		equipment.setHealthLevel(healthLevel);
		equipment.setName(name);
		equipment.setDescription(description);
		equipment.setPurchaseDate(purchaseDate);
		equipment.setRentingFee(rentingFee);
		equipment.setTags(base.getTags());
		return equipment;
	}

	private static Double myNextDouble(final int min, final int max) {
		return min + DataGenerator.RND.nextDouble() * (max - min);
	}

	private static GeoPoint getRandomizedLocation(final GeoPoint geoPoint, final Double equipmentTypeLongitudeDeviation) {
		final int latitudeInvertionFactor = DataGenerator.RND.nextBoolean() ? 1 : -1;
		final int longitudeInvertionFactor = DataGenerator.RND.nextBoolean() ? 1 : -1;
		return new GeoPoint(
				geoPoint.getLon() + DataGenerator.RND.nextDouble() * equipmentTypeLongitudeDeviation * longitudeInvertionFactor * 2, // because 180 vs 360
				geoPoint.getLat() + DataGenerator.RND.nextDouble() * equipmentTypeLongitudeDeviation * latitudeInvertionFactor);
	}

	private static Double getEquipmentTypeDeviation(final EquipmentType equipmentType) {
		return switch (equipmentType.equipmentCategory().get().getLabel()) {
			case "Bot" -> 0.5;
			case "Building" -> 0.1;
			case "Vehicle" -> 2.0;
			default -> 0.0;// no change by default
		};
	}

	@Override
	public DtList<Equipment> build() {
		Assertion.check()
				.isNotNull(myEquipmentTypes)
				.isNotNull(myBusinessList)
				.isNotNull(myBases)
				.isNotNull(myGeosectorIds);
		final DtList<Equipment> equipments = new DtList<>(Equipment.class);

		for (int currentCounter = 0; currentCounter < myMaxValues; currentCounter++) {

			final EquipmentType currentEquipmentType = myEquipmentTypes.get(DataGenerator.RND.nextInt(myEquipmentTypes.size()));
			final Business currentBusiness = myBusinessList.get(DataGenerator.RND.nextInt(myBusinessList.size()));
			final Base base = myBases.get(DataGenerator.RND.nextInt(myBases.size()));
			final Long currentGeosectorId = myGeosectorIds.get(DataGenerator.RND.nextInt(myGeosectorIds.size()));
			final GeoPoint equipmentLocation = getRandomizedLocation(base.getGeoLocation(), getEquipmentTypeDeviation(currentEquipmentType));
			final Equipment equipment = createEquipment(
					base,
					currentBusiness,
					currentEquipmentType,
					currentGeosectorId,
					getCode(currentEquipmentType.getLabel(), currentBusiness.getName(), currentCounter),
					getEquipmentValue(),
					getHealthLevel(),
					getEquipmentName(currentEquipmentType.getLabel(), currentBusiness.getName(), currentCounter),
					getDescription(),
					getPurchaseDate(),
					getRentingFee(),
					getTags(),
					equipmentLocation);
			equipments.add(equipment);
		}
		return equipments;
	}

}

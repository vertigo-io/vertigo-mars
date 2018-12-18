package io.mars.basemanagement.domain;

import io.vertigo.dynamo.domain.model.KeyConcept;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.model.VAccessor;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Equipment implements KeyConcept {
	private static final long serialVersionUID = 1L;

	private Long equipmentId;
	private String name;
	private String code;
	private Integer healthLevel;
	private java.time.LocalDate purchaseDate;
	private String description;
	private String tags;
	private String geoLocation;
	private java.math.BigDecimal rentingFee;
	private java.math.BigDecimal equipmentValue;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_BASE_EQUIPMENT",
			fkFieldName = "BASE_ID",
			primaryDtDefinitionName = "DT_BASE",
			primaryIsNavigable = true,
			primaryRole = "Base",
			primaryLabel = "Base",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_EQUIPMENT",
			foreignIsNavigable = false,
			foreignRole = "BaseEquipments",
			foreignLabel = "Base Equipments",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Base> baseIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Base.class, "Base");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_EQUIPMENT_GEOSECTOR",
			fkFieldName = "GEOSECTOR_ID",
			primaryDtDefinitionName = "DT_GEOSECTOR",
			primaryIsNavigable = true,
			primaryRole = "Geosector",
			primaryLabel = "Equipment Geosector",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_EQUIPMENT",
			foreignIsNavigable = false,
			foreignRole = "Equipment",
			foreignLabel = "Equipment",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Geosector> geosectorIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Geosector.class, "Geosector");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_BUSINESS_EQUIPMENT",
			fkFieldName = "BUSINESS_ID",
			primaryDtDefinitionName = "DT_BUSINESS",
			primaryIsNavigable = true,
			primaryRole = "Business",
			primaryLabel = "Business",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_EQUIPMENT",
			foreignIsNavigable = false,
			foreignRole = "BusinessEquipment",
			foreignLabel = "Business Equipment",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Business> businessIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Business.class, "Business");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_EQUIPMENT_EQUIPMENT_TYPE",
			fkFieldName = "EQUIPMENT_TYPE_ID",
			primaryDtDefinitionName = "DT_EQUIPMENT_TYPE",
			primaryIsNavigable = true,
			primaryRole = "EquipmentType",
			primaryLabel = "Equipment Type",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_EQUIPMENT",
			foreignIsNavigable = false,
			foreignRole = "Equipment",
			foreignLabel = "Equipment",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.catalog.domain.EquipmentType> equipmentTypeIdAccessor = new VAccessor<>(io.mars.catalog.domain.EquipmentType.class, "EquipmentType");

	/** {@inheritDoc} */
	@Override
	public UID<Equipment> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long equipmentId <b>Obligatoire</b>
	 */
	@Field(domain = "DO_ID", type = "ID", required = true, label = "Id")
	public Long getEquipmentId() {
		return equipmentId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param equipmentId Long <b>Obligatoire</b>
	 */
	public void setEquipmentId(final Long equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name
	 */
	@Field(domain = "DO_LABEL", label = "Name")
	public String getName() {
		return name;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Name'.
	 * @param name String
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Equipment Code'.
	 * @return String code
	 */
	@Field(domain = "DO_CODE", label = "Equipment Code")
	public String getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Equipment Code'.
	 * @param code String
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Health Level'.
	 * @return Integer healthLevel
	 */
	@Field(domain = "DO_HEALTH", label = "Health Level")
	public Integer getHealthLevel() {
		return healthLevel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Health Level'.
	 * @param healthLevel Integer
	 */
	public void setHealthLevel(final Integer healthLevel) {
		this.healthLevel = healthLevel;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Date of purchase'.
	 * @return LocalDate purchaseDate
	 */
	@Field(domain = "DO_LOCALDATE", label = "Date of purchase")
	public java.time.LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date of purchase'.
	 * @param purchaseDate LocalDate
	 */
	public void setPurchaseDate(final java.time.LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(domain = "DO_DESCRIPTION", label = "Description")
	public String getDescription() {
		return description;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Description'.
	 * @param description String
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Tags'.
	 * @return String tags
	 */
	@Field(domain = "DO_MULTIPLE_IDS", label = "Tags")
	public String getTags() {
		return tags;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Tags'.
	 * @param tags String
	 */
	public void setTags(final String tags) {
		this.tags = tags;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Geographic Location'.
	 * @return String geoLocation
	 */
	@Field(domain = "DO_LABEL", label = "Geographic Location")
	public String getGeoLocation() {
		return geoLocation;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Geographic Location'.
	 * @param geoLocation String
	 */
	public void setGeoLocation(final String geoLocation) {
		this.geoLocation = geoLocation;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Renting Fee'.
	 * @return BigDecimal rentingFee
	 */
	@Field(domain = "DO_CURRENCY", label = "Renting Fee")
	public java.math.BigDecimal getRentingFee() {
		return rentingFee;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Renting Fee'.
	 * @param rentingFee BigDecimal
	 */
	public void setRentingFee(final java.math.BigDecimal rentingFee) {
		this.rentingFee = rentingFee;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Current equipment value'.
	 * @return BigDecimal equipmentValue
	 */
	@Field(domain = "DO_CURRENCY", label = "Current equipment value")
	public java.math.BigDecimal getEquipmentValue() {
		return equipmentValue;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Current equipment value'.
	 * @param equipmentValue BigDecimal
	 */
	public void setEquipmentValue(final java.math.BigDecimal equipmentValue) {
		this.equipmentValue = equipmentValue;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Base'.
	 * @return Long baseId
	 */
	@Field(domain = "DO_ID", type = "FOREIGN_KEY", label = "Base")
	public Long getBaseId() {
		return (Long)  baseIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Base'.
	 * @param baseId Long
	 */
	public void setBaseId(final Long baseId) {
		baseIdAccessor.setId(baseId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Equipment Geosector'.
	 * @return Long geosectorId
	 */
	@Field(domain = "DO_ID", type = "FOREIGN_KEY", label = "Equipment Geosector")
	public Long getGeosectorId() {
		return (Long)  geosectorIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Equipment Geosector'.
	 * @param geosectorId Long
	 */
	public void setGeosectorId(final Long geosectorId) {
		geosectorIdAccessor.setId(geosectorId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Business'.
	 * @return Long businessId
	 */
	@Field(domain = "DO_ID", type = "FOREIGN_KEY", label = "Business")
	public Long getBusinessId() {
		return (Long)  businessIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Business'.
	 * @param businessId Long
	 */
	public void setBusinessId(final Long businessId) {
		businessIdAccessor.setId(businessId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Equipment Type'.
	 * @return Long equipmentTypeId
	 */
	@Field(domain = "DO_ID", type = "FOREIGN_KEY", label = "Equipment Type")
	public Long getEquipmentTypeId() {
		return (Long)  equipmentTypeIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Equipment Type'.
	 * @param equipmentTypeId Long
	 */
	public void setEquipmentTypeId(final Long equipmentTypeId) {
		equipmentTypeIdAccessor.setId(equipmentTypeId);
	}

 	/**
	 * Association : Base.
	 * @return l'accesseur vers la propriété 'Base'
	 */
	public VAccessor<io.mars.basemanagement.domain.Base> base() {
		return baseIdAccessor;
	}
	
	@Deprecated
	public io.mars.basemanagement.domain.Base getBase() {
		// we keep the lazyness
		if (!baseIdAccessor.isLoaded()) {
			baseIdAccessor.load();
		}
		return baseIdAccessor.get();
	}

	/**
	 * Retourne l'UID: Base.
	 * @return UID de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.UID<io.mars.basemanagement.domain.Base> getBaseUID() {
		return baseIdAccessor.getUID();
	}

 	/**
	 * Association : Business.
	 * @return l'accesseur vers la propriété 'Business'
	 */
	public VAccessor<io.mars.basemanagement.domain.Business> business() {
		return businessIdAccessor;
	}
	
	@Deprecated
	public io.mars.basemanagement.domain.Business getBusiness() {
		// we keep the lazyness
		if (!businessIdAccessor.isLoaded()) {
			businessIdAccessor.load();
		}
		return businessIdAccessor.get();
	}

	/**
	 * Retourne l'UID: Business.
	 * @return UID de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.UID<io.mars.basemanagement.domain.Business> getBusinessUID() {
		return businessIdAccessor.getUID();
	}

 	/**
	 * Association : Equipment Type.
	 * @return l'accesseur vers la propriété 'Equipment Type'
	 */
	public VAccessor<io.mars.catalog.domain.EquipmentType> equipmentType() {
		return equipmentTypeIdAccessor;
	}
	
	@Deprecated
	public io.mars.catalog.domain.EquipmentType getEquipmentType() {
		// we keep the lazyness
		if (!equipmentTypeIdAccessor.isLoaded()) {
			equipmentTypeIdAccessor.load();
		}
		return equipmentTypeIdAccessor.get();
	}

	/**
	 * Retourne l'UID: Equipment Type.
	 * @return UID de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.UID<io.mars.catalog.domain.EquipmentType> getEquipmentTypeUID() {
		return equipmentTypeIdAccessor.getUID();
	}

 	/**
	 * Association : Equipment Geosector.
	 * @return l'accesseur vers la propriété 'Equipment Geosector'
	 */
	public VAccessor<io.mars.basemanagement.domain.Geosector> geosector() {
		return geosectorIdAccessor;
	}
	
	@Deprecated
	public io.mars.basemanagement.domain.Geosector getGeosector() {
		// we keep the lazyness
		if (!geosectorIdAccessor.isLoaded()) {
			geosectorIdAccessor.load();
		}
		return geosectorIdAccessor.get();
	}

	/**
	 * Retourne l'UID: Equipment Geosector.
	 * @return UID de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.UID<io.mars.basemanagement.domain.Geosector> getGeosectorUID() {
		return geosectorIdAccessor.getUID();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

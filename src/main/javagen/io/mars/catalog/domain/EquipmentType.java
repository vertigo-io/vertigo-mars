package io.mars.catalog.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class EquipmentType implements Entity {
	private static final long serialVersionUID = 1L;

	private Long equipmentTypeId;
	private String label;
	private Boolean active;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AEquipmentTypeEquipmentCategory",
			fkFieldName = "equipmentCategoryId",
			primaryDtDefinitionName = "DtEquipmentCategory",
			primaryIsNavigable = true,
			primaryRole = "EquipmentCategory",
			primaryLabel = "Equipment Category",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtEquipmentType",
			foreignIsNavigable = false,
			foreignRole = "EquipmentType",
			foreignLabel = "EquipmentType",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.catalog.domain.EquipmentCategory> equipmentCategoryIdAccessor = new StoreVAccessor<>(io.mars.catalog.domain.EquipmentCategory.class, "EquipmentCategory");

	/** {@inheritDoc} */
	@Override
	public UID<EquipmentType> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long equipmentTypeId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getEquipmentTypeId() {
		return equipmentTypeId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param equipmentTypeId Long <b>Obligatoire</b>
	 */
	public void setEquipmentTypeId(final Long equipmentTypeId) {
		this.equipmentTypeId = equipmentTypeId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Equipment Type Label'.
	 * @return String label <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Equipment Type Label")
	@io.vertigo.datamodel.data.stereotype.SortField
	@io.vertigo.datamodel.data.stereotype.DisplayField
	public String getLabel() {
		return label;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Equipment Type Label'.
	 * @param label String <b>Obligatoire</b>
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Equipment type is active'.
	 * @return Boolean active <b>Obligatoire</b>
	 */
	@Field(smartType = "STyYesNo", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Equipment type is active")
	public Boolean getActive() {
		return active;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Equipment type is active'.
	 * @param active Boolean <b>Obligatoire</b>
	 */
	public void setActive(final Boolean active) {
		this.active = active;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Equipment Category'.
	 * @return Long equipmentCategoryId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Equipment Category", fkDefinition = "DtEquipmentCategory" )
	public Long getEquipmentCategoryId() {
		return (Long) equipmentCategoryIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Equipment Category'.
	 * @param equipmentCategoryId Long
	 */
	public void setEquipmentCategoryId(final Long equipmentCategoryId) {
		equipmentCategoryIdAccessor.setId(equipmentCategoryId);
	}

 	/**
	 * Association : Equipment Category.
	 * @return l'accesseur vers la propriété 'Equipment Category'
	 */
	public StoreVAccessor<io.mars.catalog.domain.EquipmentCategory> equipmentCategory() {
		return equipmentCategoryIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}

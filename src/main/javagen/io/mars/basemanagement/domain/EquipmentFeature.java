package io.mars.basemanagement.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class EquipmentFeature implements Entity {
	private static final long serialVersionUID = 1L;

	private Long equipmentFeatureId;
	private String name;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AEquipmentEquipmentFeature",
			fkFieldName = "equipmentId",
			primaryDtDefinitionName = "DtEquipment",
			primaryIsNavigable = true,
			primaryRole = "Equipment",
			primaryLabel = "Equipment",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtEquipmentFeature",
			foreignIsNavigable = false,
			foreignRole = "EquipmentFeature",
			foreignLabel = "EquipmentFeature",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.basemanagement.domain.Equipment> equipmentIdAccessor = new StoreVAccessor<>(io.mars.basemanagement.domain.Equipment.class, "Equipment");

	/** {@inheritDoc} */
	@Override
	public UID<EquipmentFeature> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long equipmentFeatureId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getEquipmentFeatureId() {
		return equipmentFeatureId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param equipmentFeatureId Long <b>Obligatoire</b>
	 */
	public void setEquipmentFeatureId(final Long equipmentFeatureId) {
		this.equipmentFeatureId = equipmentFeatureId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name
	 */
	@Field(smartType = "STyLabel", label = "Name")
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Equipment'.
	 * @return Long equipmentId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Equipment", fkDefinition = "DtEquipment" )
	public Long getEquipmentId() {
		return (Long) equipmentIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Equipment'.
	 * @param equipmentId Long
	 */
	public void setEquipmentId(final Long equipmentId) {
		equipmentIdAccessor.setId(equipmentId);
	}

 	/**
	 * Association : Equipment.
	 * @return l'accesseur vers la propriété 'Equipment'
	 */
	public StoreVAccessor<io.mars.basemanagement.domain.Equipment> equipment() {
		return equipmentIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

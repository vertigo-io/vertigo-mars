package io.mars.hr.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datastore.impl.entitystore.EnumStoreVAccessor;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Mission implements Entity {
	private static final long serialVersionUID = 1L;

	private Long missionId;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "APersonMission",
			fkFieldName = "personId",
			primaryDtDefinitionName = "DtPerson",
			primaryIsNavigable = true,
			primaryRole = "Person",
			primaryLabel = "Person",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtMission",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.hr.domain.Person> personIdAccessor = new StoreVAccessor<>(io.mars.hr.domain.Person.class, "Person");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AMissionBase",
			fkFieldName = "baseId",
			primaryDtDefinitionName = "DtBase",
			primaryIsNavigable = true,
			primaryRole = "Base",
			primaryLabel = "Base",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtMission",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.basemanagement.domain.Base> baseIdAccessor = new StoreVAccessor<>(io.mars.basemanagement.domain.Base.class, "Base");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ABusinessMission",
			fkFieldName = "businessId",
			primaryDtDefinitionName = "DtBusiness",
			primaryIsNavigable = true,
			primaryRole = "Business",
			primaryLabel = "Business",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtMission",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.basemanagement.domain.Business> businessIdAccessor = new StoreVAccessor<>(io.mars.basemanagement.domain.Business.class, "Business");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AMissionRole",
			fkFieldName = "roleId",
			primaryDtDefinitionName = "DtRole",
			primaryIsNavigable = true,
			primaryRole = "Role",
			primaryLabel = "Role",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtMission",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final EnumStoreVAccessor<io.mars.hr.domain.Role, io.mars.hr.domain.RoleEnum> roleIdAccessor = new EnumStoreVAccessor<>(io.mars.hr.domain.Role.class, "Role", io.mars.hr.domain.RoleEnum.class);

	/** {@inheritDoc} */
	@Override
	public UID<Mission> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long missionId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getMissionId() {
		return missionId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param missionId Long <b>Obligatoire</b>
	 */
	public void setMissionId(final Long missionId) {
		this.missionId = missionId;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Person'.
	 * @return Long personId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Person", fkDefinition = "DtPerson" )
	public Long getPersonId() {
		return (Long) personIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Person'.
	 * @param personId Long
	 */
	public void setPersonId(final Long personId) {
		personIdAccessor.setId(personId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Base'.
	 * @return Long baseId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Base", fkDefinition = "DtBase" )
	public Long getBaseId() {
		return (Long) baseIdAccessor.getId();
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
	 * Récupère la valeur de la propriété 'Business'.
	 * @return Long businessId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Business", fkDefinition = "DtBusiness" )
	public Long getBusinessId() {
		return (Long) businessIdAccessor.getId();
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
	 * Récupère la valeur de la propriété 'Role'.
	 * @return String roleId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyCode", label = "Role", fkDefinition = "DtRole", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public String getRoleId() {
		return (String) roleIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Role'.
	 * @param roleId String <b>Obligatoire</b>
	 */
	public void setRoleId(final String roleId) {
		roleIdAccessor.setId(roleId);
	}

 	/**
	 * Association : Business.
	 * @return l'accesseur vers la propriété 'Business'
	 */
	public StoreVAccessor<io.mars.basemanagement.domain.Business> business() {
		return businessIdAccessor;
	}

 	/**
	 * Association : Base.
	 * @return l'accesseur vers la propriété 'Base'
	 */
	public StoreVAccessor<io.mars.basemanagement.domain.Base> base() {
		return baseIdAccessor;
	}

 	/**
	 * Association : Role.
	 * @return l'accesseur vers la propriété 'Role'
	 */
	public EnumStoreVAccessor<io.mars.hr.domain.Role, io.mars.hr.domain.RoleEnum> role() {
		return roleIdAccessor;
	}

 	/**
	 * Association : Person.
	 * @return l'accesseur vers la propriété 'Person'
	 */
	public StoreVAccessor<io.mars.hr.domain.Person> person() {
		return personIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataUtil.toString(this);
	}
}

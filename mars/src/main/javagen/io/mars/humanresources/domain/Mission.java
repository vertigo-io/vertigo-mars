package io.mars.humanresources.domain;

import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.dynamo.domain.model.VAccessor;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Mission implements Entity {
	private static final long serialVersionUID = 1L;

	private Long missionId;
	private String role;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_PERSON_MISSION",
			fkFieldName = "PERSON_ID",
			primaryDtDefinitionName = "DT_PERSON",
			primaryIsNavigable = true,
			primaryRole = "Person",
			primaryLabel = "Person",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_MISSION",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.humanresources.domain.Person> personIdAccessor = new VAccessor<>(io.mars.humanresources.domain.Person.class, "Person");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_MISSION_BASE",
			fkFieldName = "BASE_ID",
			primaryDtDefinitionName = "DT_BASE",
			primaryIsNavigable = true,
			primaryRole = "Base",
			primaryLabel = "Base",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_MISSION",
			foreignIsNavigable = false,
			foreignRole = "Mission",
			foreignLabel = "Mission",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Base> baseIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Base.class, "Base");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_BUSINESS_MISSION",
			fkFieldName = "BUSINESS_ID",
			primaryDtDefinitionName = "DT_BUSINESS",
			primaryIsNavigable = true,
			primaryRole = "Business",
			primaryLabel = "Business",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DT_MISSION",
			foreignIsNavigable = false,
			foreignRole = "BusinessMissions",
			foreignLabel = "Business Missions",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Business> businessIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Business.class, "Business");

	/** {@inheritDoc} */
	@Override
	public URI<Mission> getURI() {
		return URI.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long missionId <b>Obligatoire</b>
	 */
	@Field(domain = "DO_ID", type = "ID", required = true, label = "Id")
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
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Role'.
	 * @return String role
	 */
	@Field(domain = "DO_CODE", label = "Role")
	public String getRole() {
		return role;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Role'.
	 * @param role String
	 */
	public void setRole(final String role) {
		this.role = role;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Person'.
	 * @return Long personId
	 */
	@Field(domain = "DO_ID", type = "FOREIGN_KEY", label = "Person")
	public Long getPersonId() {
		return (Long)  personIdAccessor.getId();
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
	 * Retourne l'URI: Business.
	 * @return URI de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.URI<io.mars.basemanagement.domain.Business> getBusinessURI() {
		return businessIdAccessor.getURI();
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
	 * Retourne l'URI: Base.
	 * @return URI de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.URI<io.mars.basemanagement.domain.Base> getBaseURI() {
		return baseIdAccessor.getURI();
	}

 	/**
	 * Association : Person.
	 * @return l'accesseur vers la propriété 'Person'
	 */
	public VAccessor<io.mars.humanresources.domain.Person> person() {
		return personIdAccessor;
	}
	
	@Deprecated
	public io.mars.humanresources.domain.Person getPerson() {
		// we keep the lazyness
		if (!personIdAccessor.isLoaded()) {
			personIdAccessor.load();
		}
		return personIdAccessor.get();
	}

	/**
	 * Retourne l'URI: Person.
	 * @return URI de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.URI<io.mars.humanresources.domain.Person> getPersonURI() {
		return personIdAccessor.getURI();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

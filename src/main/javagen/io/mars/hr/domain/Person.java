package io.mars.hr.domain;

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
public final class Person implements Entity {
	private static final long serialVersionUID = 1L;

	private Long personId;
	private String firstName;
	private String lastName;
	private String email;
	private Long picturefileId;
	private String picturefileIdTmp;
	private String tags;
	private java.time.LocalDate dateHired;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "APersonGroups",
			fkFieldName = "groupId",
			primaryDtDefinitionName = "DtGroups",
			primaryIsNavigable = true,
			primaryRole = "Groups",
			primaryLabel = "Group",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtPerson",
			foreignIsNavigable = false,
			foreignRole = "Person",
			foreignLabel = "Person",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.hr.domain.Groups> groupIdAccessor = new StoreVAccessor<>(io.mars.hr.domain.Groups.class, "Groups");

	/** {@inheritDoc} */
	@Override
	public UID<Person> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long personId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getPersonId() {
		return personId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param personId Long <b>Obligatoire</b>
	 */
	public void setPersonId(final Long personId) {
		this.personId = personId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'First Name'.
	 * @return String firstName <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "First Name")
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'First Name'.
	 * @param firstName String <b>Obligatoire</b>
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Last Name'.
	 * @return String lastName <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Last Name")
	public String getLastName() {
		return lastName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Last Name'.
	 * @param lastName String <b>Obligatoire</b>
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'E-mail'.
	 * @return String email <b>Obligatoire</b>
	 */
	@Field(smartType = "STyEmail", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "E-mail")
	public String getEmail() {
		return email;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'E-mail'.
	 * @param email String <b>Obligatoire</b>
	 */
	public void setEmail(final String email) {
		this.email = email;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Picture'.
	 * @return Long picturefileId
	 */
	@Field(smartType = "STyId", label = "Picture")
	public Long getPicturefileId() {
		return picturefileId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Picture'.
	 * @param picturefileId Long
	 */
	public void setPicturefileId(final Long picturefileId) {
		this.picturefileId = picturefileId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Picture'.
	 * @return String picturefileIdTmp
	 */
	@Field(smartType = "STyLabel", persistent = false, label = "Picture")
	public String getPicturefileIdTmp() {
		return picturefileIdTmp;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Picture'.
	 * @param picturefileIdTmp String
	 */
	public void setPicturefileIdTmp(final String picturefileIdTmp) {
		this.picturefileIdTmp = picturefileIdTmp;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Tags'.
	 * @return String tags
	 */
	@Field(smartType = "STyMultipleIds", label = "Tags")
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
	 * Récupère la valeur de la propriété 'Date hired'.
	 * @return LocalDate dateHired
	 */
	@Field(smartType = "STyLocaldate", label = "Date hired")
	public java.time.LocalDate getDateHired() {
		return dateHired;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date hired'.
	 * @param dateHired LocalDate
	 */
	public void setDateHired(final java.time.LocalDate dateHired) {
		this.dateHired = dateHired;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Group'.
	 * @return Long groupId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Group", fkDefinition = "DtGroups" )
	public Long getGroupId() {
		return (Long) groupIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Group'.
	 * @param groupId Long
	 */
	public void setGroupId(final Long groupId) {
		groupIdAccessor.setId(groupId);
	}
	
	/**
	 * Champ : COMPUTED.
	 * Récupère la valeur de la propriété calculée 'Full name'.
	 * @return String fullName
	 */
	@Field(smartType = "STyLabel", type = "COMPUTED", persistent = false, label = "Full name")
	@io.vertigo.datamodel.structure.stereotype.DisplayField
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

 	/**
	 * Association : Group.
	 * @return l'accesseur vers la propriété 'Group'
	 */
	public StoreVAccessor<io.mars.hr.domain.Groups> groups() {
		return groupIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

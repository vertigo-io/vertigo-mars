package io.mars.basemanagement.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class EquipmentSurveyDisplay implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long esuId;
	private java.time.Instant dateAnswer;
	private String firstName;
	private String lastName;
	private String email;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long esuId
	 */
	@Field(smartType = "STyId", label = "Id")
	public Long getEsuId() {
		return esuId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Id'.
	 * @param esuId Long
	 */
	public void setEsuId(final Long esuId) {
		this.esuId = esuId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Date'.
	 * @return Instant dateAnswer
	 */
	@Field(smartType = "STyInstant", label = "Date")
	public java.time.Instant getDateAnswer() {
		return dateAnswer;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date'.
	 * @param dateAnswer Instant
	 */
	public void setDateAnswer(final java.time.Instant dateAnswer) {
		this.dateAnswer = dateAnswer;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'First name'.
	 * @return String firstName
	 */
	@Field(smartType = "STyLabel", label = "First name")
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'First name'.
	 * @param firstName String
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Last name'.
	 * @return String lastName
	 */
	@Field(smartType = "STyLabel", label = "Last name")
	public String getLastName() {
		return lastName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Last name'.
	 * @param lastName String
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'E-mail'.
	 * @return String email
	 */
	@Field(smartType = "STyEmail", label = "E-mail")
	public String getEmail() {
		return email;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'E-mail'.
	 * @param email String
	 */
	public void setEmail(final String email) {
		this.email = email;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

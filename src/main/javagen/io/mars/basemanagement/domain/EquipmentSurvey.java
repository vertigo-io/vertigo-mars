package io.mars.basemanagement.domain;

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
public final class EquipmentSurvey implements Entity {
	private static final long serialVersionUID = 1L;

	private Long esuId;
	private java.time.Instant dateAnswer;
	private io.vertigo.easyforms.easyformsrunner.model.template.EasyFormsData formulaire;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AEquipmentSurveyEquipment",
			fkFieldName = "equipmentId",
			primaryDtDefinitionName = "DtEquipment",
			primaryIsNavigable = true,
			primaryRole = "Equipment",
			primaryLabel = "Equipment",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtEquipmentSurvey",
			foreignIsNavigable = false,
			foreignRole = "EquipmentSurvey",
			foreignLabel = "EquipmentSurvey",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.basemanagement.domain.Equipment> equipmentIdAccessor = new StoreVAccessor<>(io.mars.basemanagement.domain.Equipment.class, "Equipment");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AEquipmentSurveyPerson",
			fkFieldName = "personId",
			primaryDtDefinitionName = "DtPerson",
			primaryIsNavigable = true,
			primaryRole = "Person",
			primaryLabel = "Respondent",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtEquipmentSurvey",
			foreignIsNavigable = false,
			foreignRole = "EquipmentSurvey",
			foreignLabel = "EquipmentSurvey",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.mars.hr.domain.Person> personIdAccessor = new StoreVAccessor<>(io.mars.hr.domain.Person.class, "Person");

	/** {@inheritDoc} */
	@Override
	public UID<EquipmentSurvey> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long esuId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getEsuId() {
		return esuId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param esuId Long <b>Obligatoire</b>
	 */
	public void setEsuId(final Long esuId) {
		this.esuId = esuId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Date'.
	 * @return Instant dateAnswer <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInstant", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Date")
	public java.time.Instant getDateAnswer() {
		return dateAnswer;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date'.
	 * @param dateAnswer Instant <b>Obligatoire</b>
	 */
	public void setDateAnswer(final java.time.Instant dateAnswer) {
		this.dateAnswer = dateAnswer;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Informations'.
	 * @return EasyFormsData formulaire <b>Obligatoire</b>
	 */
	@Field(smartType = "STyEfFormData", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Informations")
	public io.vertigo.easyforms.easyformsrunner.model.template.EasyFormsData getFormulaire() {
		return formulaire;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Informations'.
	 * @param formulaire EasyFormsData <b>Obligatoire</b>
	 */
	public void setFormulaire(final io.vertigo.easyforms.easyformsrunner.model.template.EasyFormsData formulaire) {
		this.formulaire = formulaire;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Equipment'.
	 * @return Long equipmentId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Equipment", fkDefinition = "DtEquipment", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getEquipmentId() {
		return (Long) equipmentIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Equipment'.
	 * @param equipmentId Long <b>Obligatoire</b>
	 */
	public void setEquipmentId(final Long equipmentId) {
		equipmentIdAccessor.setId(equipmentId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Respondent'.
	 * @return Long personId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Respondent", fkDefinition = "DtPerson", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getPersonId() {
		return (Long) personIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Respondent'.
	 * @param personId Long <b>Obligatoire</b>
	 */
	public void setPersonId(final Long personId) {
		personIdAccessor.setId(personId);
	}

 	/**
	 * Association : Equipment.
	 * @return l'accesseur vers la propriété 'Equipment'
	 */
	public StoreVAccessor<io.mars.basemanagement.domain.Equipment> equipment() {
		return equipmentIdAccessor;
	}

 	/**
	 * Association : Respondent.
	 * @return l'accesseur vers la propriété 'Respondent'
	 */
	public StoreVAccessor<io.mars.hr.domain.Person> person() {
		return personIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}

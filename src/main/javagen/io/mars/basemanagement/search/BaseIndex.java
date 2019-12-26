package io.mars.basemanagement.search;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class BaseIndex implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long baseId;
	private String name;
	private String code;
	private java.time.LocalDate creationDate;
	private String tags;
	private String baseTypeLabel;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'id'.
	 * @return Long baseId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "id")
	public Long getBaseId() {
		return baseId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'id'.
	 * @param baseId Long <b>Obligatoire</b>
	 */
	public void setBaseId(final Long baseId) {
		this.baseId = baseId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name
	 */
	@Field(domain = "DoLabel", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Name")
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
	 * Récupère la valeur de la propriété 'Code'.
	 * @return String code
	 */
	@Field(domain = "DoCode", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Code")
	public String getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Code'.
	 * @param code String
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Creation Date'.
	 * @return LocalDate creationDate
	 */
	@Field(domain = "DoLocaldate", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Creation Date")
	public java.time.LocalDate getCreationDate() {
		return creationDate;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Creation Date'.
	 * @param creationDate LocalDate
	 */
	public void setCreationDate(final java.time.LocalDate creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Tags'.
	 * @return String tags
	 */
	@Field(domain = "DoTags", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Tags")
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
	 * Récupère la valeur de la propriété 'Type'.
	 * @return String baseTypeLabel
	 */
	@Field(domain = "DoLabel", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Type")
	public String getBaseTypeLabel() {
		return baseTypeLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Type'.
	 * @param baseTypeLabel String
	 */
	public void setBaseTypeLabel(final String baseTypeLabel) {
		this.baseTypeLabel = baseTypeLabel;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

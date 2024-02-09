package io.mars.hr.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Groups implements Entity {
	private static final long serialVersionUID = 1L;

	private Long groupId;
	private String name;

	/** {@inheritDoc} */
	@Override
	public UID<Groups> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long groupId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param groupId Long <b>Obligatoire</b>
	 */
	public void setGroupId(final Long groupId) {
		this.groupId = groupId;
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
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataUtil.toString(this);
	}
}

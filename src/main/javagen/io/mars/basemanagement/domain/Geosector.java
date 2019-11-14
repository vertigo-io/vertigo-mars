package io.mars.basemanagement.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Geosector implements Entity {
	private static final long serialVersionUID = 1L;

	private Long geosectorId;
	private String sectorLabel;

	/** {@inheritDoc} */
	@Override
	public UID<Geosector> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long geosectorId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", required = true, label = "Id")
	public Long getGeosectorId() {
		return geosectorId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param geosectorId Long <b>Obligatoire</b>
	 */
	public void setGeosectorId(final Long geosectorId) {
		this.geosectorId = geosectorId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Sector Label'.
	 * @return String sectorLabel
	 */
	@Field(domain = "DoLabel", label = "Sector Label")
	public String getSectorLabel() {
		return sectorLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Sector Label'.
	 * @param sectorLabel String
	 */
	public void setSectorLabel(final String sectorLabel) {
		this.sectorLabel = sectorLabel;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

package io.mars.opendata.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.domain.model.DtStaticMasterData;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class OpendataSetStatus implements DtStaticMasterData {
	private static final long serialVersionUID = 1L;

	private String opendataSetStatusId;
	private String label;

	/** {@inheritDoc} */
	@Override
	public UID<OpendataSetStatus> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return String opendataSetStatusId <b>Obligatoire</b>
	 */
	@Field(domain = "DoCode", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public String getOpendataSetStatusId() {
		return opendataSetStatusId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param opendataSetStatusId String <b>Obligatoire</b>
	 */
	public void setOpendataSetStatusId(final String opendataSetStatusId) {
		this.opendataSetStatusId = opendataSetStatusId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Status Label'.
	 * @return String label
	 */
	@Field(domain = "DoLabel", cardinality = io.vertigo.core.lang.Cardinality.OPTIONAL_OR_NULLABLE, label = "Status Label")
	public String getLabel() {
		return label;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Status Label'.
	 * @param label String
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

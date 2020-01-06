package io.mars.basemanagement.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.model.VAccessor;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Picture implements Entity {
	private static final long serialVersionUID = 1L;

	private Long pictureId;
	private Long picturefileId;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "ABasePicture",
			fkFieldName = "baseId",
			primaryDtDefinitionName = "DtBase",
			primaryIsNavigable = true,
			primaryRole = "Base",
			primaryLabel = "Base",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtPicture",
			foreignIsNavigable = false,
			foreignRole = "Picture",
			foreignLabel = "Picture",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.mars.basemanagement.domain.Base> baseIdAccessor = new VAccessor<>(io.mars.basemanagement.domain.Base.class, "Base");

	/** {@inheritDoc} */
	@Override
	public UID<Picture> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long pictureId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getPictureId() {
		return pictureId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param pictureId Long <b>Obligatoire</b>
	 */
	public void setPictureId(final Long pictureId) {
		this.pictureId = pictureId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long picturefileId
	 */
	@Field(domain = "DoId", label = "Id")
	public Long getPicturefileId() {
		return picturefileId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Id'.
	 * @param picturefileId Long
	 */
	public void setPicturefileId(final Long picturefileId) {
		this.picturefileId = picturefileId;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Base'.
	 * @return Long baseId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "FOREIGN_KEY", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Base")
	public Long getBaseId() {
		return (Long) baseIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Base'.
	 * @param baseId Long <b>Obligatoire</b>
	 */
	public void setBaseId(final Long baseId) {
		baseIdAccessor.setId(baseId);
	}

 	/**
	 * Association : Base.
	 * @return l'accesseur vers la propriété 'Base'
	 */
	public VAccessor<io.mars.basemanagement.domain.Base> base() {
		return baseIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

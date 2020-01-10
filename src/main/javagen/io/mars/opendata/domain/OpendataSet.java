package io.mars.opendata.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.datastore.impl.entitystore.EnumStoreVAccessor;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class OpendataSet implements Entity {
	private static final long serialVersionUID = 1L;

	private Long odsId;
	private String code;
	private String title;
	private String description;
	private String endPointUrl;
	private Long picturefileId;
	private String tags;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AOpendataSetOpendataSetStatus",
			fkFieldName = "opendataSetStatusId",
			primaryDtDefinitionName = "DtOpendataSetStatus",
			primaryIsNavigable = true,
			primaryRole = "OpendataSetStatus",
			primaryLabel = "Opendata Set Status",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtOpendataSet",
			foreignIsNavigable = false,
			foreignRole = "OpendataSet",
			foreignLabel = "OpendataSet",
			foreignMultiplicity = "0..*")
	private final EnumStoreVAccessor<io.mars.opendata.domain.OpendataSetStatus, io.mars.opendata.domain.OpendataSetStatusEnum> opendataSetStatusIdAccessor = new EnumStoreVAccessor<>(io.mars.opendata.domain.OpendataSetStatus.class, "OpendataSetStatus", io.mars.opendata.domain.OpendataSetStatusEnum.class);

	/** {@inheritDoc} */
	@Override
	public UID<OpendataSet> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long odsId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getOdsId() {
		return odsId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param odsId Long <b>Obligatoire</b>
	 */
	public void setOdsId(final Long odsId) {
		this.odsId = odsId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Code'.
	 * @return String code
	 */
	@Field(domain = "DoCode", label = "Code")
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
	 * Récupère la valeur de la propriété 'Title'.
	 * @return String title
	 */
	@Field(domain = "DoLabel", label = "Title")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Title'.
	 * @param title String
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(domain = "DoDescription", label = "Description")
	public String getDescription() {
		return description;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Description'.
	 * @param description String
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Service Endpoint URL'.
	 * @return String endPointUrl
	 */
	@Field(domain = "DoUrl", label = "Service Endpoint URL")
	public String getEndPointUrl() {
		return endPointUrl;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Service Endpoint URL'.
	 * @param endPointUrl String
	 */
	public void setEndPointUrl(final String endPointUrl) {
		this.endPointUrl = endPointUrl;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Picture'.
	 * @return Long picturefileId
	 */
	@Field(domain = "DoId", label = "Picture")
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
	 * Récupère la valeur de la propriété 'Tags'.
	 * @return String tags
	 */
	@Field(domain = "DoMultipleIds", label = "Tags")
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Opendata Set Status'.
	 * @return String opendataSetStatusId
	 */
	@Field(domain = "DoCode", type = "FOREIGN_KEY", label = "Opendata Set Status")
	public String getOpendataSetStatusId() {
		return (String) opendataSetStatusIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Opendata Set Status'.
	 * @param opendataSetStatusId String
	 */
	public void setOpendataSetStatusId(final String opendataSetStatusId) {
		opendataSetStatusIdAccessor.setId(opendataSetStatusId);
	}

 	/**
	 * Association : Opendata Set Status.
	 * @return l'accesseur vers la propriété 'Opendata Set Status'
	 */
	public EnumStoreVAccessor<io.mars.opendata.domain.OpendataSetStatus, io.mars.opendata.domain.OpendataSetStatusEnum> opendataSetStatus() {
		return opendataSetStatusIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}

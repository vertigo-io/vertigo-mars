package io.mars.basemanagement.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum EventStatusEnum implements MasterDataEnum<io.mars.basemanagement.domain.EventStatus> {

	free("FREE"), //
	pending("PENDING"), //
	reserved("RESERVED"), //
	blocked("BLOCKED")
	;

	private final Serializable entityId;

	private EventStatusEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.mars.basemanagement.domain.EventStatus> getEntityUID() {
		return UID.of(io.mars.basemanagement.domain.EventStatus.class, entityId);
	}

}

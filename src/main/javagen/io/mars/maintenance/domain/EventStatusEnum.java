package io.mars.maintenance.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum EventStatusEnum implements MasterDataEnum<io.mars.maintenance.domain.EventStatus> {

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
	public UID<io.mars.maintenance.domain.EventStatus> getEntityUID() {
		return UID.of(io.mars.maintenance.domain.EventStatus.class, entityId);
	}

}

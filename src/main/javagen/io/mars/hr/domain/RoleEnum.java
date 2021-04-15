package io.mars.hr.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum RoleEnum implements MasterDataEnum<io.mars.hr.domain.Role> {

	admin("ADMIN"), //
	manager("MANAG"), //
	engineer("ENGIN"), //
	trainee("WORKE")
	;

	private final Serializable entityId;

	private RoleEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.mars.hr.domain.Role> getEntityUID() {
		return UID.of(io.mars.hr.domain.Role.class, entityId);
	}

}

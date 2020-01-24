package io.mars.catalog.services.equipment;

import javax.inject.Inject;

import io.mars.catalog.dao.EquipmentTypeDAO;
import io.mars.catalog.domain.EquipmentType;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class EquipmentTypeServices implements Component {

	@Inject
	private EquipmentTypeDAO equipmentTypeDAO;

	public EquipmentType getEquipmentTypeFromId(final Long equipmentTypeId) {
		return equipmentTypeDAO.get(equipmentTypeId);
	}

	public void saveEquipmentType(final EquipmentType equipmentType) {
		equipmentTypeDAO.save(equipmentType);
	}

	public DtList<EquipmentType> getEquipmentTypes(final DtListState dtListState) {
		return equipmentTypeDAO.findAll(Criterions.alwaysTrue(), dtListState);
	}

}

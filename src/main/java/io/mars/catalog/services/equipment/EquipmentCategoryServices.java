package io.mars.catalog.services.equipment;

import javax.inject.Inject;

import io.mars.catalog.dao.EquipmentCategoryDAO;
import io.mars.catalog.domain.EquipmentCategory;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class EquipmentCategoryServices implements Component {

	@Inject
	private EquipmentCategoryDAO equipmentCategoryDAO;

	public EquipmentCategory getEquipmentCategoryFromId(final Long equipmentCategoryId) {
		return equipmentCategoryDAO.get(equipmentCategoryId);
	}

	public void saveEquipmentCategory(final EquipmentCategory equipmentCategory) {
		equipmentCategoryDAO.save(equipmentCategory);
	}

	public DtList<EquipmentCategory> getEquipmentCategories(final DtListState dtListState) {
		return equipmentCategoryDAO.findAll(Criterions.alwaysTrue(), dtListState);
	}

}

package io.mars.catalog.services.equipment;

import javax.inject.Inject;

import org.codehaus.commons.compiler.util.Producer;

import io.mars.authorization.SecuredEntities;
import io.mars.basemanagement.domain.Equipment;
import io.mars.catalog.dao.EquipmentCategoryDAO;
import io.mars.catalog.domain.EquipmentCategory;
import io.vertigo.account.authorization.AuthorizationUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

@Transactional
public class EquipmentCategoryServices implements Component {

	@Inject
	private EquipmentCategoryDAO equipmentCategoryDAO;

	public EquipmentCategory getEquipmentCategoryFromId(final Long equipmentCategoryId) {
		return equipmentCategoryDAO.get(equipmentCategoryId);
	}

	public void saveEquipmentCategory(final EquipmentCategory equipmentCategory, final Producer<Long> mfoSaver) {
		final Long efoId = mfoSaver.produce();
		equipmentCategory.setEfoId(efoId);
		equipmentCategoryDAO.save(equipmentCategory);
	}

	public DtList<EquipmentCategory> getEquipmentCategories(final DtListState dtListState) {
		return equipmentCategoryDAO.findAll(Criterions.alwaysTrue(), dtListState);
	}

	public EquipmentCategory getEquipmentCategoryFromEquipmentId(final Long equipmentId) {
		return equipmentCategoryDAO.getEquipmentCategoryFromEquipmentId(equipmentId, AuthorizationUtil.authorizationCriteria(Equipment.class, SecuredEntities.EquipmentOperations.read));
	}
}

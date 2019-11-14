package io.mars.basemanagement.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.mars.basemanagement.domain.EquipmentFeature;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class EquipmentFeatureDAO extends DAO<EquipmentFeature, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public EquipmentFeatureDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(EquipmentFeature.class, storeManager, taskManager);
	}

}

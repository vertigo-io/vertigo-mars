package io.mars.basemanagement.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.task.TaskManager;
import io.mars.basemanagement.domain.Picture;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class PictureDAO extends DAO<Picture, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public PictureDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(Picture.class, entityStoreManager, taskManager, smartTypeManager);
	}

}

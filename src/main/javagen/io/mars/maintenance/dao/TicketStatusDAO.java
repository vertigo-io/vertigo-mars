package io.mars.maintenance.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.mars.maintenance.domain.TicketStatus;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TicketStatusDAO extends DAO<TicketStatus, java.lang.String> implements StoreServices {

	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public TicketStatusDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(TicketStatus.class, storeManager, taskManager);
	}

}

package io.mars.maintenance.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.mars.maintenance.domain.WorkOrder;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class WorkOrderDAO extends DAO<WorkOrder, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public WorkOrderDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager) {
		super(WorkOrder.class, entityStoreManager, taskManager);
	}


	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Home.getApp().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkGetLastWorkOrders.
	 * @return DtList de WorkOrder workOrders
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.mars.maintenance.domain.WorkOrder> getLastWorkOrders() {
		final Task task = createTaskBuilder("TkGetLastWorkOrders")
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}

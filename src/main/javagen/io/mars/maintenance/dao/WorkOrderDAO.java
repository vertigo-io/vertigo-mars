package io.mars.maintenance.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.task.TaskManager;
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
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public WorkOrderDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(WorkOrder.class, entityStoreManager, taskManager, smartTypeManager);
	}


	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Node.getNode().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkGetLastWorkOrders.
	 * @return DtList de WorkOrder workOrders
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkGetLastWorkOrders",
			request = "select \n" + 
 "             	wor.*\n" + 
 " 			from work_order wor\n" + 
 " 			order by wor.date_created desc\n" + 
 " 			limit 20",
			taskEngineClass = io.vertigo.basics.task.TaskEngineSelect.class)
	@io.vertigo.datamodel.task.proxy.TaskOutput(smartType = "STyDtWorkOrder", name = "workOrders")
	public io.vertigo.datamodel.structure.model.DtList<io.mars.maintenance.domain.WorkOrder> getLastWorkOrders() {
		final Task task = createTaskBuilder("TkGetLastWorkOrders")
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}

package io.mars.maintenance.dao;

import javax.inject.Inject;

import io.vertigo.app.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.mars.maintenance.domain.Ticket;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TicketDAO extends DAO<Ticket, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public TicketDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(Ticket.class, storeManager, taskManager);
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
	 * Execute la tache TkGetLastTickets.
	 * @return DtList de Ticket tickets
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.mars.maintenance.domain.Ticket> getLastTickets() {
		final Task task = createTaskBuilder("TkGetLastTickets")
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetLastTicketsByBaseId.
	 * @param baseId Long 
	 * @return DtList de Ticket tickets
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.mars.maintenance.domain.Ticket> getLastTicketsByBaseId(final Long baseId) {
		final Task task = createTaskBuilder("TkGetLastTicketsByBaseId")
				.addValue("baseId", baseId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

}

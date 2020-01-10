package io.mars.hr.dao;

import javax.inject.Inject;

import java.util.Optional;
import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.mars.hr.domain.Person;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class PersonDAO extends DAO<Person, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public PersonDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager) {
		super(Person.class, entityStoreManager, taskManager);
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
	 * Execute la tache TkGetBaseManager.
	 * @param baseId Long
	 * @return Option de Person manager
	*/
	public Optional<io.mars.hr.domain.Person> getBaseManager(final Long baseId) {
		final Task task = createTaskBuilder("TkGetBaseManager")
				.addValue("baseId", baseId)
				.build();
		return Optional.ofNullable((io.mars.hr.domain.Person) getTaskManager()
				.execute(task)
				.getResult());
	}

}

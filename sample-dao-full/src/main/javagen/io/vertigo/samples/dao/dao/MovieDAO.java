package io.vertigo.samples.dao.dao;

import javax.inject.Inject;
import java.util.Optional;
import io.vertigo.app.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.vertigo.samples.dao.domain.Movie;

/**
 * DAO : Accès à un object (DTO, DTC). 
 * MovieDAO
 */
public final class MovieDAO extends DAO<Movie, java.lang.Long> implements StoreServices {
	 
	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public MovieDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(Movie.class, storeManager, taskManager);
	}
	

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Home.getApp().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return new TaskBuilder(taskDefinition);
	}

	/**
	 * Execute la tache TK_GET_MOVIES_BY_CRITERIA.
	 * @param title String 
	 * @param year Integer 
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> movies
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> getMoviesByCriteria(final String title, final Integer year) {
		final Task task = createTaskBuilder("TK_GET_MOVIES_BY_CRITERIA")
				.addValue("TITLE", title)
				.addValue("YEAR", year)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TK_GET_MOVIES_BY_CRITERIA_WITH_COUNTRY.
	 * @param title String 
	 * @param year Integer (peut être null)
	 * @param countries io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Country> 
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> movies
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> getMoviesByCriteriaWithCountry(final String title, final Optional<Integer> year, final io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Country> countries) {
		final Task task = createTaskBuilder("TK_GET_MOVIES_BY_CRITERIA_WITH_COUNTRY")
				.addValue("TITLE", title)
				.addValue("YEAR", year.orElse(null))
				.addValue("COUNTRIES", countries)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TK_GET_MOVIES_WITH_100_ACTORS.
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> movies
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> getMoviesWith100Actors() {
		final Task task = createTaskBuilder("TK_GET_MOVIES_WITH_100_ACTORS")
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TK_LOAD_MOVIES_BY_CHUNK.
	 * @param limit Long 
	 * @param offset Long 
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> moviesList
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.samples.dao.domain.Movie> loadMoviesByChunk(final Long limit, final Long offset) {
		final Task task = createTaskBuilder("TK_LOAD_MOVIES_BY_CHUNK")
				.addValue("LIMIT", limit)
				.addValue("OFFSET", offset)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}


}
package eu.vranckaert.worktime.dao;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.User;

public interface TaskDao extends BaseDao<Task> {
	@Deprecated
	@Override
	List<Task> findAll();
	
	@Deprecated
	@Override
	Task findById(Object id);
	
	/**
	 * Find a list of all {@link Task}s for a specific user.
	 * @param user The user for which to retrieve the tasks.
	 * @return All {@link Task}s linked to the specified user.
	 */
	List<Task> findAll(User user);
	
	/**
	 * Find a task for a certain user based on it's name.
	 * @param name The name of the task.
	 * @param user The user for which to retrieve the task.
	 * @return The {@link Task} that qualifies or null if none found.
	 */
	Task find(String name, User user);
	
	/**
	 * Find a task for a certain user based on it's synchronization key.
	 * @param syncKey The synchronization key for which to look.
	 * @param user The user for which to retrieve the task.
	 * @return The {@link Task} that qualifies or null if none found.
	 */
	Task findBySyncKey(String syncKey, User user);
	
	/**
	 * Checks if the provided synchronization key is already used for a 
	 * {@link Task} or not.
	 * @param syncKey The synchronization key to check uniqueness for.
	 * @param user The user to which the tasks should belong.
	 * @return {@link Boolean#TRUE} if the provided synchronization key is 
	 * unique. Otherwise {@link Boolean#FALSE}.
	 */
	boolean isUniqueSynKey(String syncKey, User user);
	
	/**
	 * Search for all {@link Task}s that have been modified on or after a certain
	 * date.
	 * @param user The user for which to retrieve the tasks.
	 * @param lastModifiedDate The date after which (or on which) the tasks
	 * should be modified.
	 * @return A list of {@link Task}s that are modified after the 
	 * provided date.
	 */
	List<Task> findAllModifiedAfter(User user, Date lastSuccessfulSyncDate);
}

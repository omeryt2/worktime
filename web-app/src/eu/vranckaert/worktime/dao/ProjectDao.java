package eu.vranckaert.worktime.dao;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.User;

public interface ProjectDao extends BaseDao<Project> {
	@Deprecated
	@Override
	List<Project> findAll();
	
	@Deprecated
	@Override
	Project findById(Object id);
	
	/**
	 * Find a list of all {@link Project}s for a specific user.
	 * @param user The user for which to retrieve the projects.
	 * @return All {@link Project}s linked to the specified user.
	 */
	List<Project> findAll(User user);
	
	/**
	 * Find a project for a certain user based on it's name.
	 * @param name The name of the project.
	 * @param user The user for which to retrieve the project.
	 * @return The {@link Project} that qualifies or null if none found.
	 */
	Project find(String name, User user);
	
	/**
	 * Find a project for a certain user based on it's synchronization key.
	 * @param syncKey The synchronization key for which to look.
	 * @param user The user for which to retrieve the project.
	 * @return The {@link Project} that qualifies or null if none found.
	 */
	Project findBySyncKey(String syncKey, User user);
	
	/**
	 * Checks if the provided synchronization key is already used for a 
	 * {@link Project} or not.
	 * @param syncKey The synchronization key to check uniqueness for.
	 * @param user The user to which the projects should belong.
	 * @return {@link Boolean#TRUE} if the provided synchronization key is 
	 * unique. Otherwise {@link Boolean#FALSE}.
	 */
	boolean isUniqueSynKey(String syncKey, User user);
	
	/**
	 * Search for all {@link Project}s that have been modified on or after a 
	 * certain date.
	 * @param user The user for which to retrieve the projects.
	 * @param lastModifiedDate The date after which (or on which) the projects
	 * should be modified.
	 * @return A list of {@link Project}s that are modified after the 
	 * provided date.
	 */
	List<Project> findAllModifiedAfter(User user, Date lastModifiedDate);
}

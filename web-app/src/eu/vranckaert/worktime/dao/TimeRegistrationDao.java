package eu.vranckaert.worktime.dao;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;

public interface TimeRegistrationDao extends BaseDao<TimeRegistration> {
	@Deprecated
	@Override
	List<TimeRegistration> findAll();
	
	@Deprecated
	@Override
	TimeRegistration findById(Object id);
	
	/**
	 * Find a list of all {@link TimeRegistration}s for a specific user.
	 * @param user The user for which to retrieve the time registrations.
	 * @return All {@link TimeRegistration}s linked to the specified user.
	 */
	List<TimeRegistration> findAll(User user);
	
	/**
	 * Find a time registration for a certain user based on it's start- and
	 * end-date.
	 * @param startDate The starting date of the time registration.
	 * @param endDate The ending date of the time registration (can be null if
	 * you are looking for an ongoing time registration).
	 * @param user The user for which to retrieve the time registration. 
	 * @return The {@link TimeRegistration} that qualifies or null if none 
	 * found.
	 */
	TimeRegistration find(Date startDate, Date endDate, User user);
	
	/**
	 * Find a time registration for a certain user based on it's synchronization
	 * key.
	 * @param syncKey The synchronization key for which to look.
	 * @param user The user for which to retrieve the time registration.
	 * @return The {@link TimeRegistration} that qualifies or null if none 
	 * found.
	 */
	TimeRegistration findBySyncKey(String syncKey, User user);
	
	/**
	 * Find all {@link TimeRegistration}s that interfere with the provided one.
	 * @param timeRegistration The time registrations that all resulting time
	 * registrations will interfere with.
	 * @param user The user for which to retrieve the time registrations.
	 * @return A list of {@link TimeRegistration}s that interfere, based on 
	 * start and end time, with the provided time registration.
	 */
	List<TimeRegistration> findInterferingTimeRegistrations(TimeRegistration timeRegistration, User user);
	
	/**
	 * Checks if the provided synchronization key is already used for a 
	 * {@link TimeRegistration} or not.
	 * @param syncKey The synchronization key to check uniqueness for.
	 * @param user The user to which the time registrations should belong.
	 * @return {@link Boolean#TRUE} if the provided synchronization key is 
	 * unique. Otherwise {@link Boolean#FALSE}.
	 */
	boolean isUniqueSynKey(String syncKey, User user);
	
	/**
	 * Search for all {@link TimeRegistration}s that have been modified on or 
	 * after a certain date.
	 * @param user The user for which to retrieve the time registrations.
	 * @param lastModifiedDate The date after which (or on which) the time 
	 * registrations should be modified.
	 * @return A list of {@link TimeRegistration}s that are modified after the 
	 * provided date.
	 */
	List<TimeRegistration> findAllModifiedAfter(User user, Date lastModifiedDate);
}

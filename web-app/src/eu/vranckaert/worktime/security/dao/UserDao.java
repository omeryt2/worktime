package eu.vranckaert.worktime.security.dao;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.model.User;

public interface UserDao extends BaseDao<User> {
	/**
	 * Checks if a certain email address is already in use...
	 * @param email The email address to count on.
	 * @return True if the email is already in use, false otherwise.
	 */
	boolean isEmailAlreadyInUse(String email);

	/**
	 * Find all {@link User}s that have registered within certain boundaries.
	 * @param minDate The minimum date that the user should have registered.
	 * @param maxDate The maximum date that the user should have registered.
	 * @param includeBoundaries If the boundaries should be included or excluded.
	 * @return The list of matching {@link User}s.
	 */
	List<User> findUsersRegiseredBetween(Date minDate, Date maxDate, boolean includeBoundaries);
}

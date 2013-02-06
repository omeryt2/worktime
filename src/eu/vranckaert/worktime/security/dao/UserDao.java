package eu.vranckaert.worktime.security.dao;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.model.User;

public interface UserDao extends BaseDao<User> {
	/**
	 * Checks if a certain email address is already in use...
	 * @param email The email address to count on.
	 * @return True if the email is already in use, false otherwise.
	 */
	boolean isEmailAlreadyInUse(String email);
}

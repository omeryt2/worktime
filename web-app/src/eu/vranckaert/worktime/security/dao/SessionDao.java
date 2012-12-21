package eu.vranckaert.worktime.security.dao;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.model.Session;
import eu.vranckaert.worktime.model.User;

public interface SessionDao extends BaseDao<Session> {
	/**
	 * Removes all the sessions for a specific user. Meaning that the user will
	 * be logged out on all platforms/browsers/apps/...
	 * @param user The user for which to remove all the sessions.
	 */
	void removeAllSessions(User user);

	/**
	 * Removes a certain {@link Session} from the database only if the session
	 * key matches with the provided {@link User}.
	 * @param user The user.
	 * @param sessionKey The session key.
	 */
	void removeSession(User user, String sessionKey);
}

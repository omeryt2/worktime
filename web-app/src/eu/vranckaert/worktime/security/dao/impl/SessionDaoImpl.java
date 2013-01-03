package eu.vranckaert.worktime.security.dao.impl;

import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.collect.Lists;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.Session;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.SessionDao;

public class SessionDaoImpl extends BaseDaoImpl<Session> implements SessionDao {
	public SessionDaoImpl() {
		super(Session.class);
	}

	@Override
	public void removeAllSessions(User user) {
		if (user.getSessions() != null) {
			for (Session session : user.getSessions()) {
				List<Session> sessionKeys = Lists.newArrayList(getDataStore().find().type(Session.class).addFilter("sessionKey", FilterOperator.EQUAL, session.getSessionKey()).returnResultsNow());
				for (Session sessionKey : sessionKeys) {
					if (sessionKey.equals(session)) {
						remove(sessionKey);
					}
				}
			}
		}
	}

	@Override
	public void removeSession(User user, String sessionKey) {
		Session userSession = null;
		if (user.getSessions() != null) {
			for (Session session : user.getSessions()) {
				if (session.getSessionKey().equals(sessionKey)) {
					userSession = session;
					break;
				}
			}
		}
		
		if (userSession != null) {
			List<Session> sessionKeys = Lists.newArrayList(getDataStore().find().type(Session.class).addFilter("sessionKey", FilterOperator.EQUAL, userSession.getSessionKey()).returnResultsNow());
			for (Session s : sessionKeys) {
				if (s.equals(userSession)) {
					remove(s);
					break;
				}
			}
		}
	}
}

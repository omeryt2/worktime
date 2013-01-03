package eu.vranckaert.worktime.security.dao.impl;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.UserDao;

public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

	public UserDaoImpl() {
		super(User.class);
	}

	@Override
	public boolean isEmailAlreadyInUse(String email) {
		User user = getDataStore().load(User.class, email);
		return user == null ? false : true;
	}
}

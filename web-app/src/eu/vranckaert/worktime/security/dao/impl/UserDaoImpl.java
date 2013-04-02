package eu.vranckaert.worktime.security.dao.impl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.Session;
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

	@Override
	public List<User> findUsersRegiseredBetween(Date minDate, Date maxDate, boolean includeBoundaries) {
		FilterOperator filterLowerThen = FilterOperator.LESS_THAN;
		FilterOperator filterHigherThen = FilterOperator.GREATER_THAN;
		
		if (includeBoundaries) {
			filterLowerThen = FilterOperator.LESS_THAN_OR_EQUAL;
			filterHigherThen = FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		
		List<User> users = getDataStore().find().type(User.class)
				.addFilter("registrationDate", filterHigherThen, minDate)
				.addFilter("registrationDate", filterLowerThen, maxDate)
				.returnAll().now();
		
		return users;
	}
}

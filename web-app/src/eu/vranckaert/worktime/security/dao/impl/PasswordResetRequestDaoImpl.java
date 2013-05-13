package eu.vranckaert.worktime.security.dao.impl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.PasswordResetRequest;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.PasswordResetRequestDao;

public class PasswordResetRequestDaoImpl extends
		BaseDaoImpl<PasswordResetRequest> implements PasswordResetRequestDao {

	public PasswordResetRequestDaoImpl() {
		super(PasswordResetRequest.class);
	}

	@Override
	public int countAllForDateRange(Date minDate, Date maxDate, boolean includeBoundaries) {
		FilterOperator filterLowerThen = FilterOperator.LESS_THAN;
		FilterOperator filterHigherThen = FilterOperator.GREATER_THAN;
		
		if (includeBoundaries) {
			filterLowerThen = FilterOperator.LESS_THAN_OR_EQUAL;
			filterHigherThen = FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		
		int count = getDataStore().find().type(PasswordResetRequest.class)
				.addFilter("requestDate", filterHigherThen, minDate)
				.addFilter("requestDate", filterLowerThen, maxDate)
				.returnCount().now();
		return count;
	}

	@Override
	public int countAllUsedPasswordRequests() {
		int count = getDataStore().find().type(PasswordResetRequest.class)
				.addFilter("used", FilterOperator.EQUAL, true)
				.returnCount().now();
		return count;
	}

	@Override
	public int countAllUsedPasswordRequestsForDay(Date minDate, Date maxDate,
			boolean includeBoundaries) {
		FilterOperator filterLowerThen = FilterOperator.LESS_THAN;
		FilterOperator filterHigherThen = FilterOperator.GREATER_THAN;
		
		if (includeBoundaries) {
			filterLowerThen = FilterOperator.LESS_THAN_OR_EQUAL;
			filterHigherThen = FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		
		int count = getDataStore().find().type(PasswordResetRequest.class)
				.addFilter("used", FilterOperator.EQUAL, true)
				.addFilter("requestDate", filterHigherThen, minDate)
				.addFilter("requestDate", filterLowerThen, maxDate)
				.returnCount().now();
		return count;
	}

	@Override
	public int countAllOpenPasswordRequests() {
		int count = getDataStore().find().type(PasswordResetRequest.class)
				.addFilter("used", FilterOperator.EQUAL, false)
				.returnCount().now();
		return count;
	}
}

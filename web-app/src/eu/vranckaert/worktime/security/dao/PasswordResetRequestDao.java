package eu.vranckaert.worktime.security.dao;

import java.util.Date;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.model.PasswordResetRequest;

public interface PasswordResetRequestDao extends BaseDao<PasswordResetRequest> {
	/**
	 * Count all {@link PasswordResetRequest}s within a certain date range.
	 * @param minDate The minimum date that the request should be created in.
	 * @param maxDate The maximum date that the request should be created in.
	 * @param includeBoundaries If the boundaries should be included or excluded.
	 * @return The count of all records matching the criteria.
	 */
	int countAllForDateRange(Date minDate, Date maxDate, boolean includeBoundaries);

	/**
	 * Count all {@link PasswordResetRequest} that have been used already.
	 * @return The count of all records matching the criteria.
	 */
	int countAllUsedPasswordRequests();

	/**
	 * Count all {@link PasswordResetRequest}s within a certain date range that have been used already.
	 * @param minDate The minimum date that the request should be created in.
	 * @param maxDate The maximum date that the request should be created in.
	 * @param includeBoundaries If the boundaries should be included or excluded.
	 * @return The count of all records matching the criteria.
	 */
	int countAllUsedPasswordRequestsForDay(Date minDate, Date maxDate, boolean includeBoundaries);

	/**
	 * Count all {@link PasswordResetRequest} that are not used yet.
	 * @return The count of all records matching the criteria.
	 */
	int countAllOpenPasswordRequests();
}

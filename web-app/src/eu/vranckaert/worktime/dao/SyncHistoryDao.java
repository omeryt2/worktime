package eu.vranckaert.worktime.dao;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;

public interface SyncHistoryDao extends BaseDao<SyncHistory> {
	/**
	 * Checks if a user has synced before or not or if it's the first time!
	 * @param user The user for which to look for the synchronization history.
	 * @return {@link Boolean#TRUE} if the user has synced before, if not
	 * {@link Boolean#FALSE}.
	 */
	boolean hasSyncHistory(User user);
	
	/**
	 * Searches for any ongoing sync-history records.
	 * @param user The user for which to look for the synchronization history.
	 * @return The ongoing {@link SyncHistory} or null if no ongoing is found.
	 */
	SyncHistory getOngoingSyncHistory(User user);

	List<SyncHistory> findSyncsBetween(Date dayWithMinimalTimeValues,
			Date dayWithMaximumTimeValues, boolean b);
}

package eu.vranckaert.worktime.dao.impl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import eu.vranckaert.worktime.dao.SyncHistoryDao;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;
import eu.vranckaert.worktime.model.sync.SyncResult;

public class SyncHistoryDaoImpl extends BaseDaoImpl<SyncHistory> implements SyncHistoryDao {

	public SyncHistoryDaoImpl() {
		super(SyncHistory.class);
	}

	@Override
	public boolean hasSyncHistory(User user) {
		int results = getDataStore().find()
				.type(SyncHistory.class)
				.addFilter("userEmail", FilterOperator.EQUAL, user.getEmail())
				.returnCount()
				.now();
		
		if (results == 0) 
			return false;
		else
			return true;
	}
	
	@Override
	public SyncHistory getOngoingSyncHistory(User user) {
		List<SyncHistory> syncHistories = getDataStore().find()
				.type(SyncHistory.class)
				.addFilter("userEmail", FilterOperator.EQUAL, user.getEmail())
				.addFilter("syncResult", FilterOperator.EQUAL, SyncResult.BUSY)
				.addSort("startTime", SortDirection.DESCENDING)
				.returnAll()
				.now();
		if (syncHistories != null && syncHistories.size() > 0)
			return syncHistories.get(0);
		return null;
	}

	@Override
	public List<SyncHistory> findSyncsBetween(Date minDate, Date maxDate, boolean includeBoundaries) {
		FilterOperator filterLowerThen = FilterOperator.LESS_THAN;
		FilterOperator filterHigherThen = FilterOperator.GREATER_THAN;
		
		if (includeBoundaries) {
			filterLowerThen = FilterOperator.LESS_THAN_OR_EQUAL;
			filterHigherThen = FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		
		List<SyncHistory> syncHistories = getDataStore().find().type(SyncHistory.class)
				.addFilter("startTime", filterHigherThen, minDate)
				.addFilter("startTime", filterLowerThen, maxDate)
				.returnAll().now();
		
		return syncHistories;
	}

}

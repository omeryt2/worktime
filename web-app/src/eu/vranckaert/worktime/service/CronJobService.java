package eu.vranckaert.worktime.service;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;

/**
 * 
 * @author Dirk Vranckaert
 */
public interface CronJobService {
	List<User> findUsersRegisteredOnDay(Date date);

	List<SyncHistory> findSyncsOnDay(Date time);
}

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

	int countTimeRegistrations();

	int countProjects();

	int countTasks();

	int countAllPasswordRequests();

	int countAllPasswordRequestsForDay(Date time);

	int countAllUsedPasswordRequests();

	int countAllUsedPasswordRequestsForDay(Date time);

	int countAllOpenPasswordRequests();
}

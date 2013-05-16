package eu.vranckaert.worktime.service.impl;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.SyncHistoryDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;
import eu.vranckaert.worktime.security.dao.PasswordResetRequestDao;
import eu.vranckaert.worktime.security.dao.UserDao;
import eu.vranckaert.worktime.service.CronJobService;
import eu.vranckaert.worktime.util.DateUtil;

public class CronJobServiceImpl implements CronJobService {
	@Inject private UserDao userDao;
	@Inject private SyncHistoryDao syncHistoryDao;
	@Inject private TimeRegistrationDao timeRegistrationDao;
	@Inject private ProjectDao projectDao;
	@Inject private TaskDao taskDao;
	@Inject private PasswordResetRequestDao passwordResetRequestDao;

	@Override
	public List<User> findUsersRegisteredOnDay(Date date) {
		return userDao.findUsersRegiseredBetween(
				DateUtil.getDayWithMinimalTimeValues(date),
				DateUtil.getDayWithMaximumTimeValues(date),
				true
		);
	}

	@Override
	public List<SyncHistory> findSyncsOnDay(Date date) {
		return syncHistoryDao.findSyncsBetween(
				DateUtil.getDayWithMinimalTimeValues(date),
				DateUtil.getDayWithMaximumTimeValues(date),
				true
		);
	}
	
	@Override
	public int countTimeRegistrations() {
		return timeRegistrationDao.countAll();
	}
	
	@Override
	public int countProjects() {
		return timeRegistrationDao.countAll();
	}
	
	@Override
	public int countTasks() {
		return timeRegistrationDao.countAll();
	}

	@Override
	public int countAllPasswordRequests() {
		return passwordResetRequestDao.countAll();
	}

	@Override
	public int countAllPasswordRequestsForDay(Date date) {
		return passwordResetRequestDao.countAllForDateRange(DateUtil.getDayWithMinimalTimeValues(date), DateUtil.getDayWithMaximumTimeValues(date), true);
	}

	@Override
	public int countAllUsedPasswordRequests() {
		return passwordResetRequestDao.countAllUsedPasswordRequests();
	}

	@Override
	public int countAllUsedPasswordRequestsForDay(Date date) {
		return passwordResetRequestDao.countAllUsedPasswordRequestsForDay(DateUtil.getDayWithMinimalTimeValues(date), DateUtil.getDayWithMaximumTimeValues(date), true);
	}

	@Override
	public int countAllOpenPasswordRequests() {
		return passwordResetRequestDao.countAllOpenPasswordRequests();
	}

}

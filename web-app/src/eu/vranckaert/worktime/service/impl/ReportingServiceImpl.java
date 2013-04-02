package eu.vranckaert.worktime.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.UserDao;
import eu.vranckaert.worktime.service.ReportingService;
import eu.vranckaert.worktime.util.DateUtil;

public class ReportingServiceImpl implements ReportingService {
	@Inject private UserDao userDao;

	@Override
	public List<User> findUsersRegisteredOnDay(Date date) {
		return userDao.findUsersRegiseredBetween(
				DateUtil.getDayWithMinimalTimeValues(date),
				DateUtil.getDayWithMaximumTimeValues(date),
				true
		);
	}

}

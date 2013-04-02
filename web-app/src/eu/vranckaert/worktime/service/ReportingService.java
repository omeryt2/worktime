package eu.vranckaert.worktime.service;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.User;

/**
 * 
 * @author Dirk Vranckaert
 */
public interface ReportingService {
	List<User> findUsersRegisteredOnDay(Date date);
}

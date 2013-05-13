package eu.vranckaert.worktime.cron.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.vranckaert.worktime.dao.SyncHistoryDao;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.service.CronJobService;
import eu.vranckaert.worktime.util.DateUtil;
import eu.vranckaert.worktime.util.EmailUtil;

public class ReportNewUsersServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ReportNewUsersServlet.class.getName());
	
	@Inject private UserService userService;
	@Inject private CronJobService cronJobService;
	@Inject private SyncHistoryDao syncHistoryDao;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
		List<User> allUsers = userService.findAll();
		List<User> usersRegisteredYesterday = cronJobService.findUsersRegisteredOnDay(yesterday.getTime());
		
		List<SyncHistory> allSyncHistories = syncHistoryDao.findAll();
		List<SyncHistory> syncHistoriesYesterday = cronJobService.findSyncsOnDay(yesterday.getTime());
		
		int busy = 0;
		int success = 0;
		int interrupted = 0;
		int failure = 0;
		int timeout = 0;
		
		int syncedTimeRegistrationsYesterday = 0;
		int syncedTasksYesterday = 0;
		int syncedProjectsYesterday = 0;
		
		long averageSyncTimeMillis = 0L;
		long averageSyncTimeMillisYesterday = 0L;
		
		List<String> activeUsersBySync = new ArrayList<String>();
		List<String> activeUsersBySyncYesterday = new ArrayList<String>();
		
		for (SyncHistory syncHistory : allSyncHistories) {
			if (syncHistory.getEndTime() != null) {
				averageSyncTimeMillis += (syncHistory.getEndTime().getTime() - syncHistory.getStartTime().getTime());
				
				if (!activeUsersBySync.contains(syncHistory.getUserEmail())) {
					activeUsersBySync.add(syncHistory.getUserEmail());
				}
			}
		}
		
		for (SyncHistory syncHistory : syncHistoriesYesterday) {
			syncedTimeRegistrationsYesterday += syncHistory.getSyncedTimeRegistrations();
			syncedTasksYesterday += syncHistory.getSyncedTasks();
			syncedProjectsYesterday += syncHistory.getSyncedProjects();
			
			if (syncHistory.getEndTime() != null) {
				averageSyncTimeMillisYesterday += (syncHistory.getEndTime().getTime() - syncHistory.getStartTime().getTime());
			}
			
			if (!activeUsersBySyncYesterday.contains(syncHistory.getUserEmail())) {
				activeUsersBySyncYesterday.add(syncHistory.getUserEmail());
			}
			
			switch (syncHistory.getSyncResult()) {
			case BUSY:
				busy++;
				break;
			case SUCCESS:
				success++;
				break;
			case INTERRUPTED:
				interrupted++;
				break;
			case FAILURE:
				failure++;
				break;
			case TIME_OUT:
				timeout++;
				break;
			}
		}
		
		if (allSyncHistories.size() > 0) {
			averageSyncTimeMillis = averageSyncTimeMillis / allSyncHistories.size();
		}
		if (syncHistoriesYesterday.size() > 0) {
			averageSyncTimeMillisYesterday = averageSyncTimeMillisYesterday / syncHistoriesYesterday.size();
		}
		
		int countTimeRegistrations = cronJobService.countTimeRegistrations();
		int countTasks = cronJobService.countTasks();
		int countProjects = cronJobService.countProjects();
		
		int totalCreatedPasswordRequests = cronJobService.countAllPasswordRequests();
		int totalCreatedPasswordRequestsYesterday = cronJobService.countAllPasswordRequestsForDay(yesterday.getTime());
		int totalUsedPasswordRequests = cronJobService.countAllUsedPasswordRequests();
		int totalUsedPasswordRequestsYesterday = cronJobService.countAllUsedPasswordRequestsForDay(yesterday.getTime());
		int totalOpenPasswordRequests = cronJobService.countAllOpenPasswordRequests();
		
		String html = "<html><head><style>" +
					"table {" +
						"border-collapse:collapse;" +
					"}" +
					"table, td, th {" +
						"border:1px solid black;" +
					"}" +
				"</style></head><body>";
		html += "<b><u>Data Count</u></b><br/>" +
				"<br/>" +
				"<table>" +
					"<tr><th></th><th>Total</th><th>Yesterday</th></tr>" +
					"<tr><td>Users</td><td>" + allUsers.size() + "</td><td>" + usersRegisteredYesterday.size() + "</td></tr>" +
					"<tr><td>Active users (based on syncs)</td><td>" + activeUsersBySync.size() + "</td><td>" + activeUsersBySyncYesterday.size() + "</td></tr>" +
					"<tr><td>Syncs</td><td>" + allSyncHistories.size() + "</td><td>" + syncHistoriesYesterday.size() + "</td></tr>" +
					"<tr><td>Time Registrations</td><td>" + countTimeRegistrations + "</td><td>" + syncedTimeRegistrationsYesterday + "</td></tr>" +
					"<tr><td>Tasks</td><td>" + countTasks + "</td><td>" + syncedTasksYesterday + "</td></tr>" +
					"<tr><td>Projects</td><td>" + countProjects + "</td><td>" + syncedProjectsYesterday + "</td></tr>" +
				"</table>" +
				"<br/>" +
				"<b><u>Yesterday Sync Result Overview</u></b><br/>" + 
				"<br/>" +
						"<table>" +
						"	<tr><th>Status</th><th>Result</th></tr>" +
						"	<tr><td>SUCCESS</td><td>"  + success + "</td></tr>" +
						"	<tr><td>INTERRUPTED</td><td>"  + interrupted + "</td></tr>" +
						"	<tr><td>FAILURE</td><td>"  + failure + "</td></tr>" +
						"	<tr><td>BUSY</td><td>"  + busy + "</td></tr>" +
						"	<tr><td>TIMEOUT</td><td>"  + timeout + "</td></tr>" +
						"</table>" +
						"<br/>" +
						"<b><u>Average Sync Duration</u></b><br/>" + 
						"<br/>" +
						"<table>" +
						"	<tr><th></th><th>Millis</th><th>Seconds</th><th>Minutes</th></tr>" +
						"	<tr><td>Yesterday</td><td>"  + averageSyncTimeMillisYesterday + "</td><td>"  + DateUtil.getSecondsFromMillis(averageSyncTimeMillisYesterday) + "</td><td>"  + DateUtil.getMinutesFromMillis(averageSyncTimeMillisYesterday) + "</td></tr>" +
						"	<tr><td>All Times</td><td>"  + averageSyncTimeMillis + "</td><td>"  + DateUtil.getSecondsFromMillis(averageSyncTimeMillis) + "</td><td>"  + DateUtil.getMinutesFromMillis(averageSyncTimeMillis) + "</td></tr>" +
						"</table>" +
						"<br/>" +
						"<b><u>Password Reset Requests</u></b><br/>" +
						"<br/>" +
						"<table>" +
							"<tr><th></th><th>Total</th><th>Yesterday</th></tr>" +
							"<tr><td>Created</td><td>" + totalCreatedPasswordRequests + "</td><td>" + totalCreatedPasswordRequestsYesterday + "</td></tr>" +
							"<tr><td>Used</td><td>" + totalUsedPasswordRequests + "</td><td>" + totalUsedPasswordRequestsYesterday + "</td></tr>" +
							"<tr><td>Open</td><td>" + totalOpenPasswordRequests + "</td><td>N/A</td></tr>" +
						"</table>" +
						"<br/>" +
						"Please do not reply to this mail as this is an auto generated message and you will never receive any response!";
		html += "</body></html>";
		
		log.info("The message to be sent is:");
		log.info(html);
		
		List<User> recipients = new ArrayList<User>();
		User user = new User();
		user.setEmail("dirkvranckaert@gmail.com");
		user.setFirstName("Dirk");
		user.setLastName("Vranckaert");
		recipients.add(user);
		EmailUtil.sendEmail("WorkTime Reporting", html, "text/html", recipients);
	}
}

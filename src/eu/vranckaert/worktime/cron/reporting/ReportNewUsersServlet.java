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
		
		for (SyncHistory syncHistory : syncHistoriesYesterday) {
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
		
		String body = "Total number of users / Number of registrations yesterday: " + allUsers.size() + " / " + usersRegisteredYesterday.size() + "<br/>" +
						"<br/>" +
						"Number of syncs in total / Number of syncs yesterday: " + allSyncHistories.size() + " / " + syncHistoriesYesterday.size() + "<br/>" +
						"<br/>" +
						"<table>" +
						"	<th><td>Status</td><td>Result</td></th>" +
						"	<tr><td>SUCCESS</td><td>"  + success + "</td></tr>" +
						"	<tr><td>INTERRUPTED</td><td>"  + interrupted + "</td></tr>" +
						"	<tr><td>FAILURE</td><td>"  + failure + "</td></tr>" +
						"	<tr><td>BUSY</td><td>"  + busy + "</td></tr>" +
						"	<tr><td>TIMEOUT</td><td>"  + timeout + "</td></tr>" +
						"</table>" +
						"<br/>" +
						"Please do not reply to this mail as this is an auto generated message and you will never receive any response!";
		
		
		List<User> recipients = new ArrayList<User>();
		recipients.add(userService.findUser("dirkvranckaert@gmail.com"));
		EmailUtil.sendEmail("WorkTime Reporting - Users/New Users", body, "text/html", recipients);
	}
}

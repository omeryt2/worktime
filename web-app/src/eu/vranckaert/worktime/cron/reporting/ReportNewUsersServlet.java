package eu.vranckaert.worktime.cron.reporting;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.service.ReportingService;

public class ReportNewUsersServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ReportNewUsersServlet.class.getName());
	
	@Inject private UserService userService;
	@Inject private ReportingService reportingService;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<User> allUsers = userService.findAll();
		
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
		List<User> usersRegisteredYesterday = reportingService.findUsersRegisteredOnDay(yesterday.getTime());
		
		String body = "The total number of users registered in the system yesterday is " + allUsers.size() + ".</br>" +
				"Yesterday " + usersRegisteredYesterday.size() + " user(s) have registered.";
		
		try {
			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(body, "text/html");
			mp.addBodyPart(htmlPart);
			
			Properties props = new Properties();
			javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setContent(mp);
			msg.setFrom(new InternetAddress("no-reply@worktime-web.appspotmail.com", "WorkTime"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress("dirkvranckaert@gmail.com", "Vranckaert Dirk"));
            msg.setSubject("WorkTime Reporting - Users/New Users");
            Transport.send(msg);
		} catch (AddressException e) {
			log.warning("Could not sent the new users report because of an address exception...");
		} catch (MessagingException e) {
			log.warning("Could not sent the new users report because of a messaging exception...");
		} catch (UnsupportedEncodingException e) {
			log.warning("Could not sent the new users report because of an unsupported encoding exception...");
		}
	}
}

package eu.vranckaert.worktime.util;

import java.io.UnsupportedEncodingException;
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

import eu.vranckaert.worktime.cron.reporting.ReportNewUsersServlet;
import eu.vranckaert.worktime.model.User;

public class EmailUtil {
	private static final Logger log = Logger.getLogger(ReportNewUsersServlet.class.getName());
	
	public static final void sendEmail(String subject, Object body, String bodyType, List<User> recipients) {		
		sendEmail(subject, body, bodyType, User.getTechnicalUser(), recipients);
	}
	
	public static final void sendEmail(String subject, Object body, String bodyType, User from, List<User> recipients) {
		try {
			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(body, bodyType);
			mp.addBodyPart(htmlPart);
			
			Properties props = new Properties();
			javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setContent(mp);
			msg.setFrom(new InternetAddress(from.getEmail(), from.getFirstName() + " " + from.getLastName()));
			for (User recipient : recipients) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail(), recipient.getFirstName() + " " + recipient.getLastName()));
			}
            msg.setSubject(subject);
            Transport.send(msg);
		} catch (AddressException e) {
			log.warning("Could not sent email because of an address exception...");
		} catch (MessagingException e) {
			log.warning("Could not sent email because of a messaging exception...");
		} catch (UnsupportedEncodingException e) {
			log.warning("Could not sent email because of an unsupported encoding exception...");
		}
	}
}

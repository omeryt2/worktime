package eu.vranckaert.worktime.view.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.ui.SelectOption;
import eu.vranckaert.worktime.util.EmailUtil;
import eu.vranckaert.worktime.view.BaseView;

@At(ContactView.PAGE_URL)
@Show("/WEB-INF/pages/contact.jsp")
//@Decorated
public class ContactView extends BaseView {
	public static final String PAGE_URL = "/contact";
	
	private String firstName;
	private String lastName;
	private String email;
	private String reason;
	private String message;
	
	private List<SelectOption> contactReasons;

	@Override
	protected void fetchUrlParameters(String... urlParameters) {}
	
	@Get
	public String get(Request request) {
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		buildContactReasons();
		
		return null;
	}
	
	@Post
	public String post(Request request) {
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		buildContactReasons();
		
		if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
			if (StringUtils.isBlank(firstName)) {
				firstName = "";
			}
			
			if (StringUtils.isBlank(lastName)) {
				lastName = "";
			}
			setErrorMessage(getMessage("contact.error.nameRequired"));
		}
		
		if (StringUtils.isBlank(email)) {
			setErrorMessage(getMessage("contact.error.emailRequired"));
		} else if (!EmailValidator.getInstance().isValid(email)) {
			setErrorMessage(getMessage("contact.error.emailInvalid"));
		}
		
		if (StringUtils.isBlank(message)) {
			setErrorMessage(getMessage("contact.error.messageRequired"));
		}
		
		if (hasErrorMessages()) {
			return null;
		}
		
		String body = "This is an automated messages sent using the WorkTime form located at http://worktime-web.appspot.com/contact.<br/>" +
				"In order to reply to this message just hit the the reply button and you will be in direct contact with the person who sent the form!<br/>" +
				"<br/>" +
				"The message sent is:<br/>" +
				"<br/>" + 
				message;
		
		User from = new User();
		from.setFirstName(firstName);
		from.setLastName(lastName);
		from.setEmail(email);
		
		User to = new User();
		to.setFirstName("Work Time");
		to.setLastName("");
		to.setEmail("info@vranckaert.eu");
		EmailUtil.sendEmail("WorkTime Contact Form (" + reason + ")", body, "text/html", from, Arrays.asList(new User[] {User.getTechnicalUser()}));
		
		return addMessageToSelf(MessageType.INFO, getMessage("contact.emailSent"));
	}
	
	private void buildContactReasons() {
		contactReasons = new ArrayList<SelectOption>();
		contactReasons.add(new SelectOption(Reason.QUESTION.toString(), getMessage("contact.type.reason"), (reason != null && Reason.valueOf(reason).equals(Reason.QUESTION)) ? true : false));
		contactReasons.add(new SelectOption(Reason.REMARK.toString(), getMessage("contact.type.remark"),  (reason != null && Reason.valueOf(reason).equals(Reason.REMARK)) ? true : false));
		contactReasons.add(new SelectOption(Reason.BUG_REPORT.toString(), getMessage("contact.type.bugReport"),  (reason != null && Reason.valueOf(reason).equals(Reason.BUG_REPORT)) ? true : false));
	}

	@Override
	public String getPageUrl() {
		return PAGE_URL;
	}

	@Override
	public String getFullPageUrl() {
		return PAGE_URL;
	}

	@Override
	public String getPageTitle() {
		return getMessage("contact.title");
	}
	
	/*
	 * GETTERS AND SETTERS
	 */

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<SelectOption> getContactReasons() {
		return contactReasons;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public enum Reason {
		QUESTION,
		REMARK,
		BUG_REPORT;
	}
}

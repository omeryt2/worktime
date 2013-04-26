package eu.vranckaert.worktime.view.user;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.model.Session.Platform;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.utils.Password;
import eu.vranckaert.worktime.view.BaseView;
import eu.vranckaert.worktime.view.home.HomeView;

@At(RegisterView.PAGE_URL)
@Show("/WEB-INF/pages/register.jsp")
//@Decorated
public class RegisterView extends BaseView {
	public static final String PAGE_URL = SECURED_PAGES_PREFIX + "register";

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String passwordConfirmation;
	private Boolean agree;
	
	@Inject
	private UserService userService;
	
	@Override
	protected void fetchUrlParameters(String... urlParameters) {}

	@Override
	@Get
	public String get(Request request) {
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		return null;
	}

	@Override
	@Post
	public String post(Request request) {
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		if (StringUtils.isBlank(firstName)) {
			setValidationMessage(getMessage("register.error.fristNameRequired"));
		}
		if (StringUtils.isBlank(lastName)) {
			setValidationMessage(getMessage("register.error.lastNameRequired"));
		}
		if (StringUtils.isBlank(email)) {
			setValidationMessage(getMessage("register.error.emailRequired"));
		}
		if (StringUtils.isBlank(password)) {
			setValidationMessage(getMessage("register.error.passwordRequired"));
		} else if (!Password.validatePasswordCheck(password)) {
			setValidationMessage(getMessage("register.error.passwordLength"));
		} else if (!password.equals(passwordConfirmation)) {
			setValidationMessage(getMessage("register.error.passwordConfirmation"));
		} 
		if (agree == null || !agree) {
			setValidationMessage(getMessage("register.error.acceptPolicies"));
		}
		
		if (hasValidationMessages()) {
			return null;
		}
		
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		
		String sessionKey = null;
		try {
			sessionKey = userService.register(user, password, Platform.WEB);
		} catch (EmailAlreadyInUseException e) {
			setErrorMessage(getMessage("register.error.emailAlreadyInUse", email));
		} catch (PasswordLenghtInvalidException e) {
			setErrorMessage(getMessage("register.error.passwordLength"));
		}
		
		if (hasErrorMessages()) {
			return null;
		}
		
		if (StringUtils.isNotBlank(sessionKey)) {
			super.storeLoggedInUser(email, sessionKey);
		}
		
		if (StringUtils.isBlank(getRefererUrl())) {
			return HomeView.PAGE_URL;
		} else {
			return getRefererUrl();
		}
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
		return getMessage("register.title");
	}

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public Boolean getAgree() {
		return agree;
	}

	public void setAgree(Boolean agree) {
		this.agree = agree;
	}

}

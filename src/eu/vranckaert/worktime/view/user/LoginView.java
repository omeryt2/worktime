package eu.vranckaert.worktime.view.user;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.model.Session.Platform;
import eu.vranckaert.worktime.security.exception.PasswordIncorrectException;
import eu.vranckaert.worktime.security.exception.UserNotFoundException;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.view.BaseView;
import eu.vranckaert.worktime.view.home.HomeView;

@At(LoginView.PAGE_URL)
@Show("/WEB-INF/pages/user/login.jsp")
public class LoginView extends BaseView {
	public static final String PAGE_URL = SECURED_PAGES_PREFIX + "login";
	
	@Inject
	private UserService userService;
	
	private String email;
	private String password;
	
	@Post
	public String post(Request request) {
		String superResult = super.post(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		if (StringUtils.isBlank(email)) {
			setValidationMessage(getMessage("login.error.emailRequired"));
		} else if (!EmailValidator.getInstance().isValid(email)) {
			setValidationMessage(getMessage("login.error.emailInvalid"));
		}
		
		if (StringUtils.isBlank(password)) {
			setValidationMessage(getMessage("login.error.passwordRequired"));
		}
		
		if (hasValidationMessages()) {
			return null;
		}
		
		String sessionKey = null;
		try {
			sessionKey = userService.login(email, password, Platform.WEB);
		} catch (UserNotFoundException e) {
			setErrorMessage(getMessage("login.error.incorrectCredentials"));
			return null;
		} catch (PasswordIncorrectException e) {
			setErrorMessage(getMessage("login.error.incorrectCredentials"));
			return null;
		}
		
		super.storeLoggedInUser(email, sessionKey);
		
		if (StringUtils.isBlank(getRefererUrl()) || getRefererUrl().contains(RegisterView.PAGE_URL)) {
			return HomeView.PAGE_URL;
		} else {
			return getRefererUrl();
		}
	}

	@Override
	protected void fetchUrlParameters(String... urlParameters) {}

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
		return getMessage("login.title");
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

}

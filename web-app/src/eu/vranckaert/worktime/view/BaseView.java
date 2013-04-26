package eu.vranckaert.worktime.view;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.sitebricks.binding.FlashCache;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.util.RequestUtil;
import eu.vranckaert.worktime.view.home.HomeView;
import eu.vranckaert.worktime.view.home.ResetPasswordRequestView;
import eu.vranckaert.worktime.view.home.ResetPasswordView;
import eu.vranckaert.worktime.view.user.LoginView;
import eu.vranckaert.worktime.view.user.RegisterView;

//@Show("WEB-INF/pages/template.jsp")
public abstract class BaseView {
	public static final String PAGE_URL = "";
	protected static final String SECURED_PAGES_PREFIX = "/user/";
	
	private static final String PREVIOUS_PAGE = "previousPage";
	protected static final String SESSION_KEY = "sessionKey";
	protected static final String EMAIL = "email";
	
	private String[] PAGES_FORBIDDEN_IF_LOGGED_IN = new String[] {LoginView.PAGE_URL, RegisterView.PAGE_URL, ResetPasswordRequestView.PAGE_URL, ResetPasswordView.PAGE_URL};
	private String[] UNSECURED_USER_PAGES = new String[] {LoginView.PAGE_URL, RegisterView.PAGE_URL};
	private String[] PREVIOUS_PAGES_EXLUSIONS = new String[] {LoginView.PAGE_URL};
	
	@Inject
	private UserService userService;
	
	@Inject
	private FlashCache flashCache;
	
	@Inject
	private HttpServletRequest request;

	private String sessionKey;
	private String previousPage;
	private boolean loggedIn = false;
	private String refererUrl;
	private User user;
	
	private boolean showUserHeader = true;
	private List<String> infoMessages;
	private List<String> validationMessages;
	private List<String> warningMessages;
	private List<String> errorMessages;
	
	/**
	 * Execute a GET request. Make sure if you implement the get yourself that
	 * before calling the super.get(..) method you have called and implemented
	 * the {@link BaseView#fetchUrlParameters(String...)} method.
	 * @param request The request.
	 * @return Returns a {@link String} being the page that will be redirected 
	 * to if necessary, null if everything is fine and the page can be loaded.
	 */
	@Get
	public String get(Request request) {
		previousPage = flashCache.get(PREVIOUS_PAGE);
		flashCache.remove(PREVIOUS_PAGE);
		if (Arrays.asList(PREVIOUS_PAGES_EXLUSIONS).contains(getFullPageUrl())) {
			if (StringUtils.isNotBlank(previousPage)) {
				flashCache.put(PREVIOUS_PAGE, previousPage);
			} else {
				flashCache.put(PREVIOUS_PAGE, HomeView.PAGE_URL);
			}
		} else {
			flashCache.put(PREVIOUS_PAGE, getFullPageUrl());
		}
				
		String result = validateRequest(request);
		
		loadReferer(request);
		
		if (shouldRedirect(result)) {
			return result;
		}
		
		return null;
	}
	
	/**
	 * Execute a POST request. Make sure if you implement the post yourself that
	 * before calling the super.post(..) method you have called and implemented
	 * the {@link BaseView#fetchUrlParameters(String...)} method.
	 * @param request The request.
	 * @return Returns a {@link String} being the page that will be redirected 
	 * to if necessary, null if everything is fine and the page can be loaded.
	 */
	@Post
	public String post(Request request) {
		String result = validateRequest(request);
		
		loadReferer(request);
		
		if (shouldRedirect(result)) {
			return result;
		}
		
		return null;
	}
	
	/**
	 * Fetch the parameters of the request/URL.
	 * @param urlParameters The parameters.
	 */
	protected abstract void fetchUrlParameters(String... urlParameters);
	
	/**
	 * Checks if a user is logged in based on the {@link BaseView#SESSION_KEY} 
	 * and {@link BaseView#EMAIL} session parameters. Also check that we do not 
	 * try to access a forbidden page such as register and login (while already 
	 * logged in) and if everything is find log the usage of the session key.
	 * @param request The {@link Request} that is coming in for this page.
	 * @return Returns a {@link String} being the page that will be redirected 
	 * to if necessary, null if everything is fine and the page can be loaded.
	 */
	private String validateRequest(Request request) {		
		// Check if session key and email are found
		String sessionKey = flashCache.get(SESSION_KEY);
		String email = flashCache.get(EMAIL);
		
		// If data is incorrect, removed and logout
		if (StringUtils.isBlank(sessionKey) || StringUtils.isBlank(email)) {
			resetSessionKeyAndEmail();
			loggedIn = false;
			
			// If on a secured page return to the login
			if (getPageUrl().startsWith(SECURED_PAGES_PREFIX) && !Arrays.asList(UNSECURED_USER_PAGES).contains(getPageUrl())) {
				return LoginView.PAGE_URL;
			}
		} else {
			// User seems to be logged in, check if it's true
			loggedIn = userService.isLoggedIn(email, sessionKey);
			if (!loggedIn) {
				userService.logout(email, sessionKey);
				resetSessionKeyAndEmail();
				
				// If on a secured page return to the login
				if (getPageUrl().startsWith(SECURED_PAGES_PREFIX)) {
					return LoginView.PAGE_URL;
				}
			} else {
				// If logged in, load the user data
				user = userService.findUser(email);
				this.sessionKey = sessionKey;
			}
		}
		
		// When logged in forbid access to pages such as login and register
		if (getLoggedIn() && Arrays.asList(PAGES_FORBIDDEN_IF_LOGGED_IN).contains(getPageUrl())) {
			String refererUrl = RequestUtil.getReferer(request);
			boolean shouldBeExcluded = false;
			for (String page : PREVIOUS_PAGES_EXLUSIONS) {
				if (refererUrl.contains(page)) {
					shouldBeExcluded = true;
					break;
				}
			}
			if (StringUtils.isBlank(refererUrl) || shouldBeExcluded) {
				refererUrl = HomeView.PAGE_URL;
			}
			return refererUrl;
		}
		
		// Mark the session to be used if we get here. Meaning that we do not 
		// access forbidden pages (such as login or register) and we are logged 
		// in
		if (getLoggedIn()) {
			userService.markSessionUsed(email, sessionKey);
		}
		
		return null;
	}
	
	private void loadReferer(Request request) {
		if (StringUtils.isNotBlank(refererUrl)) {
			return;
		}
		
		refererUrl = RequestUtil.getReferer(request);
		if (StringUtils.isBlank(refererUrl)) {
			if (getPreviousPage() != null) {
				refererUrl = getPreviousPage();
			} else {
				refererUrl = HomeView.PAGE_URL;
			}
		} else if(refererUrl.contains(getPageUrl())) {
			if (getPreviousPage() != null && !getPreviousPage().contains(getPageUrl())) {
				refererUrl = getPreviousPage();
			} else {
				refererUrl = HomeView.PAGE_URL;
			}
		}
	}
	
	/**
	 * Removes the {@link BaseView#SESSION_KEY} and {@link BaseView#EMAIL} from
	 * the session parameters.
	 */
	private void resetSessionKeyAndEmail() {
		flashCache.remove(SESSION_KEY);
		flashCache.remove(EMAIL);
	}
	
	/**
	 * After a login this will store the logged in user's email and session key
	 * on the http session.
	 * @param email The email of the logged in user.
	 * @param sessionKey The session key for the logged in user.
	 */
	protected void storeLoggedInUser(String email, String sessionKey) {
		flashCache.put(SESSION_KEY, sessionKey);
		flashCache.put(EMAIL, email);
	}
	
	/**
	 * Get the URL of the current page.
	 * @return The URL of the current page.
	 */
	public abstract String getPageUrl();
	
	/**
	 * Get the URL of the current page with the page parameters replaced by
	 * actual values.
	 * @return The full URL of the current page with the correct parameter 
	 * values.
	 */
	public abstract String getFullPageUrl();
	
	/**
	 * Checks if the current page ({@link BaseView#getPageUrl()} is the home 
	 * page ({@link HomeView#PAGE_URL}).
	 * @return {@link Boolean#TRUE} if the current page is the home page, 
	 * {@link Boolean#FALSE} if not.
	 */
	public boolean getIsHomePage() {
		if (getPageUrl().equals(HomeView.PAGE_URL)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get the title for the current page.
	 * @return The title for the current page.
	 */
	public abstract String getPageTitle();

	/**
	 * The full title for the current page being the 
	 * {@link BaseView#getPageTitle()} preceded by the general title
	 * {@link UIMessages#title()} which is the application name.
	 * @return The full title for the current page.
	 */
	public String getTitle() {
		String title = getMessage("title");
		
		if (StringUtils.isNotBlank(getPageTitle())) {
			return title + " -" + getPageTitle();
		}
		
		return title;
	}
	
	/**
	 * Get the current year that will be used as copyright year to be displayed
	 * on each page.
	 * @return The current year.
	 */
	public int getCopyrightYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year;
	}
	
	/**
	 * An enum representing all possible UI messages to be displayed.
	 * @author Dirk Vranckaert
	 */
	protected enum MessageType {
		INFO, WARNING, ERROR;
	}

	/**
	 * Add a message parameter to itself and returns the page URL to be 
	 * redirected to.
	 * @param messageType The {@link MessageType}.
	 * @param message The message to be displayed.
	 * @return The full page URL to be redirected to.
	 */
	protected String addMessageToSelf(MessageType messageType,
			String message) {
		String page = getFullPageUrl();
		return addMessageToPage(page, messageType, message);
	}
	
	/**
	 * Add a message to certain page URL and returns the full page URL to be 
	 * redirected to.
	 * @param page The page to add the message to.
	 * @param messageType The {@link MessageType}.
	 * @param message The message to be displayed.
	 * @return The full page URL to be redirected to.
	 */
	protected String addMessageToPage(String page, MessageType messageType, String message) {
		String parameter = "";
		switch (messageType) {
		case INFO:
			parameter = "infoMessage";
			break;
		case WARNING:
			parameter = "warningMessage";
			break;
		case ERROR:
			parameter = "errorMessage";
		}
		
		return addParameterToUrl(page, parameter, message);
	}
	
	/**
	 * Add a parameter in a certain URL and returns the full page URL to be
	 * redirected to.
	 * @param url The URL of the page.
	 * @param parameter The name of the parameter.
	 * @param value The value of the parameter.
	 * @return The full page URL to be redirected to.
	 */
	protected String addParameterToUrl(String url, String parameter, String value) {
		if (StringUtils.isBlank(parameter) || value == null) {
			return url;
		}
		
		if (url.contains("?")) {
			url += "&";
		} else {
			url += "?";
		}
		
		url += parameter + "=" + value;
		
		return url;
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isShowUserHeader() {
		return showUserHeader;
	}

	public void setShowUserHeader(boolean showUserHeader) {
		this.showUserHeader = showUserHeader;
	}

//	public UIMessages getMessages() {
//		return null;
//	}

	protected String getPreviousPage() {
		return previousPage;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}
	
	protected boolean shouldRedirect(String page) {
		if (StringUtils.isNotBlank(page) && !page.contains(getFullPageUrl())) {
			return true;
		}
		return false;
	}
	
	public void setInfoMessage(String infoMessage) {
		if (infoMessages == null) {
			infoMessages = new ArrayList<String>();
		}
		infoMessages.add(infoMessage);
	}
	
	public void setValidationMessage(String validationMessage) {
		if (validationMessages == null) {
			validationMessages = new ArrayList<String>();
		}
		validationMessages.add(validationMessage);
	}
	
	public void setWarningMessage(String warningMessage) {
		if (warningMessages == null) {
			warningMessages = new ArrayList<String>();
		}
		warningMessages.add(warningMessage);
	}
	
	public void setErrorMessage(String errorMessage) {
		if (errorMessages == null) {
			errorMessages = new ArrayList<String>();
		}
		errorMessages.add(errorMessage);
	}

	public List<String> getInfoMessages() {
		return infoMessages;
	}

	public void setInfoMessages(List<String> infoMessages) {
		this.infoMessages = infoMessages;
	}

	public List<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(List<String> validationMessages) {
		this.validationMessages = validationMessages;
	}

	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public void setWarningMessages(List<String> warningMessages) {
		this.warningMessages = warningMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	protected void clearMessages() {
		clearInfoMessages();
		clearValidationMessages();
		clearWarningMessages();
		clearErrorMessages();
	}
	
	protected void clearInfoMessages() {
		infoMessages = new ArrayList<String>();
	}
	
	protected void clearValidationMessages() {
		validationMessages = new ArrayList<String>();
	}
	
	protected void clearWarningMessages() {
		warningMessages = new ArrayList<String>();
	}
	
	protected void clearErrorMessages() {
		errorMessages = new ArrayList<String>();
	}
	
	protected boolean hasInfoMessages() {
		if (infoMessages != null && infoMessages.size() > 0) {
			return true;
		}
		return false;
	}
	
	protected boolean hasValidationMessages() {
		if (validationMessages != null && validationMessages.size() > 0) {
			return true;
		}
		return false;
	}
	
	protected boolean hasWarningMessages() {
		if (warningMessages != null && warningMessages.size() > 0) {
			return true;
		}
		return false;
	}
	
	protected boolean hasErrorMessages() {
		if (errorMessages != null && errorMessages.size() > 0) {
			return true;
		}
		return false;
	}
	
	public String formatDate(Date date) {
		Locale locale = request.getLocale();
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return format.format(date);
	}
	
	public String formatTime(Date time) {
		Locale locale = request.getLocale();
		DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		return format.format(time);
	}
	
	public String getLoggedInSessionKey() {
		if (StringUtils.isNotBlank(sessionKey)) {
			return sessionKey;
		} else {
			return "";
		}
	}
	
	public String getLoggedInUser() {
		if (user != null && StringUtils.isNotBlank(user.getEmail())) {
			return user.getEmail();
		} else {
			return "";
		}
	}
	
	protected String getMessage(String key, String... params) {
		Locale locale = request.getLocale();
		
		String message = getMessageForLocale(key, locale);
		if (StringUtils.isNotBlank(message)) {
			for (int i=0; i<params.length; i++) {
				message = message.replaceAll("\\{" + i + "\\}", params[i]);
			}
			
			return message;
		}
		
		return "???" + key + "???";
	}
	
	private String getMessageForLocale(String key, Locale locale) {
		Properties properties = getPropertiesForLocale(locale, true, true);
		String message = getMessageFromProperties(key, properties);
		
		if (StringUtils.isBlank(message)) {
			properties = getPropertiesForLocale(locale, true, false);
			message = getMessageFromProperties(key, properties);
		}
		
		if (StringUtils.isBlank(message)) {
			properties = getPropertiesForLocale(locale, false, false);
			message = getMessageFromProperties(key, properties);
		}
		
		return message;
	}
	
	private String getMessageFromProperties(String key, Properties properties) {
		if (properties != null) {
			String message = (String) properties.get(key);
			return message;
		}
		
		return null;
	}
	
	private Properties getPropertiesForLocale(Locale locale, boolean includeLanguage, boolean includeCountry) {
		String basePropName = "/eu/vranckaert/worktime/ui/i18n/uimessages";
		String extension = ".properties";
		
		String propNameLanguage = basePropName + "_" + locale.getLanguage();
		String propNameLanguageCountry = propNameLanguage + "_" + locale.getCountry().toUpperCase();
		
		if (includeLanguage && includeCountry) {
			return loadProperties(propNameLanguageCountry + extension);
		} else if (includeLanguage && !includeCountry) {
			return loadProperties(propNameLanguage + extension);
		} else {
			return loadProperties(basePropName + extension);
		}
	}
	
	private Properties loadProperties(String classpath) {
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream(classpath);
		if (inputStream == null) {
			return null;
		}
		try {
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			return null;
		}
		return properties;
	}
}

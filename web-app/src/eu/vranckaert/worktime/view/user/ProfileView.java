package eu.vranckaert.worktime.view.user;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.view.BaseView;

@At(ProfileView.PAGE_URL)
@Show("/WEB-INF/pages/user/profile.jsp")
//@Decorated
public class ProfileView extends BaseView {
	public static final String PAGE_URL = SECURED_PAGES_PREFIX + "profile";
	
	private String firstName;
	private String lastName;
	private String profileImageUrl;
	
	@Inject
	private UserService userService;

	@Override
	protected void fetchUrlParameters(String... urlParameters) {}
	
	@Get
	public String get(Request request) {
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		firstName = getUser().getFirstName();
		lastName = getUser().getLastName();
		profileImageUrl = getUser().getProfileImageUrl();
		
		return null;
	}
		
	@Post
	public String post(Request request) {
		String superResult = super.post(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		User user = userService.findUser(getUser().getEmail());
		
		if (StringUtils.isBlank(firstName)) {
			setValidationMessage(getMessage("profile.error.firstNameRequired"));
		}
		if (StringUtils.isBlank(lastName)) {
			setValidationMessage(getMessage("profile.error.lastNameRequired"));
		}
		
		if (StringUtils.isNotBlank(profileImageUrl) && !UrlValidator.getInstance().isValid(profileImageUrl)) {
			setValidationMessage(getMessage("profile.error.urlAvatarInvalid"));
		}
		
		if (hasValidationMessages()) {
			return null;
		}
		
		getUser().setFirstName(firstName);
		getUser().setLastName(lastName);
		getUser().setProfileImageUrl(profileImageUrl);
		userService.update(getUser());
		
		super.addMessageToSelf(MessageType.INFO, "Profile update ok!");
		return ProfileView.PAGE_URL;
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
		return getMessage("profile.title");
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

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
}

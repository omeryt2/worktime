package eu.vranckaert.worktime.view.home;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.view.BaseView;

@At(ResetPasswordRequestView.PAGE_URL)
@Show("/WEB-INF/pages/resetPasswordRequest.jsp")
//@Decorated
public class ResetPasswordRequestView extends BaseView {
	public static final String PAGE_URL = "/resetPasswordRequest";
	
	private String email;
	
	@Inject
	private UserService userService;

	@Post
	public String post(Request request) {
		String superResult = super.post(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		if (StringUtils.isBlank(email)) {
			setValidationMessage(getMessage("resetPasswordRequet.error.emailRequired"));
			return null;
		}
		
		userService.resetPasswordRequest(email);
		return addMessageToPage(HomeView.PAGE_URL, MessageType.INFO, getMessage("resetPasswordRequet.msg.success", email, PAGE_URL));
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
		return getMessage("resetPasswordRequet.title");
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

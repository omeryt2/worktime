package eu.vranckaert.worktime.view.home;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.view.BaseView;

@At(ResetPasswordRequestView.PAGE_URL)
@Show("WEB-INF/pages/resetPasswordRequest.html")
public class ResetPasswordRequestView extends BaseView {
	public static final String PAGE_URL = "/resetPasswordRequest";
	
	private String email;
	
	@Inject
	private UserService userService;

	@Post
	public String post() {
		userService.resetPasswordRequest(email);
		return HomeView.PAGE_URL + "?infoMessage=" + getMessages().resetPasswordRequestCheckEmail();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

package eu.vranckaert.worktime.view.home;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import eu.vranckaert.worktime.security.exception.InvalidPasswordResetKeyException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyAlreadyUsedException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyExpiredException;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.utils.Password;
import eu.vranckaert.worktime.view.BaseView;

@At(ResetPasswordView.PAGE_URL)
@Show("WEB-INF/pages/resetPassword.html")
public class ResetPasswordView extends BaseView {
	public static final String PAGE_URL = "/resetPassword/:key";
	
	private String resetKey;
	private String newPassword;
	private String repeatPassword;
	
	private boolean allowPasswordReset = true;
	
	@Inject
	private UserService userService;
	
	@Get
	public void get(@Named("key") String key) {
		this.resetKey = key;
		
		try {
			userService.getPasswordResetRequestKey(resetKey);
		} catch (InvalidPasswordResetKeyException e) {
			setErrorMessage(getMessages().resetPasswordFormErrorInvalidPasswordResetKey());
			allowPasswordReset = false;
		} catch (PasswordResetKeyAlreadyUsedException e) {
			setErrorMessage(getMessages().resetPasswordFormErrorPasswordResetKeyAlreadyUsed());
			allowPasswordReset = false;
		} catch (PasswordResetKeyExpiredException e) {
			setErrorMessage(getMessages().resetPasswordFormErrorPasswordResetKeyExpired());
			allowPasswordReset = false;
		}
	}
	
	@Post
	public String post(@Named("key") String key) {
		this.resetKey = key;
		
		if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(repeatPassword)) {
			setErrorMessage(getMessages().resetPasswordFormErrorAllRequired());
		}
		
		if (StringUtils.isBlank(getErrorMessage()) && !newPassword.equals(repeatPassword)) {
			setErrorMessage(getMessages().resetPasswordFormErrorPasswordRepeatDoesNotMatch());
		}
		
		if (StringUtils.isBlank(getErrorMessage()) && !Password.validatePasswordCheck(newPassword)) {
			setErrorMessage(getMessages().resetPasswordFormErrorPasswordRequirements());
		}
		
		if (StringUtils.isBlank(getErrorMessage())) {
			try {
				userService.resetPassword(resetKey, newPassword);
			} catch (PasswordLenghtInvalidException e) {
				setErrorMessage(getMessages().resetPasswordFormErrorPasswordRequirements());
			} catch (InvalidPasswordResetKeyException e) {
				setErrorMessage(getMessages().resetPasswordFormErrorInvalidPasswordResetKey());
			} catch (PasswordResetKeyAlreadyUsedException e) {
				setErrorMessage(getMessages().resetPasswordFormErrorPasswordResetKeyAlreadyUsed());
			} catch (PasswordResetKeyExpiredException e) {
				setErrorMessage(getMessages().resetPasswordFormErrorPasswordResetKeyExpired());
			}
		}
		
		if (StringUtils.isNotBlank(getErrorMessage())) {
			return ResetPasswordView.PAGE_URL.replace(":key", resetKey) + "?errorMessage=" + getErrorMessage();
		} else {
			return HomeView.PAGE_URL;
		}
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	public boolean isAllowPasswordReset() {
		return allowPasswordReset;
	}

	public void setAllowPasswordReset(boolean allowPasswordReset) {
		this.allowPasswordReset = allowPasswordReset;
	}
}

package eu.vranckaert.worktime.view.home;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
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
@Show("/WEB-INF/pages/resetPassword.jsp")
//@Decorated
public class ResetPasswordView extends BaseView {
	public static final String PAGE_URL = "/resetPassword/:key";
	
	private String resetKey;
	private String newPassword;
	private String repeatPassword;
	
	private boolean allowPasswordReset = true;
	
	@Inject
	private UserService userService;
	
	@Get
	public String get(@Named("key") String key, Request request) {
		fetchUrlParameters(key);
		String superResult = super.get(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		try {
			userService.getPasswordResetRequestKey(resetKey);
		} catch (InvalidPasswordResetKeyException e) {
			setErrorMessage(getMessage("resetPassword.error.urlInvalid"));
			allowPasswordReset = false;
		} catch (PasswordResetKeyAlreadyUsedException e) {
			setErrorMessage(getMessage("resetPassword.error.urlAlreadyUsed"));
			allowPasswordReset = false;
		} catch (PasswordResetKeyExpiredException e) {
			setErrorMessage(getMessage("resetPassword.error.urlExpired"));
			allowPasswordReset = false;
		}
		
		return null;
	}
	
	@Post
	public String post(Request request, @Named("key") String key) {
		fetchUrlParameters(key);
		String superResult = super.post(request);
		if (shouldRedirect(superResult)) {
			return superResult;
		}
		
		if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(repeatPassword)) {
			setValidationMessage(getMessage("resetPassword.error.allFieldsRequired"));
		}
		
		if (!hasValidationMessages()) {
			if (!newPassword.equals(repeatPassword)) {
				setValidationMessage(getMessage("resetPassword.error.passwordConfirmation"));
			}
			
			if (!Password.validatePasswordCheck(newPassword)) {
				setValidationMessage(getMessage("resetPassword.error.passwordLength"));
			}
		}
		
		if (!hasValidationMessages()) {
			try {
				userService.resetPassword(resetKey, newPassword);
			} catch (PasswordLenghtInvalidException e) {
				setErrorMessage(getMessage("resetPassword.error.passwordLength"));
			} catch (InvalidPasswordResetKeyException e) {
				setErrorMessage(getMessage("resetPassword.error.urlInvalid"));
			} catch (PasswordResetKeyAlreadyUsedException e) {
				setErrorMessage(getMessage("resetPassword.error.urlAlreadyUsed"));
			} catch (PasswordResetKeyExpiredException e) {
				setErrorMessage(getMessage("resetPassword.error.urlExpired"));
			}
		}
		
		if (hasValidationMessages()) {
			return null;
		}
		
		return addMessageToPage(HomeView.PAGE_URL, MessageType.INFO, getMessage("resetPassword.msg.success"));
	}

	@Override
	protected void fetchUrlParameters(String... urlParameters) {
		this.resetKey = urlParameters[0];
	}

	@Override
	public String getPageUrl() {
		return PAGE_URL;
	}

	@Override
	public String getFullPageUrl() {
		return PAGE_URL.replace(":key", resetKey);
	}

	@Override
	public String getPageTitle() {
		return getMessage("resetPassword.title");
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

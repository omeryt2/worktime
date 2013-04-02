package eu.vranckaert.worktime.view.i18n;

import com.google.sitebricks.i18n.Message;

public interface UIMessages {
	@Message(message = "Work Time for Android")
	String homeTitle();
	
	@Message(message = "Work Time for Android - Reset Password")
	String resetPasswordTitle();

	@Message(message = "Work Time for Android - Request Password Reset")
	String passwordResetRequestTitle();
	
	@Message(message = "This website is currently under construction.")
	String homeUnderConstruction();
	
	@Message(message = "Get the Android application from the Google Play Store now!")
	String homeDownloadFromPlayStore();
	
	@Message(message  = "An email has been sent to you with the URL to reset your password. It can take about 5 to 10 minutes to receive te email. If you do not receive the email within a few hours please do a new request.")
	String resetPasswordRequestCheckEmail();
	
	@Message(message  = "Request Reset")
	String resetPasswordRequestSubmitButton();
	
	@Message(message  = "Reset Password")
	String resetPasswordFormSubmitButton();
	
	@Message(message  = "All fields are required")
	String resetPasswordFormErrorAllRequired();

	@Message(message  = "The repeated password does not match")
	String resetPasswordFormErrorPasswordRepeatDoesNotMatch();
	
	@Message(message = "The password should be at least 6 characters long and maximum 30")
	String resetPasswordFormErrorPasswordRequirements();
	
	@Message(message = "The url is not valid. Please request a new reset!")
	String resetPasswordFormErrorInvalidPasswordResetKey();
	
	@Message(message = "The url has already been used. Try to login or if you keep having troubles with login in please request a new reset")
	String resetPasswordFormErrorPasswordResetKeyAlreadyUsed();
	
	@Message(message = "The url has expired. It is only valid for 24 hours. Please request a new reset if you keep having troubles with login.")
	String resetPasswordFormErrorPasswordResetKeyExpired();
}

package eu.vranckaert.worktime.dao.web.model.request.user;

import eu.vranckaert.worktime.dao.web.model.base.request.RegisteredServiceRequest;

/**
 * Date: 2/04/13
 * Time: 13:00
 *
 * @author Dirk Vranckaert
 */
public class ResetPasswordRequest extends RegisteredServiceRequest {
    private String passwordResetKey;
    private String newPassword;

    public String getPasswordResetKey() {
        return passwordResetKey;
    }

    public void setPasswordResetKey(String passwordResetKey) {
        this.passwordResetKey = passwordResetKey;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

package eu.vranckaert.worktime.dao.web.model.base.request;

import com.google.gson.annotations.Expose;

/**
 * Date: 27/03/13
 * Time: 15:19
 *
 * @author Dirk Vranckaert
 */
public class UserChangePasswordRequest extends AuthenticatedUserRequest {
    @Expose
    private String oldPassword;
    @Expose
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

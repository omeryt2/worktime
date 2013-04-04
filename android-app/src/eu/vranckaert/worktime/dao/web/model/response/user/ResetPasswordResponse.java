package eu.vranckaert.worktime.dao.web.model.response.user;

import eu.vranckaert.worktime.dao.web.model.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.dao.web.model.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.InvalidPasswordResetKeyJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.PasswordLengthInvalidJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.PasswordResetKeyAlreadyUsedJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.PasswordResetKeyExpiredJSONException;

/**
 * Date: 2/04/13
 * Time: 13:03
 *
 * @author Dirk Vranckaert
 */
public class ResetPasswordResponse extends WorkTimeResponse {
    private FieldRequiredJSONException fieldRequiredJSONException;
    private PasswordLengthInvalidJSONException passwordLengthInvalidJSONException;
    private InvalidPasswordResetKeyJSONException invalidPasswordResetKeyJSONException;
    private PasswordResetKeyAlreadyUsedJSONException passwordResetKeyAlreadyUsedJSONException;
    private PasswordResetKeyExpiredJSONException passwordResetKeyExpiredJSONException;

    public FieldRequiredJSONException getFieldRequiredJSONException() {
        return fieldRequiredJSONException;
    }

    public void setFieldRequiredJSONException(FieldRequiredJSONException fieldRequiredJSONException) {
        this.fieldRequiredJSONException = fieldRequiredJSONException;
    }

    public PasswordLengthInvalidJSONException getPasswordLengthInvalidJSONException() {
        return passwordLengthInvalidJSONException;
    }

    public void setPasswordLengthInvalidJSONException(PasswordLengthInvalidJSONException passwordLengthInvalidJSONException) {
        this.passwordLengthInvalidJSONException = passwordLengthInvalidJSONException;
    }

    public InvalidPasswordResetKeyJSONException getInvalidPasswordResetKeyJSONException() {
        return invalidPasswordResetKeyJSONException;
    }

    public void setInvalidPasswordResetKeyJSONException(InvalidPasswordResetKeyJSONException invalidPasswordResetKeyJSONException) {
        this.invalidPasswordResetKeyJSONException = invalidPasswordResetKeyJSONException;
    }

    public PasswordResetKeyAlreadyUsedJSONException getPasswordResetKeyAlreadyUsedJSONException() {
        return passwordResetKeyAlreadyUsedJSONException;
    }

    public void setPasswordResetKeyAlreadyUsedJSONException(PasswordResetKeyAlreadyUsedJSONException passwordResetKeyAlreadyUsedJSONException) {
        this.passwordResetKeyAlreadyUsedJSONException = passwordResetKeyAlreadyUsedJSONException;
    }

    public PasswordResetKeyExpiredJSONException getPasswordResetKeyExpiredJSONException() {
        return passwordResetKeyExpiredJSONException;
    }

    public void setPasswordResetKeyExpiredJSONException(PasswordResetKeyExpiredJSONException passwordResetKeyExpiredJSONException) {
        this.passwordResetKeyExpiredJSONException = passwordResetKeyExpiredJSONException;
    }
}

package eu.vranckaert.worktime.dao.web.model.exception.user;

import eu.vranckaert.worktime.dao.web.model.base.exception.WorkTimeJSONException;

/**
 * Date: 2/04/13
 * Time: 13:04
 *
 * @author Dirk Vranckaert
 */
public class PasswordResetKeyAlreadyUsedJSONException extends WorkTimeJSONException{
    public PasswordResetKeyAlreadyUsedJSONException(String requestUrl) {
        super(requestUrl);
    }
}

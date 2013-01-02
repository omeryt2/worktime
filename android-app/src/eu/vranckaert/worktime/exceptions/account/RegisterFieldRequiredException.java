package eu.vranckaert.worktime.exceptions.account;

/**
 * User: Dirk Vranckaert
 * Date: 2/01/13
 * Time: 12:50
 */
public class RegisterFieldRequiredException extends Exception {
    private String fieldName;

    public RegisterFieldRequiredException(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

package eu.vranckaert.worktime.exceptions;

/**
 * Date: 29/10/13
 * Time: 10:44
 *
 * @author Dirk Vranckaert
 */
public class GooglePlayServiceRequiredException extends Throwable {
    private int resultCode;

    public GooglePlayServiceRequiredException(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}

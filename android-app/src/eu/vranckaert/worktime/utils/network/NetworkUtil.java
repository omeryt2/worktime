package eu.vranckaert.worktime.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 13:25
 */
public class NetworkUtil {
    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();

    /**
     * Checks if the device is connected with internet or not.
     * @param ctx The app-context.
     * @return {@link Boolean#TRUE} if the device is connected or connecting, {@link Boolean#FALSE} if no connection is
     * available.
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(LOG_TAG, "Device is online");
            return true;
        }
        Log.d(LOG_TAG, "Device is not online");
        return false;
    }

    /**
     * Checks if the device can access a website (makes sure that proxy settings are ok).
     * @param endpoint The endpoint to try to reach.
     * @return {@link Boolean#TRUE} if the device can reach the endpoint, {@link Boolean#FALSE} if not.
     */
    public static boolean canReachEndpoint(String endpoint) {
        HttpGet requestForTest = new HttpGet(endpoint);
        try {
            HttpResponse response = new DefaultHttpClient().execute(requestForTest);
            int statusCode = response.getStatusLine().getStatusCode();
            Log.d(LOG_TAG, "Trying to surf with status code " + statusCode);
            if (statusCode == 200) {
                Log.d(LOG_TAG, "Device can surf");
                return true;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception, cannot surf");
        }
        Log.d(LOG_TAG, "Device cannot surf");
        return false;
    }

    /**
     * Tests if the device is connected to the internet and if the device can reach a website.
     * @param ctx The app-context.
     * @param endpoint The endpoint to try to reach.
     * @return {@link Boolean#TRUE} if the device is connected and can reach the endpoint website, {@link Boolean#FALSE}
     * if not.
     */
    public static boolean canSurf(Context ctx, String endpoint) {
        return (isOnline(ctx) && canReachEndpoint(endpoint));
    }
}

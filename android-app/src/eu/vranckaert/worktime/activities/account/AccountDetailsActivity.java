package eu.vranckaert.worktime.activities.account;

import android.app.Activity;
import android.os.Bundle;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 10:04
 */
public class AccountDetailsActivity extends ActionBarGuiceActivity {
    private AnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.);

        //setTitle(R.string.);
        setDisplayHomeAsUpEnabled(true);

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.ACCOUNT_DETAILS_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
package eu.vranckaert.worktime.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.preferences.AccountSyncPreferencesActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.exceptions.account.UserNotLoggedInException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.inject.InjectView;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 10:04
 */
public class AccountProfileActivity extends ActionBarGuiceActivity {
    private AnalyticsTracker tracker;

    @Inject private AccountService accountService;

    @InjectView(R.id.account_profile_container) private View container;
    @InjectView(R.id.account_profile_email) private TextView email;
    @InjectView(R.id.account_profile_name) private TextView name;
    @InjectView(R.id.account_profile_registered_since) private TextView registeredSince;
    @InjectView(R.id.account_profile_logged_in_since) private TextView loggedInSince;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_profile);

        setTitle(R.string.lbl_account_profile_title);
        setDisplayHomeAsUpEnabled(true);

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.ACCOUNT_DETAILS_ACTIVITY);

        new LoadProfileTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_account_profile, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        boolean r = super.onCreateOptionsMenu(menu);

        // Disable click on home-button
        getActionBarHelper().setHomeButtonEnabled(false);
        return r;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                IntentUtil.goBack(this);
                break;
            case R.id.menu_account_profile_activity_settings:
                Intent intent = new Intent(AccountProfileActivity.this, AccountSyncPreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_account_profile_activity_logout:
                new LogoutTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            getActionBarHelper().setLoadingIndicator(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            accountService.logout();
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            getActionBarHelper().setLoadingIndicator(false);
            setResult(Constants.IntentResultCodes.RESULT_LOGOUT);
            finish();
        }
    }

    private class LoadProfileTask extends AsyncTask<Void, Void, User> {
        private String errorMsg = null;
        private boolean logout = false;

        @Override
        protected User doInBackground(Void... params) {
            try {
                return accountService.loadUserData();
            } catch (UserNotLoggedInException e) {
                errorMsg = AccountProfileActivity.this.getString(R.string.lbl_account_profile_error_user_not_logged_in);
                logout = true;
                return null;
            } catch (GeneralWebException e) {
                errorMsg = AccountProfileActivity.this.getString(R.string.error_general_web_exception);
                return null;
            } catch (NoNetworkConnectionException e) {
                errorMsg = AccountProfileActivity.this.getString(R.string.error_no_network_connection);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null) {
                Toast.makeText(AccountProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                if (logout)
                    setResult(Constants.IntentResultCodes.RESULT_LOGOUT);
                else
                    setResult(RESULT_OK);
                finish();
            } else {
                updateUI(user);
            }
        }
    }

    private void updateUI(User user) {
        container.setVisibility(View.VISIBLE);

        email.setText(user.getEmail());
        name.setText(user.getFirstName() + " " + user.getLastName());
        registeredSince.setText(DateUtils.DateTimeConverter.convertDateTimeToString(user.getRegisteredSince(), DateFormat.MEDIUM, TimeFormat.MEDIUM, AccountProfileActivity.this));
        loggedInSince.setText(DateUtils.DateTimeConverter.convertDateTimeToString(user.getLoggedInSince(), DateFormat.MEDIUM, TimeFormat.MEDIUM, AccountProfileActivity.this));
    }
}
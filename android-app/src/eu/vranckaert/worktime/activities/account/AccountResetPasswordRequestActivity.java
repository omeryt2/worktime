package eu.vranckaert.worktime.activities.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockActivity;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.inject.InjectView;

/**
 * Date: 2/04/13
 * Time: 12:19
 *
 * @author Dirk Vranckaert
 */
public class AccountResetPasswordRequestActivity extends RoboSherlockActivity {
    @InjectView(R.id.account_reset_password_request_email) private EditText email;
    @InjectView(R.id.account_reset_password_request_error) private TextView errorTextView;

    @Inject
    private AccountService accountService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_account_reset_password_request);
        setSupportProgressBarIndeterminateVisibility(false);

        setTitle(R.string.lbl_account_reset_password_request_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_acount_reset_password_request, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                IntentUtil.goBack(this);
                break;
            case R.id.menu_account_reset_password_request_execute:
                if (StringUtils.isBlank(email.getText().toString())) {
                    email.setError(getText(R.string.lbl_account_reset_password_request_error_email_required));
                } else {
                    AsyncHelper.startWithParams(new ResetPasswordRequestTask(), new String[]{email.getText().toString()});
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ResetPasswordRequestTask extends AsyncTask<String, Void, Void> {
        private String error;

        @Override
        protected void onPreExecute() {
            errorTextView.setVisibility(View.GONE);
            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            String email = params[0];
            try {
                accountService.resetPasswordRequest(email);
            } catch (GeneralWebException e) {
                error = AccountResetPasswordRequestActivity.this.getString(R.string.error_general_web_exception);
            } catch (NoNetworkConnectionException e) {
                error = AccountResetPasswordRequestActivity.this.getString(R.string.error_no_network_connection);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setSupportProgressBarIndeterminateVisibility(false);
            if (StringUtils.isNotBlank(error)) {
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        }
    }
}
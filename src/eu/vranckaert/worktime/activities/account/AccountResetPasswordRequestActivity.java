package eu.vranckaert.worktime.activities.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.inject.InjectView;

/**
 * Date: 2/04/13
 * Time: 12:19
 *
 * @author Dirk Vranckaert
 */
public class AccountResetPasswordRequestActivity extends ActionBarGuiceActivity {
    @InjectView(R.id.account_reset_password_request_email) private EditText email;
    @InjectView(R.id.account_reset_password_request_error) private TextView errorTextView;

    @Inject
    private AccountService accountService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_reset_password_request);

        setTitle(R.string.lbl_account_reset_password_request_title);
        setDisplayHomeAsUpEnabled(true);

        errorTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
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
            getActionBarHelper().setRefreshActionItemState(true, R.id.menu_account_reset_password_request_execute);
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
            getActionBarHelper().setRefreshActionItemState(false, R.id.menu_account_reset_password_request_execute);
            if (StringUtils.isNotBlank(error)) {
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        }
    }
}
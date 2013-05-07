package eu.vranckaert.worktime.activities.account;

import android.net.Uri;
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
import eu.vranckaert.worktime.exceptions.worktime.account.InvalidPasswordResetKeyException;
import eu.vranckaert.worktime.exceptions.worktime.account.PasswordLengthValidationException;
import eu.vranckaert.worktime.exceptions.worktime.account.PasswordResetKeyAlreadyUsedException;
import eu.vranckaert.worktime.exceptions.worktime.account.PasswordResetKeyExpiredException;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockActivity;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.inject.InjectView;

/**
 * Date: 2/04/13
 * Time: 12:47
 *
 * @author Dirk Vranckaert
 */
public class AccountResetPasswordActivity extends RoboSherlockActivity {
    @InjectView(R.id.account_reset_password_new_password) private EditText newPassword;
    @InjectView(R.id.account_reset_password_repeat_password) private EditText repeatPassword;
    @InjectView(R.id.account_reset_password_error) private TextView errorTextView;

    @Inject
    private AccountService accountService;

    private String passwordResetRequestKey;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_account_reset_password);
        setSupportProgressBarIndeterminateVisibility(false);

        setTitle(R.string.lbl_account_reset_password_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorTextView.setVisibility(View.GONE);

        Uri data = getIntent().getData();
        passwordResetRequestKey = data.getPathSegments().get(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_acount_reset_password, menu);

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
            case R.id.menu_account_reset_password_execute:
                validateAndExecute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateAndExecute() {
        boolean valid = true;
        errorTextView.setVisibility(View.GONE);

        if (StringUtils.isBlank(newPassword.getText().toString())) {
            newPassword.setError(getText(R.string.lbl_account_reset_password_repeat_error_new_password_required));
            valid = false;
        }

        if (valid && !newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
            repeatPassword.setError(getText(R.string.lbl_account_reset_password_repeat_error_new_password_match_repeat_password));
            valid = false;
        }

        if (valid) {
            AsyncHelper.startWithParams(new ResetPasswordTask(), new String[]{passwordResetRequestKey, newPassword.getText().toString()});
        }
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, Void> {
        private String error;

        @Override
        protected void onPreExecute() {
            errorTextView.setVisibility(View.GONE);
            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            String passwordResetRequestKey = params[0];
            String newPassword = params[0];
            try {
                accountService.resetPassword(passwordResetRequestKey, newPassword);
            } catch (GeneralWebException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.error_general_web_exception);
            } catch (NoNetworkConnectionException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.error_no_network_connection);
            } catch (InvalidPasswordResetKeyException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.lbl_account_change_password_error_invalid_reset_key);
            } catch (PasswordResetKeyExpiredException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.lbl_account_change_password_error_reset_key_expired);
            } catch (PasswordResetKeyAlreadyUsedException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.lbl_account_change_password_error_reset_key_already_used);
            } catch (PasswordLengthValidationException e) {
                error = AccountResetPasswordActivity.this.getString(R.string.lbl_account_change_password_error_password_length_invalid);
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
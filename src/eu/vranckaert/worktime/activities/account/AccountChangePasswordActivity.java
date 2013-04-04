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
import eu.vranckaert.worktime.exceptions.worktime.account.LoginCredentialsMismatchException;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.inject.InjectView;

/**
 * Date: 27/03/13
 * Time: 15:39
 *
 * @author Dirk Vranckaert
 */
public class AccountChangePasswordActivity extends ActionBarGuiceActivity {
    @InjectView(R.id.account_change_password_old_password)
    private EditText oldPassword;
    @InjectView(R.id.account_change_password_new_password)
    private EditText newPassword;
    @InjectView(R.id.account_change_password_new_password_repeat)
    private EditText newPasswordRepeat;
    @InjectView(R.id.account_change_password_error)
    private TextView errorTextView;

    @Inject
    private AccountService accountService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_change_password);

        setTitle(R.string.lbl_account_change_password_title);
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_account_change_password, menu);

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
            case R.id.menu_account_change_password_activity_change: {
                validateAndSave();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateAndSave() {
        errorTextView.setVisibility(View.GONE);

        boolean valid = true;

        oldPassword.setError(null);
        newPassword.setError(null);
        newPasswordRepeat.setError(null);

        if (StringUtils.isBlank(oldPassword.getText().toString())) {
            oldPassword.setError(getText(R.string.error_general_required_field));
            valid = false;
        }
        if (StringUtils.isBlank(newPassword.getText().toString())) {
            newPassword.setError(getText(R.string.error_general_required_field));
            valid = false;
        }
        if (StringUtils.isBlank(newPasswordRepeat.getText().toString())) {
            newPasswordRepeat.setError(getText(R.string.error_general_required_field));
            valid = false;
        }

        if (valid) {
            if (!newPassword.getText().toString().equals(newPasswordRepeat.getText().toString())) {
                newPassword.setError(getText(R.string.lbl_account_change_password_new_password_error_new_password_equals));
                newPasswordRepeat.setError(getText(R.string.lbl_account_change_password_new_password_error_new_password_equals));
                valid = false;
            }
        }

        if (!valid)
            return;

        AsyncHelper.startWithParams(new ChangePasswordTask(), new String[] {oldPassword.getText().toString(), newPassword.getText().toString()});
    }

    private class ChangePasswordTask extends AsyncTask<String, Void, Void> {
        boolean oldPasswordIncorrect = false;
        String error = null;

        @Override
        protected void onPreExecute() {
            getActionBarHelper().setRefreshActionItemState(true, R.id.menu_account_change_password_activity_change);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                accountService.changePassword(params[0], params[1]);
            } catch (GeneralWebException e) {
                error = AccountChangePasswordActivity.this.getString(R.string.error_general_web_exception);
            } catch (NoNetworkConnectionException e) {
                error = AccountChangePasswordActivity.this.getString(R.string.error_no_network_connection);
            } catch (LoginCredentialsMismatchException e) {
                oldPasswordIncorrect = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (oldPasswordIncorrect) {
                oldPassword.setError(getText(R.string.lbl_account_change_password_old_password_incorrect));
            } else if (StringUtils.isNotBlank(error)) {
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                finish();
            }

            getActionBarHelper().setRefreshActionItemState(false, R.id.menu_account_change_password_activity_change);
        }
    }
}

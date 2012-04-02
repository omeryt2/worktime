/*
 * Copyright 2012 Dirk Vranckaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.activities.backup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.exceptions.SDCardUnavailableException;
import eu.vranckaert.worktime.exceptions.backup.BackupFileCouldNotBeCreated;
import eu.vranckaert.worktime.exceptions.backup.BackupFileCouldNotBeWritten;
import eu.vranckaert.worktime.service.BackupService;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;

/**
 * User: DIRK VRANCKAERT
 * Date: 11/09/11
 * Time: 11:49
 */
public class BackupActivity extends GuiceActivity {
    private static final String LOG_TAG = BackupActivity.class.getSimpleName();

    @Inject
    private BackupService backupService;

    private String result;
    private String error;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncTask backupTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.BACKUP_IN_PROGRESS);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                try {
                    return backupService.backup(getApplicationContext());
                } catch (SDCardUnavailableException e) {
                    error = getString(R.string.msg_backup_restore_sd_card_unavailable);
                    return false;
                } catch (BackupFileCouldNotBeCreated backupFileCouldNotBeCreated) {
                    error = getString(R.string.msg_backup_restore_writing_backup_file_not_be_created);
                    return false;
                } catch (BackupFileCouldNotBeWritten backupFileCouldNotBeWritten) {
                    error = getString(R.string.msg_backup_restore_writing_backup_file_not_written);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.BACKUP_IN_PROGRESS);

                if (StringUtils.isNotBlank(error)) {
                    showDialog(Constants.Dialog.BACKUP_ERROR);
                } else {
                    result = (String) o;
                    showDialog(Constants.Dialog.BACKUP_SUCCESS);
                }
            }
        };
        backupTask.execute();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.BACKUP_IN_PROGRESS: {
                dialog = ProgressDialog.show(
                        BackupActivity.this,
                        "",
                        getString(R.string.lbl_backup_restore_writing_backup_sd),
                        true,
                        false
                );
                break;
            }
            case Constants.Dialog.BACKUP_SUCCESS: {
                AlertDialog.Builder alertBackupSuccess = new AlertDialog.Builder(this);
				alertBackupSuccess
						   .setMessage(getString(R.string.msg_backup_restore_writing_backup_sd_success, result))
						   .setCancelable(false)
						   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   finish();
                                   dialog.cancel();
                               }
                           });
				dialog = alertBackupSuccess.create();
                break;
            }
            case Constants.Dialog.BACKUP_ERROR: {
                AlertDialog.Builder alertBackupSuccess = new AlertDialog.Builder(this);
				alertBackupSuccess
						   .setMessage(error)
						   .setCancelable(false)
						   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   finish();
                                   dialog.cancel();
                               }
                           });
				dialog = alertBackupSuccess.create();
                break;
            }
        }

        return dialog;
    }


}
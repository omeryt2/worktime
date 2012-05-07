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
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import roboguice.activity.GuiceActivity;

/**
 * User: DIRK VRANCKAERT
 * Date: 1/02/12
 * Time: 8:34
 */
public class BackupRestoreInfoActivity extends GuiceActivity {
    private static final String LOG_TAG = BackupRestoreInfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showDialog(Constants.Dialog.BACKUP_RESTORE_DOCUMENTATION);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.BACKUP_RESTORE_DOCUMENTATION: {
                AlertDialog.Builder alertBackupSuccess = new AlertDialog.Builder(this);
                alertBackupSuccess
                        .setTitle(R.string.lbl_backup_restore_documentation_title)
                        .setMessage(Html.fromHtml("<b><u>General</u></b><br/><br/>" +
                                "The backups and restore mechanism can only be used if it's been <b>activated on your device</b>: go to system 'Settings', select 'Privacy', then enable 'Backup my data' and 'Automatic restore'.<br/>" +
                                "The backups are then stored in the <b>Google cloud</b> for your account and contain <b>all the application data</b> (time registrations, projects, tasks and preferences).<br/>" +
                                "The advantage of this system is that whenever you loose your device, you can just logon to another device and all your WorkTime data will be restored!<br/><br/>" +
                                "<b><u>Backup</u></b><br/><br/>" +
                                "Backups will automatically be created whenever starting/stopping a time registration, when accessing the application or when changing preferences.<br/><br/>" +
                                "<b><u>Restore</u></b><br/><br/>" +
                                "When you lost your phone, buy a new one and login with your Google account, the WorkTime application will be restored together with all your WorkTime data based on the latest backup that is created. This guarantees you a minimal amount of data loss!"))
                        .setCancelable(true)
                        .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                dialog = alertBackupSuccess.create();
                break;
            }
        }

        return dialog;
    }
}

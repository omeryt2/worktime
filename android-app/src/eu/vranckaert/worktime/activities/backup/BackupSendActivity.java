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
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.comparators.preferences.DatabaseBackupFileComparator;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.exceptions.SDCardUnavailableException;
import eu.vranckaert.worktime.service.BackupService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 7/02/12
 * Time: 13:48
 */
public class BackupSendActivity extends GuiceActivity {
    private static final String LOG_TAG = BackupSendActivity.class.getSimpleName();

    @Inject
    private BackupService backupService;

    private List<File> databaseBackupFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            databaseBackupFiles = backupService.getPossibleRestoreFiles(getApplicationContext());
            if (databaseBackupFiles == null || databaseBackupFiles.isEmpty()) {
                showDialog(Constants.Dialog.BACKUP_SEND_FILE_SEARCH_NOTHING_FOUND);
            } else {
                Collections.sort(databaseBackupFiles, new DatabaseBackupFileComparator());
                showDialog(Constants.Dialog.BACKUP_SEND_FILE_SEARCH_SHOW_LIST);
            }
        } catch (SDCardUnavailableException e) {
            showDialog(Constants.Dialog.BACKUP_SEND_FILE_SEARCH_NO_SD);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.BACKUP_SEND_FILE_SEARCH_NOTHING_FOUND: {
                AlertDialog.Builder alertRestoreNothingFound = new AlertDialog.Builder(this);
                alertRestoreNothingFound
                        .setMessage(getString(R.string.msg_backup_restore_no_backup_files_found))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                dialog.cancel();
                            }
                        });
                dialog = alertRestoreNothingFound.create();
                break;
            }
            case Constants.Dialog.BACKUP_SEND_FILE_SEARCH_NO_SD: {
                AlertDialog.Builder alertRestoreNoSd = new AlertDialog.Builder(this);
                alertRestoreNoSd
                        .setMessage(getString(R.string.msg_backup_restore_sd_card_unavailable))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                dialog.cancel();
                            }
                        });
                dialog = alertRestoreNoSd.create();
                break;
            }
            case Constants.Dialog.BACKUP_SEND_FILE_SEARCH_SHOW_LIST: {
                List<String> fileNames = new ArrayList<String>();
                for (File file : databaseBackupFiles) {
                    Log.d(LOG_TAG, "Filename found: " + file.getName());
                    fileNames.add(backupService.toString(BackupSendActivity.this, file));
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_backup_restore_send_backup_list_title)
                        .setSingleChoiceItems(
                                StringUtils.convertListToArray(fileNames),
                                0,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        Log.d(LOG_TAG, "File at index " + index + " choosen.");
                                        File fileToSend = databaseBackupFiles.get(index);
                                        removeDialog(Constants.Dialog.BACKUP_RESTORE_FILE_SEARCH_SHOW_LIST);
                                        sendFile(fileToSend); 
                                    }
                                }
                        )
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialogInterface) {
                                Log.d(LOG_TAG, "No backup file chosen, close the activity");
                                BackupSendActivity.this.finish();
                            }
                        });
                dialog = builder.create();
                break;
            }
        }

        return dialog;
    }

    private void sendFile(File file) {
        IntentUtil.sendSomething(BackupSendActivity.this, -1, -1, file, R.string.lbl_pref_backup_send_app_chooser_title);
        finish();
    }
}

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

package eu.vranckaert.worktime.utils.context;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.dao.utils.DaoConstants;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.service.ui.impl.StatusBarNotificationServiceImpl;
import eu.vranckaert.worktime.service.ui.impl.WidgetServiceImpl;
import eu.vranckaert.worktime.utils.file.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseBackupAgent extends BackupAgentHelper {
    private static final String LOG_TAG = DatabaseBackupAgent.class.getSimpleName();

    private static final String DATABASE_BACKUP_FILE_NAME = "databaseBackupFile.db";

    private static final String PREFERENCES_BACKUP_KEY = "preferences";
    private static final String DATABASE_BACKUP_KEY = "database";

    @Override
    public void onCreate() {
        // Backup the preferences
        SharedPreferencesBackupHelper prefHelper = new SharedPreferencesBackupHelper(this, Constants.Preferences.PREFERENCES_NAME);
        addHelper(PREFERENCES_BACKUP_KEY, prefHelper);

        // Backup the database file
        FileBackupHelper fileHelper = new FileBackupHelper(this, DATABASE_BACKUP_FILE_NAME);
        addHelper(DATABASE_BACKUP_KEY, fileHelper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        // Check if a backupFile already exists, if so, delete it.
        File backupFile = new File(getFilesDir(), DATABASE_BACKUP_FILE_NAME);
        if (backupFile.exists()) {
            Log.d(getApplicationContext(), LOG_TAG, "The database backup file already exists, deleting it now");
            backupFile.delete();
        }

        // Then create the backup file, but empty!
        FileOutputStream fos = openFileOutput(DATABASE_BACKUP_FILE_NAME, Context.MODE_PRIVATE);
        fos.close();

        // Now copy all the data from the database file to the backup file
        File dbFile = new File(FileUtil.getDatabaseDirectory(this) + File.separator + DaoConstants.DATABASE);
        try {
            FileUtil.copyFile(dbFile, backupFile);
            Log.d(getApplicationContext(), LOG_TAG, "The content of the database file is correctly copied to the backup file");
        } catch (IOException e) {
            Log.e(getApplicationContext(), LOG_TAG, "The content of the database file could not be copied to the backup file", e);
        }

        // Do the actual backup
        super.onBackup(oldState, data, newState);

        // Remove the backupFile again
        if (backupFile.exists()) {
            Log.d(getApplicationContext(), LOG_TAG, "The backup file will be removed again...");
            backupFile.delete();
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);

        // Now re-create the dbFile
        // First check if the db-directory already exists
        File dbDir = FileUtil.getDatabaseDirectory(this);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
            FileUtil.applyPermissions(dbDir, true, true, false, true);
            Log.d(getApplicationContext(), LOG_TAG, "The database directory has been created");
        }
        // Then check if the db-file already exists
        File dbFile = new File(FileUtil.getDatabaseDirectory(this) + File.separator + DaoConstants.DATABASE);
        Log.d(getApplicationContext(), LOG_TAG, "Restoring backup to database file " + dbFile.getAbsolutePath());
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
                Log.d(getApplicationContext(), LOG_TAG, "Needed to create the DB file on the device first...");
            } catch (IOException e) {
                Log.e(getApplicationContext(), LOG_TAG, "Could not create the database file, quiting restore now", e);
                return;
            }

            // Apply the correct permission on the database file
            FileUtil.applyPermissions(dbFile, true, true, false, true);
        }

        // Copy the contents of the backup file over the database file
        File backupFile = new File(getFilesDir(), DATABASE_BACKUP_FILE_NAME);
        try {
            FileUtil.copyFile(backupFile, dbFile);
        } catch (IOException e) {
            Log.e(getApplicationContext(), LOG_TAG, "The database backup could not be restored, quitting restore now", e);
            return;
        }

        // Remove the backupFile again
        Log.d(getApplicationContext(), LOG_TAG, "The backup file will be removed");
        backupFile.delete();

        Log.d(getApplicationContext(), LOG_TAG, "Database restore was successful");

        Log.d(getApplicationContext(), LOG_TAG, "Ready to start updating the widget(s)");
        WidgetService widgetService = new WidgetServiceImpl(this);
        widgetService.updateAllWidgets();

        Log.d(getApplicationContext(), LOG_TAG, "Ready to start updating the notifications");
        StatusBarNotificationService notificationService = new StatusBarNotificationServiceImpl(this);
        notificationService.addOrUpdateNotification(null);

        Log.d(getApplicationContext(), LOG_TAG, "Adding notification for successful restore!");
        notificationService.addStatusBarNotificationForRestore();
    }
}

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
package eu.vranckaert.worktime.utils.file;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.OSContants;
import eu.vranckaert.worktime.utils.context.ContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/09/11
 * Time: 16:55
 */
public class FileUtil {
    private static final String LOG_TAG = FileUtil.class.getSimpleName();
    
    /**
     * Copy the content from one file to another.
     * @param src The source to copy.
     * @param dest The destination to copy to.
     * @throws IOException
     */
    public static void copyFile(File src, File dest) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dest).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * Get the directory where databases are stored on the device.
     * @param ctx The context.
     * @return The database directory.
     */
    public static File getDatabaseDirectory(Context ctx) {
        File file = new File(
                Environment.getDataDirectory() + "/data/" + ContextUtils.getApplicationPackage(ctx) + "/databases/"
        );
        Log.d(LOG_TAG, "Database directory: " + file.getAbsolutePath());
        return file;
    }

    /**
     * Get the external file directory, platform/API-version independent. The path will end with something similar
     * to: /Android/data/<package name>/files/.
     * @param ctx The context.
     * @return The external file directory.
     */
    public static File getExternalFilesDir(Context ctx) {
        return getExternalFilesDir(ctx, null);
    }

    /**
     * Get the external file directory, platform/API-version independent. The type of directory can be specified and
     * will be used by all OS versions after Eclair (API lvl 17) to index the data in the directory correctly. The path
     * will end with something similar to: /Android/data/<package name>/files/.
     * @param ctx The context.
     * @param type The {@link eu.vranckaert.worktime.constants.OSContants.DirectoryContentType}.
     * @return The external file directory.
     */
    public static File getExternalFilesDir(Context ctx, OSContants.DirectoryContentType type) {
        File externalDirFile = null;
        int apiVersion = ContextUtils.getAndroidApiVersion();
        if (apiVersion <= OSContants.API.ECLAIR) {
            Log.d(LOG_TAG, "API version of the device OS is " + apiVersion + ". We will ignore the provided type.");
            File dir = Environment.getExternalStorageDirectory();
            String appPackage = ContextUtils.getApplicationPackage(ctx);
            String externalDir = dir.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + appPackage + File.separator + "files" + File.separator;
            externalDirFile = new File(externalDir);
        } else {
            if (type == null) {
                externalDirFile  = ctx.getExternalFilesDir(null);
            } else {
                externalDirFile = ctx.getExternalFilesDir(type.getType());
            }
        }

        if (externalDirFile != null) {
            if (!externalDirFile.exists()) {
                Log.d(LOG_TAG, "The external files directory structure (" + externalDirFile.getAbsolutePath() + ") does not yet exists, creating it now...");
                externalDirFile.mkdirs();
            }
            Log.d(LOG_TAG, "External files directory: " + externalDirFile.getAbsolutePath());
        } else {
            Log.d(LOG_TAG, "No external files directory found!");
        }

        return externalDirFile;
    }

    /**
     * Get the directory to be used to save/read backup-files.
     * @return a {@link File} representing the backup directory.
     */
    public static File getBackupDir() {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(
                dir.getAbsolutePath() +
                File.separator +
                Constants.Disk.BACKUP_DIRECTORY +
                File.separator
        );

        applyPermissions(file);

        checkIfDirectoryExists(file);
        
        return file;
    }

    /**
     * Get the directory to be used to save/read export-files.
     * @param ctx The context.
     * @return a {@link File} representing the export directory. Null if the export directory is not available.
     */
    public static File getExportDir(Context ctx) {
        File externalFilesDir = getExternalFilesDir(ctx);
        if (externalFilesDir == null) {
            return null;
        }

        File file = new File(
                externalFilesDir +
                File.separator +
                Constants.Disk.EXPORT_DIRECTORY +
                File.separator
        );

        applyPermissions(file);

        checkIfDirectoryExists(file);

        return file;
    }

    /**
     * Checks if a certain {@link File} is a directory (if not it will delete the file and create the directory) and if
     * the directory exists (if not it will create the directory).
     * @param file The {@link File} to check.
     */
    private static void checkIfDirectoryExists(File file) {
        if (file.exists() && file.isFile()) {
            Log.d(LOG_TAG, "Directory seems to be a file... Deleting it now...");
            file.delete();
        }
        if (!file.exists()) {
            Log.d(LOG_TAG, "Directory does not exist yet! Creating it now...");
            file.mkdir();
        }
    }

    /**
     * Modify the rights of a {@link File} to be readable, writable and executable to everyone. This is not supported on
     * pre 3.0 Android devices. The method will not throw an exception but will end silent!
     * @param file The {@Link File} to modify the rights on.
     */
    public static void applyPermissions(File file) {
        applyPermissions(file, true, true, true);
    }

    /**
     * Modify the rights of a {@link File}. You can define it should be readable, writable and executable by everyone.
     * This is not supported on pre 3.0 Android devices. The method will not throw an exception but will end silent!
     * @param file The @{Link File} for which the rights will be modified.
     * @param readable If the file should be readable for everyone.
     * @param writable If the file should be writable for everyone.
     * @param executable If the file should be executable for everyone.
     */
    public static void applyPermissions(File file, boolean readable, boolean writable, boolean executable) {
        applyPermissions(file, readable, writable, executable, false);
    }

    /**
     * Modify the rights of a {@link File}. You can define it should be readable, writable and executable. You can
     * specify if these settings should be applied for everyone or just the owner of the file. This is not supported on
     * pre 3.0 Android devices. The method will not throw an exception but will end silent!
     * @param file The @{Link File} for which the rights will be modified.
     * @param readable If the file should be readable for everyone.
     * @param writable If the file should be writable for everyone.
     * @param executable If the file should be executable for everyone.
     * @param ownerOnly If these changes will be applied only for the owner of the file or for everyone.
     */
    public static void applyPermissions(File file, boolean readable, boolean writable, boolean executable, boolean ownerOnly) {
        applyPermissions(file, readable, writable, executable, ownerOnly, ownerOnly, ownerOnly);
    }

    /**
     * Modify the rights of a {@link File}. You can define it should be readable, writable and executable. You can
     * specify per right if the setting should be applied for everyone or just the owner of the file. This is not
     * supported on pre 3.0 Android devices. The method will not throw an exception but will end silent!
     * @param file The @{Link File} for which the rights will be modified.
     * @param readable If the file should be readable for everyone.
     * @param writable If the file should be writable for everyone.
     * @param executable If the file should be executable for everyone.
     * @param readableOwnerOnly If the readable settings will be applied only for the owner of the file or for everyone.
     * @param writableOwnerOnly If the writable settings will be applied only for the owner of the file or for everyone.
     * @param executableOwnerOnly If the executable settings will be applied only for the owner of the file or for everyone.
     */
    public static void applyPermissions(File file, boolean readable, boolean writable, boolean executable,
                                        boolean readableOwnerOnly, boolean writableOwnerOnly, boolean executableOwnerOnly) {
        if (ContextUtils.getAndroidApiVersion() < OSContants.API.HONEYCOMB_3_0) {
            Log.i(LOG_TAG, "File right modifications are not permitted on Android systems running an OS version pre 3.0!");
            return;
        }
        
        boolean readableResult = file.setReadable(readable, readableOwnerOnly);
        boolean writableResult = file.setWritable(writable, writableOwnerOnly);
        boolean executableResult = file.setExecutable(executable, executableOwnerOnly);

        if (readableResult) {
            Log.d(LOG_TAG, "The file permission for 'readable' have been changed successfully!");
            Log.d(LOG_TAG, "The file is now " + (readable ? "readable" : "unreadable") + " for " + (readableOwnerOnly ? "the owner only" : "everyone"));
        } else {
            Log.d(LOG_TAG, "The file permission for 'readable' could not be changed!");
        }

        if (writableResult) {
            Log.d(LOG_TAG, "The file permission for 'writable' have been changed successfully!");
            Log.d(LOG_TAG, "The file is now " + (writable ? "writable" : "not-writable") + " for " + (writableOwnerOnly ? "the owner only" : "everyone"));
        } else {
            Log.d(LOG_TAG, "The file permission for 'writable' could not be changed!");
        }

        if (executableResult) {
            Log.d(LOG_TAG, "The file permission for 'executable' have been changed successfully!");
            Log.d(LOG_TAG, "The file is now " + (executable ? "executable" : "not-executable") + " for " + (executableOwnerOnly ? "the owner only" : "everyone"));
        } else {
            Log.d(LOG_TAG, "The file permission for 'executable' could not be changed!");
        }
    }

    public static void enableForMTP(Context ctx, File file) {
        if (file == null) {
            return;
        }

        if (ContextUtils.getAndroidApiVersion() < OSContants.API.HONEYCOMB_3_0) {
            Log.i(LOG_TAG, "Android only supports MTP since android 3.0!");
            return;
        }

        MediaScannerConnection.scanFile(
                ctx, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(LOG_TAG, "Scanned " + path);
                        Log.i(LOG_TAG, "-> uri=" + uri);
                    }
                }
        );
    }
}

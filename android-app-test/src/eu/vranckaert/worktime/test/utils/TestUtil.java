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

package eu.vranckaert.worktime.test.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.support.ConnectionSource;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.dao.generic.GenericDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.dao.utils.DatabaseHelper;
import eu.vranckaert.worktime.utils.preferences.Preferences;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.fail;

/**
 * User: DIRK VRANCKAERT
 * Date: 15/03/12
 * Time: 8:39
 */
public class TestUtil {
    private static final String LOG_TAG = TestUtil.class.getSimpleName();
    private static final String[] dbTables = {"commentHistory", "project", "task", "timeregistration"};
    
    public static class Time {
        public static final Long FIVE_SECONDS = 5000L;
        public static final Long TEN_SECONDS = 10000L;
        public static final Long TWENTY_SECONDS = 20000L;
        public static final Long THIRTY_SECONDS = 30000L;
        public static final Long SIXTY_SECONDS = 60000L;

        public static final Long TWO_MINUTES = 120000L;
    }

    /**
     * Removes the database
     */
    public static void cleanUpDatabase(Context ctx) {
        Log.i(LOG_TAG, "Preparing to clean up database...");
        DatabaseHelper dbHelper = new DatabaseHelper(ctx);
        ConnectionSource cs = dbHelper.getConnectionSource();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.i(LOG_TAG, "Dropping all tables");
        for (String table : dbTables) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }

        Log.i(LOG_TAG, "Executing the onCreate(..)");
        dbHelper.onCreate(db, cs);

        Log.i(LOG_TAG, "Verifying the data...");
        for (String table : dbTables) {
            Cursor c = db.query(table, new String[]{"id"}, null, null, null, null, null);
            int count = c.getCount();
            if (count != 1 && (table.equals("project") || table.equals("task"))) {
                dbHelper.close();
                Log.e(LOG_TAG, "We should have 1 record for table " + table + " after cleanup but we found " + count + " record(s)");
                throw new RuntimeException("Error during cleanup of DB, exactly one record should be present for table " + table + " but we found " + count + " record(s)");
            } else if (count != 0 && !(table.equals("project") || table.equals("task"))) {
                dbHelper.close();
                Log.e(LOG_TAG, "We should have 0 records for table " + table + " after cleanup but we found " + count + " record(s)");
                throw new RuntimeException("Error during cleanup of DB, no records should be present for table " + table + " but we found " + count + " record(s)");
            }
        }

        Log.i(LOG_TAG, "The database has been cleaned!");
        dbHelper.close();
    }

    /**
     * Removes all the preferences
     */
    public static void removeAllPreferences(Context ctx) {
        List<String> preferenceKeys = new ArrayList<String>();

        Class prefKeysClass = Constants.Preferences.Keys.class;
        Field[] keyFields = prefKeysClass.getFields();
        for(Field field : keyFields) {
            try {
                preferenceKeys.add((String)field.get(null));
            } catch (IllegalAccessException e) {}
        }

        for (String key : preferenceKeys) {
            Preferences.removePreference(ctx, key);
        }
    }

    /**
     * Set a preference, defined by the key parameter, to a certain value.
     * @param ctx The context to be used for setting a preference.
     * @param key The key referring to the preference.
     * @param value The value of the preference to set.
     */
    public static void setPreference(Context ctx, String key, Object value) {
        SharedPreferences sp = ctx.getSharedPreferences(Constants.Preferences.PREFERENCES_NAME, Activity.MODE_PRIVATE);

        if (value == null) {
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }

    /**
     * Get an instance for a certain DAO implementation class.
     * @param ctx The context.
     * @param daoInterface The interface class reference of the DAO to load.
     * @param daoClass The implementation class reference of the DAO to load.
     * @param <F> The interface class.
     * @param <D> The implementation class.
     * @return A DAO instance that extends {@link GenericDaoImpl}.
     * @throws Exception If the DAO cannot be resolved.
     */
    public static <F extends GenericDao, D extends GenericDaoImpl> F getDaoForClass(Context ctx, Class<F> daoInterface, Class<D> daoClass) {
        try {
            Constructor<D> constructor = daoClass.getConstructor(Context.class);
            return (F) constructor.newInstance(ctx);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not create DAO class!", e);
            fail();
        }
        return null;
    }
}

/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.utils.view.actionbar.synclock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.SyncHistory;
import eu.vranckaert.worktime.model.SyncHistoryAction;
import eu.vranckaert.worktime.model.SyncHistoryStatus;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;

/**
 * User: Dirk Vranckaert
 * Date: 16/01/13
 * Time: 15:20
 */
public class SyncLockingActivity extends ActionBarGuiceActivity {
    private AsyncTask<Void, SyncHistoryAction, Void> syncCheck;

    @Inject
    private AccountService accountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sync_locking);

        setTitle(R.string.lbl_sync_blocking_title);
        setDisplayHomeAsUpEnabled(false);
    }

    private class SyncCheck extends AsyncTask<Void, SyncHistoryAction, Void> {
        SyncHistoryAction currentAction = null;

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                if (!accountService.isSyncBusy()) {
                    return null;
                }

                SyncHistoryAction newAction = accountService.getLastSyncHistory().getAction();
                if (currentAction == null || !currentAction.equals(newAction)) {
                    publishProgress(newAction);
                }
                currentAction = newAction;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(SyncHistoryAction... values) {
            SyncHistoryAction action = values[0];
            // TODO show update the TextView!!!!
            Toast.makeText(SyncLockingActivity.this, action.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            stopLocking();
        }
    }

    private void stopLocking() {
        SyncHistory syncHistory = accountService.getLastSyncHistory();
        if (syncHistory.getStatus().equals(SyncHistoryStatus.FAILED) || syncHistory.getStatus().equals(SyncHistoryStatus.FAILED)) {
            setResult(Constants.IntentResultCodes.SYNC_COMPLETED_ERROR);
        } else {
            setResult(Constants.IntentResultCodes.SYNC_COMPLETED_SUCCESS);
        }
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncCheck = new SyncCheck();
        syncCheck.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncCheck.cancel(true);
    }

    @Override
    public void finish() {
        // Do nothing
    }
}

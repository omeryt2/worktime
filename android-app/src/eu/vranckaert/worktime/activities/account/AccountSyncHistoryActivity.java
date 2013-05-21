/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.activities.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.account.listadapter.SyncHistoryListAdapter;
import eu.vranckaert.worktime.model.SyncHistory;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.view.actionbar.synclock.SyncLockedListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dirk Vranckaert
 * Date: 11/02/13
 * Time: 13:22
 */
public class AccountSyncHistoryActivity extends SyncLockedListActivity {
    @Inject private AccountService accountService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sync_history);

        setTitle(R.string.lbl_account_sync_history_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void refillListView(List<SyncHistory> syncHistories) {
        List<SyncHistory> listOfNewSyncHistories = new ArrayList<SyncHistory>();
        listOfNewSyncHistories.addAll(syncHistories);

        if (getListView().getAdapter() == null) {
            SyncHistoryListAdapter adapter = new SyncHistoryListAdapter(AccountSyncHistoryActivity.this, listOfNewSyncHistories);
            setListAdapter(adapter);
        } else {
            ((SyncHistoryListAdapter) getListView().getAdapter()).refill(listOfNewSyncHistories);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.ab_activity_account_sync_history, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                IntentUtil.goBack(this);
                break;
            case R.id.menu_account_sync_history_activity_clear:
                clearSyncHistory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearSyncHistory() {
        AlertDialog dialog = new AlertDialog.Builder(AccountSyncHistoryActivity.this)
                .setTitle(R.string.lbl_account_sync_history_clear_dialog_title)
                .setMessage(R.string.lbl_account_sync_history_clear_dialog_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        accountService.clearSyncHistory();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refillListView(accountService.findAllSyncHistories());
    }
}
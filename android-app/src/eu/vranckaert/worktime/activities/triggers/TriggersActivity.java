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

package eu.vranckaert.worktime.activities.triggers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockActivity;
import roboguice.inject.ContentView;

/**
 * User: DIRK VRANCKAERT
 * Date: 22/05/13
 * Time: 8:00
 */
@ContentView(R.layout.activity_triggers)
public class TriggersActivity extends RoboSherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void geoFencesClicked(View view) {
        Intent intent = new Intent(TriggersActivity.this, TriggerGeoFencingMapActivity.class);
        startActivity(intent);
    }

    public void wifiNetworksClicked(View view) {
    }

    public void recurringClicked(View view) {
        Intent intent = new Intent(TriggersActivity.this, TriggerRecurringAddEditActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                IntentUtil.goBack(TriggersActivity.this);
            }
        }

        return true;
    }
}

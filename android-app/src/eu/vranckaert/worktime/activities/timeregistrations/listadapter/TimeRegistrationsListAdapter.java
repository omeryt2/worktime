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

package eu.vranckaert.worktime.activities.timeregistrations.listadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationsActivity;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.utils.context.Log;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;
import eu.vranckaert.worktime.utils.string.StringUtils;

import java.util.List;

/**
 * The list adapater private inner-class used to display the manage projects list.
 */
public class TimeRegistrationsListAdapter extends ArrayAdapter<TimeRegistration> {
    private final String LOG_TAG = TimeRegistrationsListAdapter.class.getSimpleName();

    private TimeRegistrationsActivity ctx;
    private List<TimeRegistration> timeRegistrations;
    /**
     * {@inheritDoc}
     */
    public TimeRegistrationsListAdapter(TimeRegistrationsActivity ctx, List<TimeRegistration> timeRegistrations) {
        super(ctx, R.layout.list_item_time_registrations, timeRegistrations);
        Log.d(ctx, LOG_TAG, "Creating the time registrations list adapter");

        this.ctx = ctx;
        this.timeRegistrations = timeRegistrations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(ctx, LOG_TAG, "Start rendering/recycling row " + position);
        View row = null;
        final TimeRegistration tr = timeRegistrations.get(position);

        if (tr.getId().equals(ctx.loadExtraTimeRegistration.getId())) {
            row = ctx.getLayoutInflater().inflate(R.layout.list_item_time_registrations_load_more, parent, false);
            return row;
        }

        Log.d(ctx, LOG_TAG, "Got time registration with startDate " +
                DateUtils.DateTimeConverter.convertDateTimeToString(tr.getStartTime(),
                        DateFormat.FULL,
                        TimeFormat.MEDIUM,
                        ctx));

        if (convertView == null) {
            Log.d(ctx, LOG_TAG, "Render a new line in the list");
            row = ctx.getLayoutInflater().inflate(R.layout.list_item_time_registrations, parent, false);
        } else {
            Log.d(ctx, LOG_TAG, "Recycling an existing line in the list");
            row = convertView;

            if ((TextView) row.findViewById(R.id.lbl_timereg_startdate) == null) {
                row = ctx.getLayoutInflater().inflate(R.layout.list_item_time_registrations, parent, false);
            }
        }

        Log.d(ctx, LOG_TAG, "Ready to update the startdate, enddate and projectname of the timeregistration...");
        TextView startDate = (TextView) row.findViewById(R.id.lbl_timereg_startdate);
        startDate.setText(DateUtils.DateTimeConverter.convertDateTimeToString(tr.getStartTime(), DateFormat.MEDIUM,
                TimeFormat.MEDIUM, ctx));
        TextView endDate = (TextView) row.findViewById(R.id.lbl_timereg_enddate);
        String endDateStr = "";
        if(tr.getEndTime() == null) {
            endDateStr = ctx.getString(R.string.now);
        } else {
            endDateStr = DateUtils.DateTimeConverter.convertDateTimeToString(tr.getEndTime(), DateFormat.MEDIUM,
                    TimeFormat.MEDIUM, ctx);
        }
        endDate.setText(endDateStr);
        TextView projectNameTaskName = (TextView) row.findViewById(R.id.lbl_timereg_projectname_taskname);
        String projectAndTaskText = tr.getTask().getProject().getName() +
                " " + ctx.getString(R.string.dash) + " " + tr.getTask().getName();
        projectNameTaskName.setText(projectAndTaskText);

        Log.d(ctx, LOG_TAG, "Ready to update the duration of the timeregistration...");
        TextView durationView = (TextView) row.findViewById(R.id.lbl_timereg_duration);
        String durationText = DateUtils.TimeCalculator.calculatePeriod(ctx.getApplicationContext(), tr);
        durationView.setText(durationText);

        Log.d(ctx, LOG_TAG, "Ready to set the comment if available...");
        View view = row.findViewById(R.id.registrations_comment_view);
        if (StringUtils.isNotBlank(tr.getComment())) {
            Log.d(ctx, LOG_TAG, "CommentHistory available...");
            view.setVisibility(View.VISIBLE);
            TextView commentTextView = (TextView) row.findViewById(R.id.lbl_registrations_comment);
            commentTextView.setText(tr.getComment());
        } else {
            Log.d(ctx, LOG_TAG, "CommentHistory not available...");
            view.setVisibility(View.GONE);
        }

        Log.d(ctx, LOG_TAG, "Done rendering row " + position);
        return row;
    }

    public void refill(List<TimeRegistration> timeRegistrations) {
        this.timeRegistrations.clear();
        this.timeRegistrations.addAll(timeRegistrations);
        notifyDataSetChanged();
    }
}
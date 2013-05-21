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

package eu.vranckaert.worktime.activities.timeregistrations.listadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.vranckaert.worktime.R;

import java.util.List;

/**
 * Date: 8/05/13
 * Time: 14:26
 *
 * @author Dirk Vranckaert
 */
public class SlideInMenuAdapter extends ArrayAdapter<SlideInMenuAdapter.SlideInMenuItem> {
    private Activity context;

    public SlideInMenuAdapter(Activity context, List<SlideInMenuItem> menuItems) {
        super(context, R.layout.activity_time_registration_list_slidein_menu_item, menuItems);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;

        if (convertView == null) {
            row = context.getLayoutInflater().inflate(R.layout.activity_time_registration_list_slidein_menu_item, parent, false);
        } else {
            row = convertView;
        }

        SlideInMenuItem listItem = getItem(position);

        ImageView image = (ImageView) row.findViewById(R.id.activity_time_registration_list_slidein_menu_item_image);
        TextView label = (TextView) row.findViewById(R.id.activity_time_registration_list_slidein_menu_item_label);

        if (listItem.getImageResId() == -1) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(listItem.getImageResId());
        }

        label.setText(listItem.getLabelResId());

        View imageParent = (View) image.getParent();
        imageParent.setId(listItem.getContainerId());

        return row;
    }

    public static class SlideInMenuItem {
        private Intent intent;
        private int labelResId;
        private int imageResId = -1;
        private int requestCode = -1;
        private int containerId;

        public SlideInMenuItem(Context context, Class toClazz, int labelResId, int imageResId, int containerId) {
            Intent intent = new Intent(context, toClazz);
            this.intent = intent;
            this.labelResId = labelResId;
            this.imageResId = imageResId;
            this.containerId = containerId;
        }

        public SlideInMenuItem(Context context, Class toClazz, int labelResId, int imageResId, int requestCode, int containerId) {
            Intent intent = new Intent(context, toClazz);
            this.intent = intent;
            this.labelResId = labelResId;
            this.imageResId = imageResId;
            this.requestCode = requestCode;
            this.containerId = containerId;
        }

        public Intent getIntent() {
            return intent;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public int getLabelResId() {
            return labelResId;
        }

        public void setLabelResId(int labelResId) {
            this.labelResId = labelResId;
        }

        public int getImageResId() {
            return imageResId;
        }

        public void setImageResId(int imageResId) {
            this.imageResId = imageResId;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getContainerId() {
            return containerId;
        }

        public void setContainerId(int containerId) {
            this.containerId = containerId;
        }
    }
}

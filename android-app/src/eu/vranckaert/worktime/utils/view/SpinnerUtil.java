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

package eu.vranckaert.worktime.utils.view;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 27/05/13
 * Time: 9:14
 */
public class SpinnerUtil<T extends Object> {
    private Spinner spinner;
    private List<T> listObjects;
    private List<String> listEntries;

    public SpinnerUtil(final Spinner spinner, final Activity activity, final List<T> listObjects, final List<String> listEntries, final int selectedIndex) {
        this.spinner = spinner;
        this.listObjects = listObjects;
        this.listEntries = listEntries;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, listEntries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (selectedIndex != -1) {
            spinner.setSelection(selectedIndex);
        } else if (listObjects.size() > 0) {
            spinner.setSelection(0);
        }

        spinner.post(new Runnable() {
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SpinnerUtil.this.onItemSelected(parent, view, listObjects.get(position), listEntries.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    public SpinnerUtil(final Spinner spinner, final Activity activity, final List<T> listObjects, final List<String> listEntries, final T selectedObject) {
        this(spinner, activity, listObjects, listEntries, selectedObject == null ? -1 : listObjects.indexOf(selectedObject));
    }

    public SpinnerUtil(final Spinner spinner, final Activity activity, final T[] listObjects, final List<String> listEntries, final T selectedObject) {
        this(spinner, activity, Arrays.asList(listObjects), listEntries, selectedObject);
    }

    public SpinnerUtil(final Spinner spinner, final Activity activity, final List<T> listObjects, final List<String> listEntries) {
        this(spinner, activity, listObjects, listEntries, null);
    }

    public T getSelectedItem() {
        return listObjects.get(spinner.getSelectedItemPosition());
    }

    public void onItemSelected(AdapterView<?> parent, View view, T object, String listEntry) {}

    public void setSelectedItem(T selectedItem) {
        setSelectedIndex(listObjects.indexOf(selectedItem));
    }

    public void setSelectedIndex(int selectedIndex) {
        spinner.setSelection(selectedIndex);
    }
}

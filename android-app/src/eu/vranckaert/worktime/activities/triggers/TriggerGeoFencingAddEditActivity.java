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

import android.annotation.SuppressLint;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.exceptions.worktime.trigger.geofence.DuplicateGeofenceNameException;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;
import eu.vranckaert.worktime.service.GeofenceService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.view.ProjectTaskSelectionUtil;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockFragmentActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.*;

/**
 * User: DIRK VRANCKAERT
 * Date: 17/05/13
 * Time: 8:25
 */
public class TriggerGeoFencingAddEditActivity extends RoboSherlockFragmentActivity {
    private static final String LOG_TAG = TriggerGeoFencingAddEditActivity.class.getSimpleName();

    @InjectView(R.id.trigger_geo_fencing_add_edit_name) private TextView name;
    @InjectView(R.id.trigger_geo_fencing_add_edit_radius) private SeekBar radius;
    @InjectView(R.id.trigger_geo_fencing_add_edit_expires) private CheckBox expires;
    @InjectView(R.id.trigger_geo_fencing_add_edit_expiration_date) private Button expirationDateButton;

    @InjectView(R.id.validation_error_container) private View validationErrorContainer;
    @InjectView(R.id.validation_error_text_view) private TextView validationErrorTextView;

    @Inject private GeofenceService geofenceService;

    @InjectExtra(value = Constants.Extras.GEOFENCE, optional = true) @Nullable private GeofenceTrigger geofence;

    private ProjectTaskSelectionUtil projectTaskSelectionUtil;
    private GoogleMap mGoogleMap = null;
    private LocationClient mLocationClient = null;
    private LatLng mCurrentLocation = null;
    private LatLng mSelectedLocation = null;
    private Date expirationDate = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_geo_fencing_add_edit);

        if (geofence != null && geofence.getId() != null) {
            setTitle(R.string.lbl_trigger_geo_fencing_add_edit_edit_title);
        } else {
            setTitle(R.string.lbl_trigger_geo_fencing_add_edit_title);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expires.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expirationDateButton.setVisibility(View.VISIBLE);
                } else {
                    expirationDateButton.setVisibility(View.GONE);
                }
            }
        });

        expirationDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                if (expirationDate != null) {
                    calendar.setTime(expirationDate);
                }
                if (expirationDate == null) {
                    calendar.add(Calendar.MONTH, 1);
                }
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TriggerGeoFencingAddEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, monthOfYear);
                                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                expirationDate = selectedDate.getTime();

                                expirationDateButton.setText(
                                        DateUtils.DateTimeConverter.convertDateToString(expirationDate, DateFormat.MEDIUM, TriggerGeoFencingAddEditActivity.this)
                                );
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                initMapWithDefaultMarkersAndCustomLocation();
            }
        });

        projectTaskSelectionUtil = ProjectTaskSelectionUtil.getInstance(this);

        if (savedInstanceState != null) {
            geofence = (GeofenceTrigger) savedInstanceState.getSerializable(Constants.Extras.GEOFENCE);
            mCurrentLocation = savedInstanceState.getParcelable(Constants.Extras.CURRENT_LOCATION);
            loadEditData();
        } else {
            setupMap();
        }
    }

    private void setupMap() {
        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Show the progress dialog to block the ui...
        final ProgressDialog loadingDialog = new ProgressDialog(TriggerGeoFencingAddEditActivity.this);
        loadingDialog.setMessage(getText(R.string.lbl_trigger_geo_fencing_add_edit_map_loading_location_message));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedLocation = latLng;
                initMapWithDefaultMarkersAndCustomLocation();
            }
        });

        // Create a location client...
        mLocationClient = new LocationClient(TriggerGeoFencingAddEditActivity.this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Create the location request...
                LocationRequest locationRequest = LocationRequest.create().setInterval(5000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationClient.requestLocationUpdates(locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        initMapWithDefaultMarkers();
                        mLocationClient.disconnect();

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 17.0f));
                        loadingDialog.dismiss();
                        loadEditData();
                    }
                });
            }
            @Override
            public void onDisconnected() {
                loadingDialog.dismiss();
                loadEditData();
            }
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                loadingDialog.dismiss();
                loadEditData();
            }
        });
        mLocationClient.connect();
    }

    private void loadEditData() {
        if (geofence != null) {
            name.setText(geofence.getName());
            radius.setProgress(((Double)geofence.getRadius()).intValue());
            if (geofence.getExpirationDate() != null) {
                expires.setChecked(true);
                expirationDateButton.setText(DateUtils.DateTimeConverter.convertDateToString(geofence.getExpirationDate(), DateFormat.MEDIUM, TriggerGeoFencingAddEditActivity.this));
                expirationDateButton.setVisibility(View.VISIBLE);
                expirationDate = geofence.getExpirationDate();
            } else {
                expires.setChecked(false);
                expirationDateButton.setVisibility(View.GONE);
                expirationDate = null;
            }
            projectTaskSelectionUtil.setSelectedTask(geofence.getTask());
            mSelectedLocation = new LatLng(geofence.getLatitude(), geofence.getLongitude());
            initMapWithDefaultMarkersAndCustomLocation();

            final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation") // We use the new method when supported
                    @SuppressLint("NewApi") // We check which build version we are using.
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                        if (mCurrentLocation != null) {
                            bounds.include(mCurrentLocation);
                        }
                        if (mSelectedLocation != null) {
                            bounds.include(mSelectedLocation);
                        }

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
                    }
                });
            }
        }
    }

    private void initMapWithDefaultMarkers() {
        mGoogleMap.clear();
        if (mCurrentLocation!=null) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .title(getString(R.string.lbl_trigger_geo_fencing_add_edit_map_current_location_title))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    private void initMapWithDefaultMarkersAndCustomLocation() {
        initMapWithDefaultMarkers();
        if (mSelectedLocation == null) {
            return;
        }

        double radius = new Double(this.radius.getProgress());

        mGoogleMap.addCircle(new CircleOptions()
                .center(mSelectedLocation)
                .radius(radius)
                .fillColor(0x55abd4f4)
                .strokeWidth(2f)
                .strokeColor(getResources().getColor(R.color.maps_radius_circle_stroke))
        );
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mSelectedLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        if (geofence != null && geofence.getId() != null) {
            menuInflater.inflate(R.menu.ab_activity_trigger_geo_fencing_add_edit_edit, menu);
        } else {
            menuInflater.inflate(R.menu.ab_activity_trigger_geo_fencing_add_edit, menu);
        }

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                IntentUtil.goBack(TriggerGeoFencingAddEditActivity.this);
                break;
            case R.id.menu_trigger_geo_fencing_list_activity_add_edit:
                save();
                break;
            case R.id.menu_trigger_geo_fencing_list_activity_add_edit_edit:
                update();
                break;
            case R.id.menu_trigger_geo_fencing_list_activity_add_edit_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        Dialog dialog = new AlertDialog.Builder(TriggerGeoFencingAddEditActivity.this)
                .setTitle(R.string.lbl_trigger_geo_fencing_delete_dialog_title)
                .setMessage(R.string.lbl_trigger_geo_fencing_delete_dialog_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        geofenceService.deleteGeofence(geofence);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void update() {
        if (!validateInput()) {
            return;
        }

        if (!expires.isChecked()) {
            expirationDate = null;
        }

        geofence.setName(name.getText().toString());
        geofence.setExpirationDate(expirationDate);
        geofence.setLatitude(mSelectedLocation.latitude);
        geofence.setLongitude(mSelectedLocation.longitude);
        geofence.setRadius(radius.getProgress());
        geofence.setTask(projectTaskSelectionUtil.getSelectedTask());

        try {
            geofenceService.updatGeofence(geofence);
        } catch (DuplicateGeofenceNameException e) {
            name.setError(getText(R.string.lbl_trigger_geo_fencing_add_edit_error_duplicate_names));
            return;
        }

        mLocationClient = new LocationClient(TriggerGeoFencingAddEditActivity.this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                mLocationClient.removeGeofences(Arrays.asList(new String[] {TriggerGeoFencingAddEditActivity.this.geofence.getGeofenceRequestId()}), new LocationClient.OnRemoveGeofencesResultListener() {
                    @Override
                    public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {
                        Geofence geofence = new Geofence.Builder().setRequestId(TriggerGeoFencingAddEditActivity.this.geofence.getGeofenceRequestId())
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                .setCircularRegion(mSelectedLocation.latitude, mSelectedLocation.longitude, radius.getProgress())
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .build();

                        List<Geofence> geofences = new ArrayList<Geofence>();
                        geofences.add(geofence);

                        Intent intent = new Intent(Constants.Broadcast.GEOFENCE_INTENT);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(TriggerGeoFencingAddEditActivity.this, TriggerGeoFencingAddEditActivity.this.geofence.getId(), intent, 0);

                        mLocationClient.addGeofences(geofences, pendingIntent, new LocationClient.OnAddGeofencesResultListener() {
                            @Override
                            public void onAddGeofencesResult(int i, String[] strings) {
                                IntentUtil.goBack(TriggerGeoFencingAddEditActivity.this);
                            }
                        });
                    }

                    @Override
                    public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {}
                });
            }
            @Override
            public void onDisconnected() {}
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Toast.makeText(TriggerGeoFencingAddEditActivity.this, R.string.lbl_trigger_geo_fencing_add_edit_error_updating_fence, Toast.LENGTH_LONG).show();
            }
        });
        mLocationClient.connect();
    }

    private void save() {
        if (!validateInput()) {
            return;
        }

        if (!expires.isChecked()) {
            expirationDate = null;
        }
        final GeofenceTrigger workTimeGeoFence = new GeofenceTrigger(
                name.getText().toString(),
                expirationDate,
                mSelectedLocation,
                radius.getProgress(),
                projectTaskSelectionUtil.getSelectedTask()
        );
        try {
            geofenceService.storeGeofence(workTimeGeoFence);
        } catch (DuplicateGeofenceNameException e) {
            name.setError(getText(R.string.lbl_trigger_geo_fencing_add_edit_error_duplicate_names));
            return;
        }

        mLocationClient = new LocationClient(TriggerGeoFencingAddEditActivity.this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                long expirationDuration = Geofence.NEVER_EXPIRE;
                if (expirationDate != null) {
                    expirationDuration = expirationDate.getTime() - new Date().getTime();
                }
                Geofence geofence = new Geofence.Builder().setRequestId(workTimeGeoFence.getGeofenceRequestId())
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setCircularRegion(mSelectedLocation.latitude, mSelectedLocation.longitude, radius.getProgress())
                        .setExpirationDuration(expirationDuration)
                        .build();

                List<Geofence> geofences = new ArrayList<Geofence>();
                geofences.add(geofence);

                Intent intent = new Intent(Constants.Broadcast.GEOFENCE_INTENT);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(TriggerGeoFencingAddEditActivity.this, workTimeGeoFence.getId(), intent, 0);

                mLocationClient.addGeofences(geofences, pendingIntent, new LocationClient.OnAddGeofencesResultListener() {
                    @Override
                    public void onAddGeofencesResult(int i, String[] strings) {
                        Log.d(LOG_TAG, "Geofence has been created!");
                        IntentUtil.goBack(TriggerGeoFencingAddEditActivity.this);
                    }
                });
            }
            @Override
            public void onDisconnected() {}
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                geofenceService.deleteGeofence(workTimeGeoFence);
                Toast.makeText(TriggerGeoFencingAddEditActivity.this, R.string.lbl_trigger_geo_fencing_add_edit_error_creating_fence, Toast.LENGTH_LONG).show();
            }
        });
        mLocationClient.connect();
    }

    private boolean validateInput() {
        // TODO make this validation better and easier...
        boolean valid = true;

        if (StringUtils.isBlank(name.getText().toString())) {
            name.setError(getText(R.string.lbl_trigger_geo_fencing_add_edit_error_required));
            valid = false;
        }
        if (expires.isChecked() && expirationDate == null) {
            expirationDateButton.setError(getText(R.string.lbl_trigger_geo_fencing_add_edit_error_required));
            valid = false;
        } else if (expires.isChecked()) {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(new Date());
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            if ( expirationDate.before(tomorrow.getTime())) {
                expirationDateButton.setError(getText(R.string.lbl_trigger_geo_fencing_add_edit_error_expiration_date_future));
                valid = false;
            }
        }
        if (projectTaskSelectionUtil.getSelectedTask() == null) {
            valid = false;
        }

        if (mSelectedLocation == null) {
            valid = false;
        }

        if (!valid) {
            validationErrorTextView.setText(R.string.lbl_trigger_geo_fencing_add_edit_error_general_message);
            validationErrorContainer.setVisibility(View.VISIBLE);
        } else {
            validationErrorContainer.setVisibility(View.GONE);
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (geofence == null) {
            geofence = new GeofenceTrigger();
        }
        if (!expires.isSelected()) {
            expirationDate = null;
        }
        geofence.setName(name.getText().toString());
        geofence.setExpirationDate(expirationDate);
        if (mSelectedLocation != null) {
            geofence.setLatitude(mSelectedLocation.latitude);
            geofence.setLongitude(mSelectedLocation.longitude);
        }
        geofence.setRadius(radius.getProgress());
        geofence.setTask(projectTaskSelectionUtil.getSelectedTask());
        outState.putSerializable(Constants.Extras.GEOFENCE, geofence);
        outState.putParcelable(Constants.Extras.CURRENT_LOCATION, mCurrentLocation);

        super.onSaveInstanceState(outState);
    }
}
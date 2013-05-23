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
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.preferences.TriggersPreferencesActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;
import eu.vranckaert.worktime.service.GeofenceService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockFragmentActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: DIRK VRANCKAERT
 * Date: 17/05/13
 * Time: 8:12
 */
public class TriggerGeoFencingMapActivity extends RoboSherlockFragmentActivity {
    @Inject private GeofenceService geofenceService;

    private Map<Marker, GeofenceTrigger> mapMarkers = new HashMap<Marker, GeofenceTrigger>();
    private GoogleMap mGoogleMap;
    private LocationClient mLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_geo_fencing_map);

        setTitle(R.string.lbl_trigger_geo_fencing_map_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_trigger_geo_fencing_map, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                IntentUtil.goBack(TriggerGeoFencingMapActivity.this);
                break;
            case R.id.menu_trigger_geo_fencing_map_settings:
                Intent preferenceIntent = new Intent(this, TriggersPreferencesActivity.class);
                startActivity(preferenceIntent);
                break;
            case R.id.menu_trigger_geo_fencing_map_activity_add:
                Intent intent = new Intent(TriggerGeoFencingMapActivity.this, TriggerGeoFencingAddEditActivity.class);
                startActivityForResult(intent, Constants.IntentRequestCodes.ADD_TRIGGER_GEO_FENCING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int playServicesCheckResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(TriggerGeoFencingMapActivity.this);
        if (playServicesCheckResult != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(playServicesCheckResult, TriggerGeoFencingMapActivity.this, Constants.IntentRequestCodes.INSTALL_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(TriggerGeoFencingMapActivity.this, R.string.lbl_trigger_geo_fencing_map_play_services_required, Toast.LENGTH_LONG).show();
                    finish();
                }
            }).show();
            return;
        }

        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

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

                        loadDataOnMap();
                    }
                });
            }

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    GeofenceTrigger geofenceTrigger = mapMarkers.get(marker);
                    Intent intent = new Intent(TriggerGeoFencingMapActivity.this, TriggerGeoFencingAddEditActivity.class);
                    intent.putExtra(Constants.Extras.GEOFENCE, geofenceTrigger);
                    startActivityForResult(intent, Constants.IntentRequestCodes.EDIT_TRIGGER_GEO_FENCING);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentRequestCodes.ADD_TRIGGER_GEO_FENCING || requestCode == Constants.IntentRequestCodes.EDIT_TRIGGER_GEO_FENCING) {
            loadDataOnMap();
        }
    }

    private void loadDataOnMap() {
        mGoogleMap.clear();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        List<GeofenceTrigger> geofenceTriggers = geofenceService.findAllNonExpired();
        for (GeofenceTrigger geofence : geofenceTriggers) {
            LatLng latLng = new LatLng(geofence.getLatitude(), geofence.getLongitude());
            bounds.include(latLng);

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(geofence.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mapMarkers.put(marker, geofence);
        }

        if (geofenceTriggers.size() > 0) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
        } else {
            // Create a location client...
            mLocationClient = new LocationClient(TriggerGeoFencingMapActivity.this, new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    // Create the location request...
                    LocationRequest locationRequest = LocationRequest.create().setInterval(5000).setPriority(LocationRequest.PRIORITY_NO_POWER);
                    mLocationClient.requestLocationUpdates(locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f));
                        }
                    });
                }
                @Override
                public void onDisconnected() {}
            }, new GooglePlayServicesClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {    }
            });
            mLocationClient.connect();
        }
    }
}
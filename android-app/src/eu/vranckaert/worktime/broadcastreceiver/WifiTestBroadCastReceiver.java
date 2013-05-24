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

package eu.vranckaert.worktime.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 24/05/13
 * Time: 9:24
 */
public class WifiTestBroadCastReceiver extends RoboBroadcastReceiver {
    @Override
    protected void handleReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
            String ssid = null;

            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
            }

            if (bssid != null && ssid == null) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> wifis = wifiManager.getScanResults();
                for (ScanResult scanResult : wifis) {
                    if (scanResult.BSSID.equals(bssid)) {
                        ssid = scanResult.SSID;
                        break;
                    }
                }
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(ssid != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getDetailedState().equals(NetworkInfo.DetailedState.CONNECTED)) {
                Log.d("WIFI-TEST-WORKTIME", "Connected to wifi: (" + bssid + ")" + ssid);
            } else if (ssid != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getDetailedState().equals(NetworkInfo.DetailedState.DISCONNECTED)) {
                Log.d("WIFI-TEST-WORKTIME", "Disconnected from wifi: (" + bssid + ")" + ssid);
            }
        }

//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        List<WifiConfiguration> wifis = wifiManager.getConfiguredNetworks();
//        for (WifiConfiguration wifi : wifis) {
//            // ...
//        }
//
//        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}

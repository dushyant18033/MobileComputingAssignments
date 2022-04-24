package com.mc2022.template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String previousState = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.main_fragment_container) == null) {
            fm.beginTransaction()
                    .add(R.id.main_fragment_container, new MainFragment())
                    .commit();
        }

//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context c, Intent intent) {
//                Log.d(TAG, "WiFi scan results available");
//
//                for (ScanResult result : wifiManager.getScanResults()) {
//                    Log.d(TAG, result.SSID);
//                }
//            }
//        };
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        registerReceiver(wifiScanReceiver, intentFilter);
//
//        boolean success = wifiManager.startScan();
//        if (!success) {
//            // scan failure handling
//            Log.d(TAG, "showing old scan results");
//            for (ScanResult result : wifiManager.getScanResults()) {
//                Log.d(TAG, result.SSID);
//            }
//        }

        stateLogAndToast("Created");
    }

    private void stateLogAndToast(String currentState)
    {
        String msg = "State of " + TAG + " changed";
        if (previousState.equals("")) {
            msg += " to " + currentState;
        }
        else {
            msg += " from " + previousState + " to " + currentState;
        }
        previousState = currentState;

        Log.i(TAG, msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stateLogAndToast("Started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLogAndToast("Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stateLogAndToast("Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stateLogAndToast("Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stateLogAndToast("Destroyed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stateLogAndToast("Restarted");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
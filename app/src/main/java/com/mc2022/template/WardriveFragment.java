package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;


public class WardriveFragment extends Fragment {

    private static final String TAG = "WarDriveFragment";

    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;

    private EditText editTextLocId;
    private Button btnAddLocation;

    private TextView textViewPredLocation;
    private Button btnPredLocation;

    public WardriveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Log.d(TAG, "WiFi scan results available");
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wardrive, container, false);

        editTextLocId = v.findViewById(R.id.editTextName);
        btnAddLocation = v.findViewById(R.id.btnAdd);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnAdd");

                boolean success = wifiManager.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure();
                }
            }
        });

        textViewPredLocation = v.findViewById(R.id.textViewPredLocation);
        btnPredLocation = v.findViewById(R.id.btnPredict);

        btnPredLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnPred");

                boolean success = wifiManager.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure();
                }
            }
        });

        return v;
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();

        for (ScanResult result : results) {
//            WifiInfo.Builder.setBssid(result.SSID).build()
            Log.d(TAG,result.BSSID + " " + result.level);
        }
        // ... use new scan results ...
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        // ... potentially use older scan results ...

        for (ScanResult result : results) {
            Log.d(TAG, result.SSID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(wifiScanReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);
    }
}
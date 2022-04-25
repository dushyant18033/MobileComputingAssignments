package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class WardriveFragment extends Fragment {

    private static final String TAG = "WarDriveFragment";
    private static final String FILE_NAME = "RSSI.txt";
    private static final int K = 5;

    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;

    private EditText editTextLocId;
    private Button btnAddLocation;

    private TextView textViewPredLocation;
    private Button btnScan;


    // State vars
    private List<AnnotatedEntry> annotations = new ArrayList<AnnotatedEntry>();
    private String newLocId;
    private boolean saveNewAnnotation;

    public WardriveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load previous data
        try {
            loadAnnotationsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "init older annotations");
        for (AnnotatedEntry entry:annotations)
        {
            Log.d(TAG, entry.getUserAnnotation());
        }



        // init wifi scanner
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false);
                if (success) {
                    updateCurrentLocation();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        // setup broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        // trial scan
        boolean success = wifiManager.startScan();
        if (!success) {
            scanFailure();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wardrive, container, false);

        editTextLocId = v.findViewById(R.id.editTextLocId);
        btnAddLocation = v.findViewById(R.id.btnAdd);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnAdd");

                newLocId = editTextLocId.getText().toString();
                updateInformation();
            }
        });

        textViewPredLocation = v.findViewById(R.id.textViewPredLocation);
        btnScan = v.findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnScan");

                boolean success = wifiManager.startScan();
                if (!success) {
                    scanFailure();
                }
                else {
                    scanSuccess();
                }
            }
        });

        return v;
    }

    private void scanSuccess() {
        Log.i(TAG, "scanning...");
        Toast.makeText(getContext(), "scanning...", Toast.LENGTH_SHORT).show();
    }

    private void scanFailure() {
        Log.i(TAG, "scan failure");
        Toast.makeText(getContext(), "scan failure", Toast.LENGTH_SHORT).show();
    }

    private void updateInformation() {
        List<ScanResult> results = wifiManager.getScanResults();

        for (ScanResult result : results) {
            Log.d(TAG, result.SSID);
        }

        HashMap<String, Integer> rssiInfo = new HashMap<String, Integer>();
        for (ScanResult result : results)
        {
            rssiInfo.put(result.BSSID, result.level);
        }

        // saving
        annotations.add(new AnnotatedEntry(newLocId, rssiInfo));

        Toast.makeText(getContext(), "Saved: " + newLocId, Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentLocation()
    {
        List<ScanResult> results = wifiManager.getScanResults();

        for (ScanResult result : results) {
            Log.d(TAG, result.SSID);
        }

        HashMap<String, Integer> rssiInfo = new HashMap<String, Integer>();
        for (ScanResult result : results)
        {
            rssiInfo.put(result.BSSID, result.level);
        }

        // predicting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(annotations, new Comparator<AnnotatedEntry>() {
                @Override
                public int compare(AnnotatedEntry t1, AnnotatedEntry t2) {
                    return (int) (t1.distRssi(rssiInfo) - t2.distRssi(rssiInfo));
                }
            });

            Log.i(TAG, "sorted annotations");

            // sorting
            for (AnnotatedEntry t : annotations) {
                Log.d(TAG, t.getUserAnnotation() + ": " + t.distRssi(rssiInfo));
            }

            // counting repetitions for mode based KNN
            HashMap<String, Integer> locId_count = new HashMap<String, Integer>();
            String best_predict = "";
            int best_count = 0;

            String all_predict = "";

            for (int i = 0; i < Math.min(annotations.size(), K); i++) {
                String locId = annotations.get(i).getUserAnnotation();
                int newCount = 1 + locId_count.getOrDefault(locId, 0);
                locId_count.put(locId, newCount);

                if (newCount > best_count) {
                    best_count = newCount;
                    best_predict = locId;
                }

                all_predict += locId + ";";
            }

            // set on UI
            if (annotations.size() > 0) {
                textViewPredLocation.setText(all_predict + "\r\n Voting-Based KNN --> " + best_predict);
            }
        }

        Toast.makeText(getContext(), "Scan completed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Scan completed");
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

    @Override
    public void onStop() {
        super.onStop();
        try {
            saveAnnotationsToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAnnotationsToFile() throws IOException
    {
        FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(annotations);
        os.close();
        fos.close();
    }

    public void loadAnnotationsFromFile() throws IOException, ClassNotFoundException
    {
        FileInputStream fis = getContext().openFileInput(FILE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);
        annotations = (List<AnnotatedEntry>) is.readObject();
        is.close();
        fis.close();
    }
}
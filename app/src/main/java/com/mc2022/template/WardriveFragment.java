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

import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WardriveFragment extends Fragment {

    private static final String TAG = "WarDriveFragment";
    private static final String FILE_NAME = "RSSI.txt";

    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;

    private EditText editTextLocId;
    private EditText editTextLocX;
    private EditText editTextLocY;
    private Button btnAddLocation;

    private TextView textViewPredLocation;
    private Button btnScan;


    // State vars
    private List<AnnotatedEntry> annotations = new ArrayList<AnnotatedEntry>();
    private String newLocId;
    private float newLocX;
    private float newLocY;
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


        Log.d(TAG, "init older annotations");
        for (AnnotatedEntry entry:annotations)
        {
            Log.d(TAG, entry.getUserAnnotation());
        }



        // init wifi scanner
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

        // setup broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        // trial scan
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

        editTextLocId = v.findViewById(R.id.editTextLocId);
        editTextLocX = v.findViewById(R.id.editTextLocX);
        editTextLocY = v.findViewById(R.id.editTextLocY);
        btnAddLocation = v.findViewById(R.id.btnAdd);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "btnAdd");

                saveNewAnnotation = true;
                newLocId = editTextLocId.getText().toString();
                newLocX = Float.parseFloat(editTextLocX.getText().toString());
                newLocY = Float.parseFloat(editTextLocY.getText().toString());

                boolean success = wifiManager.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure();
                }
            }
        });

        textViewPredLocation = v.findViewById(R.id.textViewPredLocation);
        btnScan = v.findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
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

        HashMap<String, Integer> rssiInfo = new HashMap<String, Integer>();
        for (ScanResult result : results)
        {
            rssiInfo.put(result.BSSID, result.level);
        }


        // TODO: save logic --> (more testing needed)
        if (saveNewAnnotation)
        {
            saveNewAnnotation = false;
            annotations.add(new AnnotatedEntry(newLocId, newLocX, newLocY, rssiInfo));
        }


        // TODO: predict logic --> (more testing needed, code remaining)
        for (AnnotatedEntry entry : annotations)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                float metric = entry.matchRssi(rssiInfo); // metric ka kya karein, kaise karein???
            }
        }

    }

    private void scanFailure() {
        Log.d(TAG, "old scan results available, but not using them for now!");

        List<ScanResult> results = wifiManager.getScanResults();

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
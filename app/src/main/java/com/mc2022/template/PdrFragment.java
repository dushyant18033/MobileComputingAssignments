package com.mc2022.template;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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


public class PdrFragment extends Fragment {

    private static final String TAG = "PDRFragment";
    private static final String FILE_NAME = "PDR.txt";
    private static final int K = 5;

    private SensorManager sensorManager;

    private Sensor mLinAccSensor;
    private Sensor mMagSensor;
    private Sensor mAccSensor;
    private Sensor mLightSensor;

    private SensorEventListener mLinAccListener;
    private SensorEventListener mMagListener;
    private SensorEventListener mAccListener;
    private SensorEventListener mLightListener;

    private float[] magValues;
    private float[] accValues;
    private float[] lightValues;
    private float[] orientation = new float[3];

    private int stepCtr = 0;
    private int step_state = 0;

    private float step_threshold = 3.5f;
    private float height = 67f;
    private float stride = 0.41f * height;

    private TextView textViewStepCount;
    private TextView textViewDirection;

    private EditText editTextHeight;
    private EditText editTextThresh;

    private TextView textViewStatus;
    private Button btnSet;

    private List<SensorFingerprint> fingerprints = new ArrayList<SensorFingerprint>();
    private TextView textViewPredLoc;
    private EditText editTextLocId;
    private Button btnAdd;
    private Button btnUpdate;

    private ArrayList<Entry> points = new ArrayList<Entry>();
    private ScatterChart chart;

    public PdrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load previous data
        try {
            loadFingerprintsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "init older fingerprint anchors");
        for (SensorFingerprint entry:fingerprints)
        {
            Log.d(TAG, entry.getUserAnnotation());
        }

        sensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
        points.add(new Entry(0,0));

        initializeSensors();
        registerSensors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pdr, container, false);

        textViewStepCount = v.findViewById(R.id.textViewStepCount);
        textViewDirection = v.findViewById(R.id.textViewDirection);

        editTextHeight = v.findViewById(R.id.editTextHeight);
        editTextThresh = v.findViewById(R.id.editTextThresh);

        textViewStatus = v.findViewById(R.id.textViewStatus);
        textViewStatus.setText(
                "Height: " + height + " inches \r\n" +
                "Stride: " + stride + " inches \r\n" +
                "Step Thresh: " + step_threshold
        );

        btnSet = v.findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String heightStr = editTextHeight.getText().toString();
                String stepThreshStr = editTextThresh.getText().toString();

                if (!heightStr.isEmpty())
                {
                    height = Float.parseFloat(heightStr);
                    stride = 0.41f * height;
                    editTextHeight.setText("");
                }

                if (!stepThreshStr.isEmpty())
                {
                    step_threshold = Float.parseFloat(stepThreshStr);
                    editTextThresh.setText("");
                }

                textViewStatus.setText(
                        "Height: " + height + " inches \r\n" +
                        "Stride: " + stride + " inches \r\n" +
                        "Step Thresh: " + step_threshold
                );
            }
        });

        textViewPredLoc = v.findViewById(R.id.textViewPredLoc);
        editTextLocId = v.findViewById(R.id.editTextLocId);

        btnAdd = v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnAdd");

                if(mAccSensor==null || mMagSensor==null || mLightSensor==null)
                {
                    Toast.makeText(getContext(), "Plz wait, sensors not ready!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String locId = editTextLocId.getText().toString();
                fingerprints.add(new SensorFingerprint(locId, accValues, magValues, lightValues));
                Toast.makeText(getContext(), "Saved: " + locId, Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate = v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(fingerprints, new Comparator<SensorFingerprint>() {
                        @Override
                        public int compare(SensorFingerprint t1, SensorFingerprint t2) {
                            return (int) (t1.distFingerprint(accValues, magValues, lightValues) - t2.distFingerprint(accValues, magValues, lightValues));
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "unsupported operation", Toast.LENGTH_SHORT).show();
                    return;
                }

                // counting repetitions for mode based KNN
                HashMap<String, Integer> locId_count = new HashMap<String, Integer>();
                String best_predict = "";
                int best_count = 0;

                String all_predict = "";

                for (int i = 0; i < Math.min(fingerprints.size(), K); i++) {
                    String locId = fingerprints.get(i).getUserAnnotation();
                    int newCount = 1 + locId_count.getOrDefault(locId, 0);
                    locId_count.put(locId, newCount);

                    if (newCount > best_count) {
                        best_count = newCount;
                        best_predict = locId;
                    }

                    all_predict += locId + ";";
                }

                // set on UI
                if (fingerprints.size() > 0) {
                    textViewPredLoc.setText(all_predict + "\r\n Voting-Based KNN --> " + best_predict);
                }

            }
        });

        chart = v.findViewById(R.id.chart);
        updatePathPlot();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSensors();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            saveFingerprintsToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updatePathPlot() {
        chart.getDescription().setEnabled(false);

        ScatterDataSet set1 = new ScatterDataSet(points, "travel path");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        set1.setScatterShapeSize(8f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        ScatterData data = new ScatterData(dataSets);
        chart.setData(data);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                chart.getXAxis().setTextColor(Color.WHITE);
                chart.getAxisRight().setTextColor(Color.WHITE);
                chart.getLegend().setTextColor(Color.WHITE);
                data.setValueTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                chart.getXAxis().setTextColor(Color.BLACK);
                chart.getAxisRight().setTextColor(Color.BLACK);
                chart.getLegend().setTextColor(Color.BLACK);
                data.setValueTextColor(Color.BLACK);
                break;
        }

        chart.invalidate();
    }

    void initializeSensors()
    {
        mLinAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mLinAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                double magnitude = Math.sqrt(x*x + y*y + z*z);
                Log.d(TAG, "linear acc magnitude: " + magnitude);

                // step count
                if (step_state == 0 && magnitude > step_threshold)
                    step_state = 1;

                if (step_state == 1 && magnitude < step_threshold)
                    step_state = 2;

                if (step_state == 2)
                {
                    stepCtr++;
                    step_state = 0;

                    textViewStepCount.setText("Steps: " + stepCtr);

                    // plot
                    double ang = -orientation[0];
                    Entry prev = points.get(points.size() - 1);

                    float x_new = (float) (stride * Math.cos(ang)) + prev.getX();
                    float y_new = (float) (stride * Math.sin(ang)) + prev.getY();
                    points.add(new Entry(x_new, y_new));

                    updatePathPlot();
                }

                // direction
                if (magValues!=null && accValues!=null)
                {
                    float[] rotMatrix = new float[9];

                    SensorManager.getRotationMatrix(rotMatrix, null, accValues, magValues);
                    SensorManager.getOrientation(rotMatrix, orientation);

                    double ang = (-orientation[0]*180/3.14159);
                    textViewDirection.setText("Direction: " + Math.round(100.0*ang)/100.0);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        mMagSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(TAG, "mag: " + sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2]);

                magValues = sensorEvent.values;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        mAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(TAG, "acc: " + sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2]);

                accValues = sensorEvent.values;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        mLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mLightListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(TAG, "light: " + sensorEvent.values[0]);

                lightValues = sensorEvent.values;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void registerSensors() {
        sensorManager.registerListener(mLinAccListener, mLinAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mMagListener, mMagSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mLightListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregisterSensors() {
        sensorManager.unregisterListener(mLinAccListener);
        sensorManager.unregisterListener(mMagListener);
        sensorManager.unregisterListener(mAccListener);
        sensorManager.unregisterListener(mLightListener);
    }

    public void saveFingerprintsToFile() throws IOException
    {
        FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(fingerprints);
        os.close();
        fos.close();
    }

    public void loadFingerprintsFromFile() throws IOException, ClassNotFoundException
    {
        FileInputStream fis = getContext().openFileInput(FILE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);
        fingerprints = (List<SensorFingerprint>) is.readObject();
        is.close();
        fis.close();
    }
}
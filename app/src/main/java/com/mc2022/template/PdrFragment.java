package com.mc2022.template;

import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class PdrFragment extends Fragment {

    private static final String TAG = "PDRFragment";

    private SensorManager sensorManager;

    private Sensor mLinAccSensor;
    private Sensor mMagSensor;
    private Sensor mAccSensor;

    private SensorEventListener mLinAccListener;
    private SensorEventListener mMagListener;
    private SensorEventListener mAccListener;

    private float[] magValues;
    private float[] accValues;
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

    private ArrayList<Entry> points = new ArrayList<Entry>();
    private ScatterChart chart;

    public PdrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    void registerSensors() {
        sensorManager.registerListener(mLinAccListener, mLinAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mMagListener, mMagSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregisterSensors() {
        sensorManager.unregisterListener(mLinAccListener);
        sensorManager.unregisterListener(mMagListener);
        sensorManager.unregisterListener(mAccListener);
    }
}
package com.mc2022.template;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;



public class PdrFragment extends Fragment {

    private static final String TAG = "PDRFragment";

    private SensorManager sensorManager;

    private Sensor mAccSensor;
    private Sensor mMagSensor;
    private SensorEventListener mAccListener;
    private SensorEventListener mMagListener;

    private int stepCtr = 0;
    private int step_state = 0;

    private TextView textViewStepCount;
    private TextView textViewDirection;

    public PdrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
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

    void initializeSensors()
    {
        mAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                double magnitude = Math.sqrt(x*x + y*y + z*z);
                Log.d(TAG, "acc magnitude: " + magnitude);

                if (step_state == 0 && magnitude > 5)
                    step_state = 1;

                if (step_state == 1 && magnitude < 5)
                    step_state = 2;

                if (step_state == 2)
                {
                    stepCtr++;
                    step_state = 0;

                    textViewStepCount.setText("Steps: " + stepCtr);
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
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void registerSensors() {
        sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mMagListener, mMagSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregisterSensors() {
        sensorManager.unregisterListener(mAccListener);
        sensorManager.unregisterListener(mMagListener);
    }
}
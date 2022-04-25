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

                // direction
                if (magValues!=null && accValues!=null)
                {
                    float[] rotMatrix = new float[9];

                    SensorManager.getRotationMatrix(rotMatrix, null, accValues, magValues);
                    SensorManager.getOrientation(rotMatrix, orientation);

                    textViewDirection.setText("Direction: " + (-orientation[0]*180/3.14159265));
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
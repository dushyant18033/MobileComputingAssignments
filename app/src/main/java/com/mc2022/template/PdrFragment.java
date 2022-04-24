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

import androidx.fragment.app.Fragment;



public class PdrFragment extends Fragment {

    private static final String TAG = "PDRFragment";

    private SensorManager sensorManager;

    private Sensor mAccSensor;
    private SensorEventListener mAccListener;

    public PdrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
        initializeAcc();
        registerSensors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pdr, container, false);

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

    void initializeAcc()
    {
        mAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.i(TAG, "acc: " + sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void registerSensors() {
        sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(mGyroListener, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void unregisterSensors() {
        sensorManager.unregisterListener(mAccListener);
//        sensorManager.unregisterListener(mGyroListener);
    }
}
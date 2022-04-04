package com.mc2022.template;

import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.mc2022.template.SensorDataModels.AccelData;
import com.mc2022.template.SensorDataModels.AppDatabase;
import com.mc2022.template.SensorDataModels.GyroData;
import com.mc2022.template.SensorDataModels.LightData;
import com.mc2022.template.SensorDataModels.OrientationData;
import com.mc2022.template.SensorDataModels.ProximityData;
import com.mc2022.template.SensorDataModels.TempData;


public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private SensorManager sensorManager;
    private SensorDataDao dao;

    private Sensor mAccSensor;
    private Sensor mGyroSensor;
    private Sensor mTempSensor;
    private Sensor mLightSensor;
    private Sensor mProximitySensor;
    private Sensor mOrientationSensor;

    private SensorEventListener mAccListener;
    private SensorEventListener mGyroListener;
    private SensorEventListener mTempListener;
    private SensorEventListener mLightListener;
    private SensorEventListener mProximityListener;
    private SensorEventListener mOrientationListener;

    // state vars
    private boolean accState = false;
    private boolean gyroState = false;
    private boolean tempState = false;
    private boolean lightState = false;
    private boolean proximityState = false;
    private boolean orientationState = false;

    // ui widgets
    private ToggleButton accButton;
    private ToggleButton gyroButton;
    private ToggleButton tempButton;
    private ToggleButton lightButton;
    private ToggleButton proximityButton;
    private ToggleButton orientationButton;

    private TextView textViewAcc;

    private Button gpsButton;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        initializeAcc();
        initializeGyro();
        initializeTemp();
        initializeLight();
        initializeProximity();
        initializeOrientation();

        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "sensor-data-db")
                .allowMainThreadQueries()
                .build();

        dao = db.sensorDataDao();

        if (savedInstanceState != null)
        {
            accState = savedInstanceState.getBoolean("accState");
            gyroState = savedInstanceState.getBoolean("gyroState");
            tempState = savedInstanceState.getBoolean("tempState");
            lightState = savedInstanceState.getBoolean("lightState");
            proximityState = savedInstanceState.getBoolean("proximityState");
            orientationState = savedInstanceState.getBoolean("orientationState");

            registerSensors();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        initializeToggleButtons(v);
        restoreToggleUiState(); // though happens automatically

        textViewAcc = v.findViewById(R.id.textViewAcc);

        gpsButton = v.findViewById(R.id.buttonGps);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new LocationFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSensors();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("accState", accState);
        outState.putBoolean("gyroState", gyroState);
        outState.putBoolean("tempState", tempState);
        outState.putBoolean("lightState", lightState);
        outState.putBoolean("proximityState", proximityState);
        outState.putBoolean("orientationState", orientationState);
    }

    void registerSensors() {
        if (accState) {
            sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroState) {
            sensorManager.registerListener(mGyroListener, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (tempState) {
            sensorManager.registerListener(mTempListener, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (lightState) {
            sensorManager.registerListener(mLightListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (proximityState) {
            sensorManager.registerListener(mProximityListener, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (orientationState) {
            sensorManager.registerListener(mOrientationListener, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    void unregisterSensors() {
        sensorManager.unregisterListener(mAccListener);
        sensorManager.unregisterListener(mGyroListener);
        sensorManager.unregisterListener(mTempListener);
        sensorManager.unregisterListener(mLightListener);
        sensorManager.unregisterListener(mProximityListener);
        sensorManager.unregisterListener(mOrientationListener);
    }

    void restoreToggleUiState()
    {
        accButton.setChecked(accState);
        gyroButton.setChecked(gyroState);
        tempButton.setChecked(tempState);
        lightButton.setChecked(lightState);
        proximityButton.setChecked(proximityState);
        orientationButton.setChecked(orientationState);
    }

    void initializeToggleButtons(View v)
    {
        accButton = v.findViewById(R.id.toggleAcc);
        accButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accState = b;
                if (b)
                {
                    startAccel();
                }
                else
                {
                    stopAccel();
                }
            }
        });

        gyroButton = v.findViewById(R.id.toggleGyro);
        gyroButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                gyroState = b;
                if (b)
                {
                    startGyro();
                }
                else
                {
                    stopGyro();
                }
            }
        });

        tempButton = v.findViewById(R.id.toggleTemp);
        tempButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tempState = b;
                if (b)
                {
                    startTemp();
                }
                else
                {
                    stopTemp();
                }
            }
        });

        lightButton = v.findViewById(R.id.toggleLight);
        lightButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                lightState = b;
                if (b)
                {
                    startLight();
                }
                else
                {
                    stopLight();
                }
            }
        });

        proximityButton = v.findViewById(R.id.toggleProximity);
        proximityButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                proximityState = b;
                if (b)
                {
                    startProximity();
                }
                else
                {
                    stopProximity();
                }
            }
        });

        orientationButton = v.findViewById(R.id.toggleOrientation);
        orientationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orientationState = b;
                if (b)
                {
                    startOrientation();
                }
                else
                {
                    stopOrientation();
                }
            }
        });
    }

    void initializeAcc()
    {
        mAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mAccListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertAcc(new AccelData(sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));

                float avg_acc = (sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2])/3;
                textViewAcc.setText("Avg Acc = " + avg_acc + "\r\nDevice " + ((Math.abs(avg_acc)>=0.2)?"Not Stationary":"Stationary"));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "acc: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void initializeGyro()
    {
        mGyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertGyro(new GyroData(sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "gyro: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void initializeTemp()
    {
        mTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mTempListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertTemp(new TempData(sensorEvent.timestamp, sensorEvent.values[0]));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "temp: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void initializeLight()
    {
        mLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mLightListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertLight(new LightData(sensorEvent.timestamp, sensorEvent.values[0]));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "light: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void initializeProximity()
    {
        mProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mProximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertProximity(new ProximityData(sensorEvent.timestamp, sensorEvent.values[0]));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "proximity: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void initializeOrientation()
    {
        mOrientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mOrientationListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                dao.insertOrientation(new OrientationData(sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));

                Log.i(TAG, "# of values:"+sensorEvent.values.length);
                for(float value : sensorEvent.values)
                {
                    Log.i(TAG, "orientation: " + String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    void startAccel()
    {
        sensorManager.registerListener(mAccListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "accel active", Toast.LENGTH_SHORT).show();
    }

    void stopAccel()
    {
        sensorManager.unregisterListener(mAccListener);
        Toast.makeText(getContext(), "accel inactive", Toast.LENGTH_SHORT).show();
    }

    void startGyro()
    {
        sensorManager.registerListener(mGyroListener, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "gyro active", Toast.LENGTH_SHORT).show();
    }

    void stopGyro()
    {
        sensorManager.unregisterListener(mGyroListener);
        Toast.makeText(getContext(), "gyro inactive", Toast.LENGTH_SHORT).show();
    }

    void startTemp()
    {
        sensorManager.registerListener(mTempListener, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "temp active", Toast.LENGTH_SHORT).show();
    }

    void stopTemp()
    {
        sensorManager.unregisterListener(mTempListener);
        Toast.makeText(getContext(), "temp inactive", Toast.LENGTH_SHORT).show();
    }

    void startLight()
    {
        sensorManager.registerListener(mLightListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "light active", Toast.LENGTH_SHORT).show();
    }

    void stopLight()
    {
        sensorManager.unregisterListener(mLightListener);
        Toast.makeText(getContext(), "light inactive", Toast.LENGTH_SHORT).show();
    }

    void startProximity()
    {
        sensorManager.registerListener(mProximityListener, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "proximity active", Toast.LENGTH_SHORT).show();
    }

    void stopProximity()
    {
        sensorManager.unregisterListener(mProximityListener);
        Toast.makeText(getContext(), "proximity inactive", Toast.LENGTH_SHORT).show();
    }

    void startOrientation()
    {
        sensorManager.registerListener(mOrientationListener, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(getContext(), "orientation active", Toast.LENGTH_SHORT).show();
    }

    void stopOrientation()
    {
        sensorManager.unregisterListener(mOrientationListener);
        Toast.makeText(getContext(), "orientation inactive", Toast.LENGTH_SHORT).show();
    }
}
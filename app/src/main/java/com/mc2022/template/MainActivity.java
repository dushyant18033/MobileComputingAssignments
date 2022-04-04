package com.mc2022.template;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import com.mc2022.template.SensorDataModels.AccelData;
import com.mc2022.template.SensorDataModels.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.main_fragment_container);

        if (frag == null) {
            Log.i(TAG, "buttons fragment is null");
            frag = new MainFragment();
            fm.beginTransaction()
                    .add(R.id.main_fragment_container, frag)
                    .commit();
        }

//        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        for(Sensor sensor : deviceSensors)
//        {
//            Log.i(TAG, sensor.getName() + " " + sensor.getStringType());
//        }


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "sensor-data-db")
                .allowMainThreadQueries()
                .build();

        SensorDataDao dao = db.sensorDataDao();
        Log.i(TAG, "accel");
        for(AccelData a : dao.getAcc())
        {
            Log.i(TAG, a.toString());
        }

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
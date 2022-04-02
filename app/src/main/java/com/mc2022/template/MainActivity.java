package com.mc2022.template;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
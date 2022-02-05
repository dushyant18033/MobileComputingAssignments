package com.mc2022.template;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    // widgets
    private TextView textViewInfo;
    private Button buttonCheck;
    private TextView textViewStatus;

    // state variables
    private CovidUser user;
    private boolean buttonPressed = false;

    // other helper variables
    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        /* CONTROLLER logic */

        textViewInfo = (TextView) findViewById(R.id.textViewInfoFilled);
        buttonCheck = (Button) findViewById(R.id.buttonCheckStatus);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        // handling activity restart - restoring the state
        if (savedInstanceState!=null) {
            user = (CovidUser) savedInstanceState.getSerializable("user");
            buttonPressed = savedInstanceState.getBoolean("buttonPressed");

            if (buttonPressed) {
                textViewStatus.setText(user.isTestNeeded() ? R.string.rtpcr_yes : R.string.rtpcr_no);
            }
        }
        else {
            Intent intent = getIntent();
            user = (CovidUser) intent.getSerializableExtra("user");
        }

        // displaying entered form info
        textViewInfo.setText(user.toString());

        // button to check whether test is needed
        buttonCheck.setOnClickListener(view -> {
            Log.i(TAG, "check: onclick");
            Toast.makeText(SecondActivity.this, "check: onclick", Toast.LENGTH_SHORT).show();
            buttonPressed = true;
            textViewStatus.setText( user.isTestNeeded()? R.string.rtpcr_yes:R.string.rtpcr_no);
        });

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
        Toast.makeText(SecondActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stateLogAndToast("Started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLogAndToast("Resume");
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
        // saving state variables
        savedInstanceState.putSerializable("user", user);
        savedInstanceState.putBoolean("buttonPressed", buttonPressed);
        Log.i(TAG, "onSaveInstanceState");
        Toast.makeText(SecondActivity.this, TAG + ", onSaveInstanceState", Toast.LENGTH_SHORT).show();
    }
}
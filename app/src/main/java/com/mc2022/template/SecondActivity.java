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
            buttonPressed = true;
            textViewStatus.setText( user.isTestNeeded()? R.string.rtpcr_yes:R.string.rtpcr_no);
        });

        Log.i(TAG, "state: onCreate");
        Toast.makeText(SecondActivity.this, TAG + ", state: onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "state: onStart");
        Toast.makeText(SecondActivity.this, TAG + ", state: onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "state: onResume");
        Toast.makeText(SecondActivity.this, TAG + ", state: onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "state: onPause");
        Toast.makeText(SecondActivity.this, TAG + ", state: onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "state: onStop");
        Toast.makeText(SecondActivity.this, TAG + ", state: onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "state: onDestroy");
        Toast.makeText(SecondActivity.this, TAG + ", state: onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "state: onRestart");
        Toast.makeText(SecondActivity.this, TAG + ", state: onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // saving state variables
        savedInstanceState.putSerializable("user", user);
        savedInstanceState.putBoolean("buttonPressed", buttonPressed);
        Log.i(TAG, "state: onSaveInstanceState");
        Toast.makeText(SecondActivity.this, TAG + ", state: onSaveInstanceState", Toast.LENGTH_SHORT).show();
    }
}
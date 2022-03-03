package com.mc2022.template;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.buttons_fragment_container);

        if (frag == null) {
            Log.i(TAG, "buttons fragment is null");
            frag = new ButtonsFragment();
            fm.beginTransaction()
                    .add(R.id.buttons_fragment_container, frag)
                    .commit();
        }

        Fragment fragment = fm.findFragmentById(R.id.mainFragmentContainer);

        if (fragment == null) {
            Log.i(TAG, "main fragment is null");
            fragment = new MainFragment();
            fm.beginTransaction()
                    .add(R.id.mainFragmentContainer, fragment)
                    .commit();
        }

        stateLogs("onCreate");
    }

    private void stateLogs(String currentState)
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
        stateLogs("onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stateLogs("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stateLogs("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stateLogs("onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stateLogs("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLogs("onResume");
    }
}
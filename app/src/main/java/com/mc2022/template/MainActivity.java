package com.mc2022.template;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
    }
}
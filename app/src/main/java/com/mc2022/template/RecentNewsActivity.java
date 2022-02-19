package com.mc2022.template;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RecentNewsActivity extends AppCompatActivity {

    private static final String TAG = "RecentNewsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_news);

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.recentNewsContainer);

        if (frag == null) {
            Log.i(TAG, "buttons fragment is  null");
            frag = new RecentNewsFragment();
            fm.beginTransaction()
                    .add(R.id.recentNewsContainer, frag)
                    .commit();
        }
    }
}
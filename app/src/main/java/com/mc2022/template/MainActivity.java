package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

        broadcastReceivers();

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.buttons_fragment_container);

        if (frag == null) {
            Log.i(TAG, "buttons fragment is  null");
            frag = new ButtonsFragment();
            fm.beginTransaction()
                    .add(R.id.buttons_fragment_container, frag)
                    .commit();
        }

        frag = fm.findFragmentById(R.id.news_fragment_container);

        if (frag == null) {
            Log.i(TAG, "news fragment is  null");
            frag = new NewsFragment();
            fm.beginTransaction()
                    .add(R.id.news_fragment_container, frag)
                    .commit();
        }

        Fragment finalFrag = frag;
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, intent.getStringExtra("title"));
                Log.i(TAG, intent.getStringExtra("body"));
                Log.i(TAG, intent.getStringExtra("image"));
                ((NewsFragment) finalFrag).setContent(intent.getStringExtra("title"), intent.getStringExtra("body"), intent.getStringExtra("image"));
            }
        }, new IntentFilter("NewsPublished"));
    }

    private void broadcastReceivers()
    {
        IntentFilter batteryLow = new IntentFilter();
        batteryLow.addAction("BATTERY_LOW");
        registerReceiver(new BatteryLowReceiver(), batteryLow);

        IntentFilter powerConnect = new IntentFilter();
        batteryLow.addAction("POWER_CONNECTED");
        registerReceiver(new PowerConnectReceiver(), powerConnect);
    }
}
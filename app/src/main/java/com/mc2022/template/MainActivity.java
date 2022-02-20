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

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NewsFragment newsFragment;

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

        newsFragment = (NewsFragment) frag;
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.i(TAG, intent.getStringExtra("title"));
//                Log.i(TAG, intent.getStringExtra("body"));
//                Log.i(TAG, intent.getStringExtra("image"));
//                ((NewsFragment) finalFrag).setContent(intent.getStringExtra("title"), intent.getStringExtra("body"), intent.getStringExtra("image"));
                refreshLatestNews();
            }
        }, new IntentFilter("NewsPublished"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshLatestNews();
    }

    private void refreshLatestNews()
    {
        String[] files = fileList();
        int i=files.length - 1;
        while(i>=0)
        {
            if (files[i].startsWith("news"))
                break;

            i--;
        }
        if(i<0)
            return;

        try {
            News news = News.readFromFile(files[i], getApplicationContext());
            newsFragment.setContent(news.getTitle(), news.getBody(), news.getImageUrl());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
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
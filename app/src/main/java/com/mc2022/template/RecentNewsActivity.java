package com.mc2022.template;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONException;

import java.io.IOException;

public class RecentNewsActivity extends AppCompatActivity {

    private static final String TAG = "RecentNewsActivity";

    private NewsFragment newsFragment;

    private int idx = 0;
    private int MAX_NEWS = 5;
    private String[] files;

    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_news);

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.newsFragmentContainerView);

        if (frag == null) {
            Log.i(TAG, "buttons fragment is  null");
            frag = new NewsFragment();
            fm.beginTransaction()
                    .add(R.id.newsFragmentContainerView, frag)
                    .commit();
        }
        newsFragment = (NewsFragment) frag;

        if (savedInstanceState == null)
        {
            initialize();
        }
        else
        {
            idx = savedInstanceState.getInt("idx");
            MAX_NEWS = savedInstanceState.getInt("MAX_NEWS");
            files = savedInstanceState.getStringArray("files");
        }

        findViewById(R.id.buttonPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idx > 0)
                {
                    idx--;
                    updateNewsContainer();
                }
            }
        });

        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idx < MAX_NEWS-1)
                {
                    idx++;
                    updateNewsContainer();
                }
            }
        });

        stateLogAndToast("Created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (idx < MAX_NEWS)
            updateNewsContainer();

        stateLogAndToast("Started");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("idx", idx);
        outState.putInt("MAX_NEWS", MAX_NEWS);
        outState.putStringArray("files", files);
    }

    private void initialize()
    {
        String[] temp = fileList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, String.join(", ", temp));
        }

        int len = temp.length;

        files = new String[MAX_NEWS];
        int i=0, j=len-1;
        while( i<MAX_NEWS && j>=0 ) {
            String name = temp[j--];
            if (name.startsWith("news"))
                files[i++] = name;
        }
        MAX_NEWS = i;

        Log.d(TAG, "vars: " + MAX_NEWS + "," + idx);
    }

    private void updateNewsContainer()
    {
        try {
            News news = News.readFromFile(files[idx], getApplicationContext());
            newsFragment.setContent(news.getTitle(), news.getBody(), news.getImageUrl());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
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

}
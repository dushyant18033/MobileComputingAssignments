package com.mc2022.template;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

        initialize();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (idx < MAX_NEWS)
            updateNewsContainer();
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
}
package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.i(TAG, String.join(", ", getActivity().fileList()));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());

        ArrayList<News> newsList = null;
        try {
            newsList = loadNewsList(getContext());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (adapter == null) {
            adapter = new NewsAdapter(newsList);
        } else {
            adapter.notifyDataSetChanged();
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<News> finalNewsList = newsList;
        getActivity().registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    finalNewsList.add(News.readFromFile(intent.getStringExtra("filename"), context));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                Log.i(TAG, "news published .... " + intent.getStringExtra("filename"));
            }
        }, new IntentFilter("NewsPublished"));

        return v;
    }

    private ArrayList<News> loadNewsList(Context context) throws JSONException, IOException {
        ArrayList<News> newsList = new ArrayList<News>();
        String[] files = context.fileList();

        for(int i=0; i<files.length; i++) {
            if (files[i].startsWith("news")) {
                newsList.add(News.readFromFile(files[i], context));
            }
        }

        return newsList;
    }
}
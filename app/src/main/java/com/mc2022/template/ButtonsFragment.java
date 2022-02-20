package com.mc2022.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonsFragment extends Fragment {

    private static final String TAG = "ButtonsFragment";

    public ButtonsFragment() {
        // Required empty public constructor
    }

    public static ButtonsFragment newInstance(String param1, String param2) {
        ButtonsFragment fragment = new ButtonsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_buttons, container, false);

        v.findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG,"startService");
                Intent intent = new Intent(getActivity(), NewsService.class);
                intent.putExtra("start_index", countValidFiles(getContext()));
                getActivity().startService(intent);
            }
        });

        v.findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG,"stopService");
                getActivity().stopService(new Intent(getActivity(), NewsService.class));
            }
        });

        v.findViewById(R.id.buttonRecentNews).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG,"Show Recent News");
                getActivity().startActivity(new Intent(getActivity(), RecentNewsActivity.class));
            }
        });

        return v;
    }

    private int countValidFiles(Context context)
    {
        String[] files = context.fileList();

        int ctr = 0;
        for (int i=0; i<files.length; i++)
        {
            if (files[i].startsWith("news"))
            {
                ctr++;
            }
        }

        return ctr;
    }
}
package com.mc2022.template;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class ButtonsFragment extends Fragment {

    private static final String TAG = "ButtonsFragment";

    private String previousState = "";

    public ButtonsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateLogs("onCreate");
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
                getActivity().startService(new Intent(getActivity(), NewsService.class));
            }
        });

        v.findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG,"stopService");
                getActivity().stopService(new Intent(getActivity(), NewsService.class));
            }
        });

        stateLogs("onCreateView");

        return v;
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
    public void onPause() {
        super.onPause();
        stateLogs("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        stateLogs("onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        stateLogs("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        stateLogs("onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stateLogs("onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stateLogs("onDestroyView");
    }
}
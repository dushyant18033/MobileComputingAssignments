package com.mc2022.template;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;



public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        v.findViewById(R.id.btnPdr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction()
                    .replace(R.id.main_fragment_container, new PdrFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        v.findViewById(R.id.btnRssiWardrive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction()
                        .replace(R.id.main_fragment_container, new WardriveFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }
}
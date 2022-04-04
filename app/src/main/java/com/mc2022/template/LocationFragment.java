package com.mc2022.template;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.mc2022.template.SensorDataModels.AppDatabase;
import com.mc2022.template.SensorDataModels.GpsData;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;


public class LocationFragment extends Fragment {

    private static final String TAG = "LocationFragment";

    private LocationManager locationManager;
    private LocationListener mLocationListener;

    private SensorDataDao dao;

    // state vars
    private double mLatitude;
    private double mLongitude;
    private String viewMyPlacesString;

    // ui
    private EditText editName;
    private EditText editAddress;
    private TextView viewLatitude;
    private TextView viewLongitude;

    private Button btnAddPlace;
    private Button btnMyPlaces;

    private TextView viewMyPlaces;


    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (savedInstanceState != null)
        {
            mLatitude = savedInstanceState.getDouble("latitude");
            mLongitude = savedInstanceState.getDouble("longitude");
            viewMyPlacesString = savedInstanceState.getString("viewMyPlaces");
        }
        else
        {
            initGps();
        }

        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "sensor-data-db")
                .allowMainThreadQueries()
                .build();

        dao = db.sensorDataDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        editName = v.findViewById(R.id.editName);
        editAddress = v.findViewById(R.id.editAddress);

        viewLatitude = v.findViewById(R.id.editLatitude);
        viewLatitude.setText("Latitude:"+mLatitude);

        viewLongitude = v.findViewById(R.id.editLongitude);
        viewLongitude.setText("Longitude:"+mLongitude);

        viewMyPlaces = v.findViewById(R.id.textViewMyPlaces);
        viewMyPlaces.setText(viewMyPlacesString);

        btnAddPlace = v.findViewById(R.id.btnAddPlace);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dao.insertGps(new GpsData(editName.getText().toString(), mLatitude, mLongitude, editAddress.getText().toString()));
//                for(int i=0; i<20; i++)
//                    dao.insertGps(new GpsData(editName.getText().toString(), 10*i, 10*i, editAddress.getText().toString()));
                Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnMyPlaces = v.findViewById(R.id.btnMyPlaces);
        btnMyPlaces.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    List<GpsData> gpsData = dao.getAllGps();
                    gpsData.sort(new Comparator<GpsData>(){

                        @Override
                        public int compare(GpsData t1, GpsData t2) {

                            float[] dist1 = {-1};
                            Location.distanceBetween(t1.latitude, t1.longitude, mLatitude, mLongitude, dist1);

                            float[] dist2 = {-1};
                            Location.distanceBetween(t2.latitude, t2.longitude, mLatitude, mLongitude, dist2);

                            return (int) (10*(dist1[0] - dist2[0]));
                        }
                    });

                    viewMyPlacesString = "";

                    for (int i=0; i<Math.min(3, gpsData.size()); i++)
                        viewMyPlacesString += gpsData.get(i).toString() + "\r\n";

                    viewMyPlaces.setText(viewMyPlacesString);
                }
                else {
                    Log.d(TAG, "ufff..... uh oh !!!");
                    viewMyPlacesString = "";
                    for (GpsData gpsData : dao.getGpsData(mLatitude, mLongitude))
                    {
    //                    double dist = Math.abs(gpsData.latitude - mLatitude) + Math.abs(gpsData.longitude - mLongitude);
                        viewMyPlacesString += gpsData.toString() + "\r\n";
                    }
                    viewMyPlaces.setText(viewMyPlacesString);
                }
            }
        });

        return v;
    }

    void initGps()
    {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();

                Geocoder geocoder = new Geocoder(getContext());
                try {
                    String addr = geocoder.getFromLocation(mLatitude, mLongitude, 1).get(0).getAddressLine(0);
                    editAddress.setText(addr);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, location.getLatitude() + " " + location.getLongitude());

                if (viewLatitude!=null)
                    viewLatitude.setText("Latitude: "+mLatitude);

                if (viewLongitude!=null)
                    viewLongitude.setText("Longitude: "+mLongitude);
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, mLocationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, mLocationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("latitude", mLatitude);
        outState.putDouble("longitude", mLongitude);
        outState.putString("viewMyPlaces", viewMyPlacesString);
    }
}
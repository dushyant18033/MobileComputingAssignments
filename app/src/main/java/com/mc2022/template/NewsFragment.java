package com.mc2022.template;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_IMAGE = "image";

    // TODO: Rename and change types of parameters
    private String mTitle = "Empty";
    private String mBody = "";
    private String mImage = "";

    private TextView textViewTitle;
    private TextView textViewBody;
    private ImageView imageView;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2, String param3) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, param1);
        args.putString(ARG_BODY, param2);
        args.putString(ARG_IMAGE, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mBody = getArguments().getString(ARG_BODY);
            mImage = getArguments().getString(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        textViewTitle = v.findViewById(R.id.textViewTitle);
        textViewTitle.setText(mTitle);

        textViewBody = v.findViewById(R.id.textViewBody);
        textViewBody.setText(mBody);

        imageView = v.findViewById(R.id.imageView);

        return v;
    }

    public void setContent(String title, String body, String image) {
        mTitle = title;
        mBody = body;
        mImage = image;

        textViewTitle.setText(mTitle);
        textViewBody.setText(mBody);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        new ImageLoadTask(mImage, imageView).execute();
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String uri;
        private ImageView imageView;

        public ImageLoadTask(String uri, ImageView imageView) {
            this.uri = uri;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream input = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
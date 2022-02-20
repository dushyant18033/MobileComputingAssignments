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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(ARG_TITLE);
            mBody = savedInstanceState.getString(ARG_BODY);
            mImage = savedInstanceState.getString(ARG_IMAGE);
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
        new ImageDownloadTask(mImage, imageView).execute();

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TITLE, mTitle);
        outState.putString(ARG_BODY, mBody);
        outState.putString(ARG_IMAGE, mImage);
    }

    public void setContent(String title, String body, String image) {
        mTitle = title;
        mBody = body;
        mImage = image;

        textViewTitle.setText(mTitle);
        textViewBody.setText(mBody);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        new ImageDownloadTask(mImage, imageView).execute();
    }

    private class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

        private String uri;
        private ImageView imageView;

        public ImageDownloadTask(String uri, ImageView imageView) {
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
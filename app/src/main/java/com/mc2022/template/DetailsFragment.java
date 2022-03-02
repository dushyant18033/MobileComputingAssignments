package com.mc2022.template;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_COMMENT = "comment";
    private static final String ARG_RATING = "rating";

    private String mTitle = "Empty";
    private String mBody = "";
    private String mImage = "";
    private String mComment = "";
    private float mRating = .0f;

    private News news;
    private int pos;

    private TextView titleTextView;
    private TextView bodyTextView;
    private ImageView imageView;
    private EditText commentBox;
    private RatingBar ratingBar;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(News news, int pos) {
        this.news = news;
        this.pos = pos;

        this.mTitle = news.getTitle();
        this.mBody = news.getBody();
        this.mImage = news.getImageUrl();
        this.mComment = news.getComment();
        this.mRating = news.getRating();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(ARG_TITLE);
            mBody = savedInstanceState.getString(ARG_BODY);
            mImage = savedInstanceState.getString(ARG_IMAGE);
            mComment = savedInstanceState.getString(ARG_COMMENT);
            mRating = savedInstanceState.getFloat(ARG_RATING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_details, container, false);

        titleTextView = v.findViewById(R.id.textViewTitle);
        titleTextView.setText(mTitle);

        bodyTextView = v.findViewById(R.id.textViewBody);
        bodyTextView.setText(mBody);

        imageView = v.findViewById(R.id.imageView);
        new ImageDownloadTask(mImage, imageView).execute();

        commentBox = v.findViewById(R.id.editTextComment);
        commentBox.setText(mComment);

        ratingBar = v.findViewById(R.id.ratingBar);
        ratingBar.setRating(mRating);

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TITLE, mTitle);
        outState.putString(ARG_BODY, mBody);
        outState.putString(ARG_IMAGE, mImage);
        outState.putString(ARG_COMMENT, mComment);
        outState.putFloat(ARG_RATING, mRating);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRating = ratingBar.getRating();
        mComment = commentBox.getText().toString();

        try {
            news.setComment(mComment);
            news.setRating(mRating);
            news.saveToFile("news_"+pos+".json", getContext());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, mComment + " " + mRating);
    }

    //    public void setContent(String title, String body, String image) {
//        mTitle = title;
//        mBody = body;
//        mImage = image;
//
//        titleTextView.setText(mTitle);
//        bodyTextView.setText(mBody);
//        imageView.setImageResource(R.drawable.ic_launcher_foreground);
//        new ImageDownloadTask(mImage, imageView).execute();
//    }

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
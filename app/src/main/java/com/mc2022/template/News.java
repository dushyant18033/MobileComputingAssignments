package com.mc2022.template;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class News
{
    private static final String TAG = "News";

    private JSONObject json;

    private String contents;
    private String title;
    private String body;
    private String imageUrl;
    private String comment;
    private float rating;

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setComment(String comment) throws JSONException {
        this.comment = comment;
        json.put("comment", comment);
        Log.d(TAG, "comment:" + comment);
    }

    public void setRating(float rating) throws JSONException {
        this.rating = rating;
        json.put("rating", rating);
        Log.d(TAG, "rating:" + rating);
    }

    public News(String jsonStr) throws JSONException {
        contents = jsonStr;

        json = new JSONObject(jsonStr);

        title = json.getString("title");
        body = json.getString("body");
        imageUrl = json.getString("image-url");

        if (json.isNull("comment")) {
            comment = "No comment!";
        } else {
            comment = json.getString("comment");
        }

        if (json.isNull("rating")) {
            rating = .0f;
        } else {
            rating = (float) json.getDouble("rating");
        }
    }

    public static News readFromFile(String filename, Context context) throws IOException, JSONException {

        InputStream is = context.openFileInput(filename);

        if ( is != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String res = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (res = bufferedReader.readLine()) != null ) {
                stringBuilder.append("\n").append(res);
            }

            is.close();

            return new News(stringBuilder.toString());
        }

        return null;
    }

    public void saveToFile(String filename, Context context) throws IOException, JSONException {

        OutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);

        if (os != null) {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(json.toString(2));
            osw.close();
        }
    }

    @Override
    public String toString() {
        return contents;
    }
}

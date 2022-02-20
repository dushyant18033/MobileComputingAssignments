package com.mc2022.template;

import android.content.Context;

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
    private String contents;
    private String title;
    private String body;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public News(String jsonStr) throws JSONException {
        contents = jsonStr;

        JSONObject json = new JSONObject(jsonStr);

        title = json.getString("title");
        body = json.getString("body");
        imageUrl = json.getString("image-url");
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

    public void saveToFile(String filename, Context context) throws IOException {

        OutputStream os = context.openFileOutput(filename, Context.MODE_PRIVATE);

        if (os != null) {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(contents);
            osw.close();
        }
    }

    @Override
    public String toString() {
        return contents;
    }
}

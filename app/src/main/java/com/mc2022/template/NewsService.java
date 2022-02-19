package com.mc2022.template;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private static final int PERIOD = 5000;

    private static Timer timer;

    private int index = 0;

    public NewsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "NewsService starting...");

        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connMng.getActiveNetworkInfo();
                    boolean connected = netInfo.isConnected();
                    if (connected) {
                        new DownloadTask().execute("https://petwear.in/mc2022/news/news_" + index + ".json");
                        index++;
                    }
                }
            }, 0, PERIOD);
        }

        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    private class DownloadTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... strings) {

            String res = null;
            try {
                res = downloadJson(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG, res);

            try {
                JSONObject json = new JSONObject(res);

                String title = json.getString("title");
                String body = json.getString("body");
                String image = json.getString("image-url");

                Intent news = new Intent("NewsPublished");
                news.putExtra("title", title);
                news.putExtra("body", body);
                news.putExtra("image", image);
                sendBroadcast(news);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(Void result) {
        }

        private String downloadJson(String urlString) throws IOException {
            InputStream is = null;
            int BUF_SIZE = 1024;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int response = conn.getResponseCode();
                Log.d(TAG, "Response " + response);
                is = conn.getInputStream();

                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] rawBuffer = new char[BUF_SIZE];
                int readSize;
                StringBuffer buffer = new StringBuffer();
                while (((readSize = reader.read(rawBuffer)) != -1)) {
                    if (readSize > BUF_SIZE) {
                        readSize = BUF_SIZE;
                    }
                    buffer.append(rawBuffer, 0, readSize);
                }
                return buffer.toString();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
}
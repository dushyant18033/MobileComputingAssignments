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
    private static final int PERIOD = 10000;

    private static Timer timer;

    private int index = 0;

    public NewsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        index = intent.getIntExtra("start_index", 0);

        Log.d(TAG, "Service starting...");

        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connMng.getActiveNetworkInfo();
                    boolean connected = netInfo.isConnected();
                    if (connected) {
                        new DownloadTask().execute("https://petwear.in/mc2022/news/", "news_" + index + ".json");
                        index++;
                    }
                }
            }, 0, PERIOD);
        }

        Log.i(TAG, "onStartCommand");
        return START_STICKY;
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

        Log.d(TAG, "Service stopped.");
    }

    private class DownloadTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... strings) {

            String res = null;
            try {
                res = downloadJson(strings[0], strings[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                News news = new News(res);
                news.saveToFile("" + strings[1], getApplicationContext());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            Intent news = new Intent("NewsPublished");
            sendBroadcast(news);
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(Void result) {
        }

        private String downloadJson(String baseUri, String path) throws IOException {
            InputStream is = null;
            int BUF_SIZE = 1024;
            try {
                URL url = new URL(baseUri + path);
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
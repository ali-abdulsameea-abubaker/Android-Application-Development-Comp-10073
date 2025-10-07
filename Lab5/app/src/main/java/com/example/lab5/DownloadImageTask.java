package com.example.lab5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    public static String tag = "==DownloadImageTask==";
    public static int HTTP_OK = 200;

    protected Bitmap doInBackground(String... urls) {
        Bitmap bmp = null;
        Log.d(tag, "do background " + urls[0]);
        int statusCode = -1;
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            statusCode = conn.getResponseCode();
            if (statusCode == HTTP_OK) {
                bmp = BitmapFactory.decodeStream(conn.getInputStream());
            }
        } catch (MalformedURLException e) {
            Log.d(tag, "bad URL " + e);
        } catch (IOException e) {
            Log.d(tag, "bad I/O " + e);
        }
        Log.d(tag, "done " + statusCode);
        return bmp;
    }

    protected void onPostExecute(Bitmap result) {
        Log.d(tag, "onPostExecute()");
        if (result != null) {
            MainActivity.setImage(result);
        }
    }
}
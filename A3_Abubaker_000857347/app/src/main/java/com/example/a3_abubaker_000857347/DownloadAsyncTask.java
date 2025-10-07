package com.example.a3_abubaker_000857347;
/**
 * I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 */

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Handles background downloading of data from the Web API
 */
public class DownloadAsyncTask extends AsyncTask<String, Void, String> {
    /** Tag for logging */
    public static final String TAG = AppConstants.TAG_DOWNLOAD;
    /** HTTP OK status code */
    public static final int HTTP_OK = 200;
    /** Reference to the calling activity */
    private MainActivity mainActivity;

    /**
     * Constructor that stores reference to calling activity
     * @param activity The MainActivity instance
     */
    public DownloadAsyncTask(MainActivity activity) {
        mainActivity = activity;
    }

    /**
     * Downloads data from the supplied URL in the background
     * @param params First parameter is the URL to download from
     * @return The downloaded data as a string, or null on failure
     */
    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Starting Background Task");
        StringBuilder results = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            String line;

            // Open the Connection - GET is the default setRequestMethod
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Read the response
            int statusCode = conn.getResponseCode();
            if (statusCode == HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));

                while ((line = bufferedReader.readLine()) != null) {
                    results.append(line);
                }
            }
            Log.d(TAG, "Data received = " + results.length());
            Log.d(TAG, "Response Code: " + statusCode);
        } catch (MalformedURLException e) {
            Log.d(TAG, "bad URL " + e);
        } catch (IOException e) {
            Log.d(TAG, "bad I/O " + e);
        }

        return results.toString();
    }

    /**
     * Called after download is complete, processes the results
     * @param result The downloaded data as a string
     */
    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute()");
        mainActivity.processDownloadResult(result);
    }
}
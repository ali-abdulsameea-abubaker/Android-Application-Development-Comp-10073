package com.example.a3_abubaker_000857347;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Handles background downloading of project images
 */
public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
    /** Tag for logging */
    public static final String TAG = AppConstants.TAG_IMAGE_DOWNLOAD;
    /** HTTP OK status code */
    public static final int HTTP_OK = 200;
    /** Reference to the ImageView to display the downloaded image */
    private ImageView imageView;

    /**
     * Constructor that stores reference to the ImageView
     * @param imageView The ImageView to display the downloaded image
     */
    public ImageDownloadAsyncTask(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Downloads an image from the supplied URL in the background
     * @param params First parameter is the URL to download from
     * @return The downloaded image as a Bitmap, or null on failure
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        Log.d(TAG, "Starting Image Download");
        Bitmap bitmap = null;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int statusCode = conn.getResponseCode();

            if (statusCode == HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            Log.d(TAG, "Image Download Response Code: " + statusCode);
        } catch (IOException e) {
            Log.d(TAG, "Image Download Error: " + e);
        }

        return bitmap;
    }

    /**
     * Called after download is complete, displays the image
     * @param bitmap The downloaded image as a Bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d(TAG, "onPostExecute()");
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
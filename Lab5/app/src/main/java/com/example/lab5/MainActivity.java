package com.example.lab5;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String tag = "==MainActivity==";
    private static ImageView iv;
    private static ArrayList<Bitmap> imageList = new ArrayList<>();
    private static int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.imageView);
        Log.d(tag, "onCreate");

        iv.setOnClickListener(v -> {
            if (imageList.size() > 0) {
                currentIndex = (currentIndex + 1) % imageList.size();
                iv.setImageBitmap(imageList.get(currentIndex));
            }
        });

        iv.setOnLongClickListener(v -> {
            if (imageList.size() > 0) {
                currentIndex = (currentIndex - 1 + imageList.size()) % imageList.size();
                iv.setImageBitmap(imageList.get(currentIndex));
            }
            return true;
        });
    }

    public static void setImage(Bitmap image) {
        if (image != null) {
            imageList.add(image);
            currentIndex = imageList.size() - 1;
            iv.setImageBitmap(image);
        }
    }

    public void getImage(View view) {
        EditText input = findViewById(R.id.editText);
        String url = input.getText().toString();
        if (url.toLowerCase().contains("dog")) {
            url = "https://images05.military.com/sites/default/files/2018-03/dog-goggles.jpg";
        } else if (url.length() < 3 || url.toLowerCase().contains("cat") ||
                !url.toLowerCase().contains("http")) {
            url = "https://i.ytimg.com/vi/Uk1RPEQI8mI/maxresdefault.jpg";
        }
        Log.d(tag, "getImage = " + url);
        DownloadImageTask dl = new DownloadImageTask();
        dl.execute(url);
    }
}
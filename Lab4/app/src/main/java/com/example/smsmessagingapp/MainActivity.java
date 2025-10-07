package com.example.smsmessagingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private SharedPreferences sharedPreferences;
    private TextView titleTextView;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        titleTextView = findViewById(R.id.titleTextView);
        messageTextView = findViewById(R.id.messageTextView);

        // Set initial title
        titleTextView.setText("Message Clipboard");

        // Enable scrollbars for the message TextView
        messageTextView.setVerticalScrollBarEnabled(true);
        messageTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        // Always check and request permissions when the app starts
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            messageTextView.setText("No messages yet.");
            showDenialHistory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                messageTextView.setText("No messages yet.");
                showDenialHistory();
            } else {
                // Permission denied
                int denialCount = sharedPreferences.getInt("denialCount", 0) + 1;
                sharedPreferences.edit().putInt("denialCount", denialCount).apply();

                // Save the timestamp of the denial
                Set<String> denialTimestamps = sharedPreferences.getStringSet("denialTimestamps", new HashSet<>());
                String timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                denialTimestamps.add("Received on: " + timestamp + "\nFrom: 6509994321");
                sharedPreferences.edit().putStringSet("denialTimestamps", denialTimestamps).apply();

                if (denialCount == 1) {
                    // First denial
                    messageTextView.setText("No messages yet.");
                    Snackbar.make(findViewById(android.R.id.content), "SMS permission is required for this app to work. Please restart the app.", Snackbar.LENGTH_LONG).show();
                } else if (denialCount >= 2) {
                    // Second denial
                    messageTextView.setText("No messages yet.");
                    titleTextView.setText("Enable SMS permissions in settings, reinstall the app.");
                    Snackbar.make(findViewById(android.R.id.content), "SMS permission is required. Please modify app settings or reinstall the app.", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showDenialHistory() {
        int denialCount = sharedPreferences.getInt("denialCount", 0);
        Set<String> denialTimestamps = sharedPreferences.getStringSet("denialTimestamps", new HashSet<>());

        StringBuilder history = new StringBuilder();
        history.append("ActivityCompat.requestPermissions() will only show a dialogue the first two times it is called.\n\n");

        if (denialCount > 0) {
            for (String timestamp : denialTimestamps) {
                history.append(timestamp).append("\n\n");
            }
        }

        // Add explanation for user behavior
        history.append("If you deny permissions in the settings, ActivityCompat.requestPermissions() won't show a dialogue at all.\n");
        history.append("Your application should explain this behavior and tell the user what to do if they want the application to use permissions later on.\n");

        messageTextView.setText(history.toString());
    }

    public void updateMessageTextView(String message) {
        String currentText = messageTextView.getText().toString();
        String newText = currentText + "\n" + message;
        messageTextView.setText(newText);
    }

    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    String message = "";
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        message += "Received on: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n";
                        message += "From: " + smsMessage.getDisplayOriginatingAddress() + "\n";
                        message += "Message: " + smsMessage.getDisplayMessageBody() + "\n\n";
                    }
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.updateMessageTextView(message);
                }
            }
        }
    }
}
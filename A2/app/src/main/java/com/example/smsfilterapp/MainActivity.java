package com.example.smsfilterapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity class for the SMS Filter application.
 * This app allows users to filter and tag SMS messages with graphical icons.
 * Author: Ali Abubaker- 000857347 - it is my own work and no one else.
 * Mohawk College, Winter 2025
 * Assignment #2: SMS Filter App
 */
public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 1;
    private static final String PREFS_NAME = "PermissionPrefs";
    private static final String KEY_DENIAL_COUNT = "denial_count";
    private static final String KEY_SHOWED_RESTART_MESSAGE = "showed_restart_message";
    private static final String KEY_SHOWED_SETTINGS_MESSAGE = "showed_settings_message";
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String PDUS_KEY = "pdus";

    private final List<Message> messageList = new ArrayList<>();
    private int currentPosition = 0;
    private String filterText = "";
    private EditText editTextFilter;
    private TextView[] textViews;
    private ImageView[] imageViews;

    private final BroadcastReceiver smsReceiver = new SMSReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextFilter = findViewById(R.id.editTextFilter);
        if (editTextFilter == null) {
            throw new RuntimeException("EditText not found in layout");
        }

        textViews = new TextView[]{
                findViewById(R.id.textView1), findViewById(R.id.textView2),
                findViewById(R.id.textView3), findViewById(R.id.textView4)
        };
        for (TextView textView : textViews) {
            if (textView == null) {
                throw new RuntimeException("TextView not found in layout");
            }
        }

        imageViews = new ImageView[]{
                findViewById(R.id.imageView1), findViewById(R.id.imageView2),
                findViewById(R.id.imageView3), findViewById(R.id.imageView4)
        };
        for (ImageView imageView : imageViews) {
            if (imageView == null) {
                throw new RuntimeException("ImageView not found in layout");
            }
        }

        // Restore the state if available
        if (savedInstanceState != null) {
            messageList.clear();
            messageList.addAll((List<Message>) savedInstanceState.getSerializable("messageList"));
            currentPosition = savedInstanceState.getInt("currentPosition");
            filterText = savedInstanceState.getString("filterText");
            editTextFilter.setText(filterText);
            displayMessages();
        }

        // Set up navigation buttons
        Button btnNext = findViewById(R.id.btnNext);
        Button btnPrevious = findViewById(R.id.btnPrevious);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateMessages(1);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateMessages(-1);
            }
        });

        // Set up filter text listener
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Register the SMS receiver
        IntentFilter filter = new IntentFilter(SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, filter);

        // Request SMS permissions
        requestSMSPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(smsReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver was not registered, ignore
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("messageList", new ArrayList<>(messageList));
        outState.putInt("currentPosition", currentPosition);
        outState.putString("filterText", filterText);
    }

    /**
     * Requests SMS permissions from the user.
     */
    private void requestSMSPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int denialCount = prefs.getInt(KEY_DENIAL_COUNT, 0);

            if (denialCount == 1 && !prefs.getBoolean(KEY_SHOWED_RESTART_MESSAGE, false)) {
                Toast.makeText(this, "Please restart the app and allow SMS permissions.", Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean(KEY_SHOWED_RESTART_MESSAGE, true).apply();
            } else if (denialCount >= 2 && !prefs.getBoolean(KEY_SHOWED_SETTINGS_MESSAGE, false)) {
                Toast.makeText(this, "Please enable SMS permissions manually in settings.", Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean(KEY_SHOWED_SETTINGS_MESSAGE, true).apply();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                int denialCount = prefs.getInt(KEY_DENIAL_COUNT, 0);
                denialCount++;
                prefs.edit().putInt(KEY_DENIAL_COUNT, denialCount).apply();

                if (denialCount == 1 && !prefs.getBoolean(KEY_SHOWED_RESTART_MESSAGE, false)) {
                    Toast.makeText(this, "Please restart the app and allow SMS permissions.", Toast.LENGTH_LONG).show();
                    prefs.edit().putBoolean(KEY_SHOWED_RESTART_MESSAGE, true).apply();
                } else if (denialCount >= 2 && !prefs.getBoolean(KEY_SHOWED_SETTINGS_MESSAGE, false)) {
                    Toast.makeText(this, "Please enable SMS permissions manually in settings.", Toast.LENGTH_LONG).show();
                    prefs.edit().putBoolean(KEY_SHOWED_SETTINGS_MESSAGE, true).apply();
                }
            }
        }
    }

    /**
     * Updates the message list with a new SMS message.
     *
     * @param sender    The sender's phone number.
     * @param content   The message content.
     * @param timestamp The timestamp of the message.
     */
    public void updateMessageList(String sender, String content, long timestamp) {
        messageList.add(0, new Message(sender, content, timestamp));
        currentPosition = 0;
        displayMessages();
    }

    /**
     * Formats a message for display.
     *
     * @param message The message to format.
     * @return The formatted message string.
     */
    private String formatMessage(Message message) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedSender = formatSenderNumber(message.getSender());
        return String.format("Msg#%d Received on: %s\nFrom: %s\n%s",
                messageList.indexOf(message) + 1,
                sdf.format(new Date(message.getTimestamp())),
                formattedSender,
                message.getContent());
    }

    /**
     * Formats the sender's phone number.
     *
     * @param sender The sender's phone number.
     * @return The formatted phone number.
     */
    private String formatSenderNumber(String sender) {
        if (sender != null && sender.length() == 10) {
            return String.format("(%s) %s-%s",
                    sender.substring(0, 3),
                    sender.substring(3, 6),
                    sender.substring(6));
        }
        return sender;
    }

    /**
     * Navigates through the message history.
     *
     * @param direction The direction to navigate (1 for next, -1 for previous).
     */
    private void navigateMessages(int direction) {
        List<Message> filteredMessages = filterMessages();
        int newPosition = currentPosition + direction;

        if (newPosition >= 0 && newPosition <= filteredMessages.size() - 4) {
            currentPosition = newPosition;
            displayMessages();
        } else {
            Toast.makeText(this, "No more messages to display.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the filter text and refreshes the message display.
     *
     * @param newFilter The new filter text.
     */
    private void updateFilter(String newFilter) {
        filterText = newFilter.toLowerCase();
        currentPosition = 0;
        displayMessages();
    }

    /**
     * Filters messages based on the current filter text.
     *
     * @return A list of filtered messages.
     */
    private List<Message> filterMessages() {
        List<Message> filtered = new ArrayList<>();
        for (Message msg : messageList) {
            if (filterText.isEmpty() ||
                    msg.getSender().toLowerCase().contains(filterText) ||
                    msg.getContent().toLowerCase().contains(filterText)) {
                filtered.add(msg);
            }
        }
        return filtered;
    }

    /**
     * Displays the current set of messages.
     */
    private void displayMessages() {
        List<Message> allMessages = messageList; // Use the full message list
        for (int i = 0; i < 4; i++) {
            int messageIndex = currentPosition + i;
            if (messageIndex < allMessages.size()) {
                Message message = allMessages.get(messageIndex);

                // Log the message content for debugging
                Log.d("MainActivity", "Message Content: " + message.getContent());

                // Format the message metadata (number, date, time, sender)
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String formattedSender = formatSenderNumber(message.getSender());
                String messageMetadata = String.format("Msg#%d Received on: %s\nFrom: %s",
                        messageList.indexOf(message) + 1,
                        sdf.format(new Date(message.getTimestamp())),
                        formattedSender);

                // Check if the message is blocked
                if (isMessageBlocked(message)) {
                    // Display the metadata and hide the content
                    textViews[i].setText(messageMetadata);
                    imageViews[i].setImageResource(R.drawable.ic_blocked); // Red "X"
                } else {
                    // Display the full message (metadata + content)
                    textViews[i].setText(messageMetadata + "\n" + message.getContent());

                    // Display emoticons based on message content
                    if (message.getContent().contains(":-)")) {
                        Log.d("MainActivity", "Displaying Smiley Face");
                        imageViews[i].setImageResource(R.drawable.ic_smiley); // Smiley face
                    } else if (message.getContent().contains(":-(")) {
                        Log.d("MainActivity", "Displaying Sad Face");
                        imageViews[i].setImageResource(R.drawable.ic_sad); // Sad face
                    } else {
                        Log.d("MainActivity", "Displaying Default Star");
                        imageViews[i].setImageResource(R.drawable.btn_star); // Default star
                    }
                }
            } else {
                // Clear the TextView and ImageView if no message is available
                textViews[i].setText("");
                imageViews[i].setImageResource(0);
            }
        }
    }

    /**
     * Checks if a message is blocked by the filter.
     *
     * @param message The message to check.
     * @return True if the message is blocked, false otherwise.
     */
    private boolean isMessageBlocked(Message message) {
        return !filterText.isEmpty() &&
                (message.getSender().toLowerCase().contains(filterText) ||
                        message.getContent().toLowerCase().contains(filterText));
    }

    /**
     * Represents an SMS message with sender, content, and timestamp.
     */
    private static class Message implements Serializable {
        private final String sender;
        private final String content;
        private final long timestamp;

        public Message(String sender, String content, long timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getSender() {
            return sender;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * BroadcastReceiver for handling incoming SMS messages.
     */
    private class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get(PDUS_KEY);
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = smsMessage.getDisplayOriginatingAddress();
                            String messageBody = smsMessage.getMessageBody();
                            long timestamp = smsMessage.getTimestampMillis();
                            updateMessageList(sender, messageBody, timestamp);
                        }
                    }
                }
            }
        }
    }
}
package com.example.passwordsafe;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private boolean isLocked = true; // App starts in a locked state
    private boolean incorrectAttempt = false; // Track incorrect password attempts
    private AlertDialog alertDialog; // Store reference to AlertDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tv_status);
        updateStatus();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isLocked) {
                tvStatus.setText("Rotate back to Portrait to unlock");
                incorrectAttempt = false; // Reset incorrect attempt flag
                if (alertDialog != null) {
                    alertDialog.dismiss(); // Dismiss alert when rotating to landscape
                }
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isLocked) {
                showPasswordDialog(); // Always show the password prompt instead of the alert
            }
        }
    }

    private void updateStatus() {
        if (isLocked) {
            tvStatus.setText(getString(R.string.locked_message));
        } else {
            tvStatus.setText(getString(R.string.secret_data));
        }
    }

    private void showPasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);

        EditText etPassword = dialogView.findViewById(R.id.et_password);
        Button btnUnlock = dialogView.findViewById(R.id.btn_unlock);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnUnlock.setOnClickListener(v -> {
            String enteredPassword = etPassword.getText().toString();
            String correctPassword = getString(R.string.password);

            if (enteredPassword.equals(correctPassword)) {
                isLocked = false;
                incorrectAttempt = false; // Reset incorrect attempt flag
                updateStatus();
                dialog.dismiss();
            } else {
                dialog.dismiss(); // Dismiss dialog before showing alert
                incorrectAttempt = true; // Mark incorrect attempt
                showAlertDialog(); // Show alert message
            }
        });

        dialog.show();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.incorrect_password))
                .setCancelable(false) // User cannot dismiss alert manually
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing, force user to rotate phone instead
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }
}

package com.example.listactivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editProductName, editSerialNumber;
    private ProductDbHelper dbHelper;
    private static String savedName = null;
    private static String savedSerial = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ProductDbHelper(this);

        editProductName = findViewById(R.id.editProductName);
        editSerialNumber = findViewById(R.id.editSerialNumber);

        Button btnAdd = findViewById(R.id.btnAddProduct);
        Button btnView = findViewById(R.id.btnViewList);


        // Restore saved state
        if (savedName != null) editProductName.setText(savedName);
        if (savedSerial != null) editSerialNumber.setText(savedSerial);

        btnAdd.setOnClickListener(v -> addProduct());
        btnView.setOnClickListener(v -> viewList());

        if (savedInstanceState != null) {
            editProductName.setText(savedInstanceState.getString("productName"));
            editSerialNumber.setText(savedInstanceState.getString("serialNumber"));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("productName", editProductName.getText().toString());
        outState.putString("serialNumber", editSerialNumber.getText().toString());
    }

    private void addProduct() {
        String name = editProductName.getText().toString().trim();
        String serial = editSerialNumber.getText().toString().trim();

        // Validate name (at least 5 non-space chars)
        if (name.replaceAll("\\s", "").length() < 5) {
            editProductName.setError("Must be ≥5 non-space characters");
            return;
        }

        // Validate serial (5-9 digits)
        if (!serial.matches("\\d{5,9}")) {
            editSerialNumber.setError("Must be 5-9 digits");
            return;
        }

        long id = dbHelper.addProduct(name, serial);
        if (id != -1) {
            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
            editProductName.setText("");
            editSerialNumber.setText("");
        } else {
            Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewList() {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }





    @Override
    protected void onPause() {
        super.onPause();
        // Save current input
        savedName = editProductName.getText().toString();
        savedSerial = editSerialNumber.getText().toString();
    }
}
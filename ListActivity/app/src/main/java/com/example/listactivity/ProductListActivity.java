package com.example.listactivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProductListActivity extends AppCompatActivity {
    private ProductDbHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new ProductDbHelper(this);
        ListView listView = findViewById(R.id.listViewProducts);

        // Set up adapter
        String[] fromColumns = {ProductDbHelper.COLUMN_NAME, ProductDbHelper.COLUMN_SERIAL};
        int[] toViews = {android.R.id.text1, android.R.id.text2};

        Cursor cursor = dbHelper.getAllProducts();
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                fromColumns,
                toViews,
                0);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor itemCursor = (Cursor) adapter.getItem(position);
            String productName = itemCursor.getString(
                    itemCursor.getColumnIndexOrThrow(ProductDbHelper.COLUMN_NAME));
            Toast.makeText(this, productName, Toast.LENGTH_SHORT).show();
        });
    }


}
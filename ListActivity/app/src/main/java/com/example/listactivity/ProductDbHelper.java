package com.example.listactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "products.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SERIAL = "serial";

    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SERIAL + " INTEGER)";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private boolean dbEmpty = false;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        dbEmpty = true;
        initDatabase(db); // Add initial data
    }

    private void initDatabase(SQLiteDatabase db) {
        if (!dbEmpty) return;

        // Add 5 sample products
        String[] products = {
                "Samsung TV", "0723661588",
                "South Computer", "566696",
                "US Bridge", "486843587",
                "LG Monitor 2F", "456783187",
                "Apple iPhone", "987654321"
        };

        for (int i = 0; i < products.length; i += 2) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, products[i]);
            values.put(COLUMN_SERIAL, products[i+1]);
            db.insert(TABLE_NAME, null, values);
        }
        dbEmpty = false;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add method to insert a product
    public long addProduct(String name, String serial) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SERIAL, serial);
        return db.insert(TABLE_NAME, null, values);
    }

    // Add method to get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SERIAL},
                null, null, null, null, null);
    }
}
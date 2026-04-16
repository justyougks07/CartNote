package com.example.cartnote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cartnote.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cart_db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_ITEMS = "cart_items";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_USERS = "users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IS_CHECKED = "isChecked";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_IMAGE_URI = "image_uri";
    private static final String COLUMN_CATEGORY_ID = "category_id";

    private static final String COLUMN_DESC = "description";

    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESC + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_PRICE + " INTEGER,"
                + COLUMN_IS_CHECKED + " INTEGER DEFAULT 0,"
                + COLUMN_DEADLINE + " TEXT,"
                + COLUMN_IMAGE_URI + " TEXT,"
                + COLUMN_CATEGORY_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_ITEMS_TABLE);

        // Initial Categories
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name, description) VALUES ('Pesta', 'Kebutuhan untuk acara pesta')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name, description) VALUES ('Ulang Tahun', 'Perlengkapan ulang tahun')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name, description) VALUES ('Kebutuhan Kantor', 'Alat tulis dan kantor')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (name, description) VALUES ('Harian', 'Belanja rutin harian')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public long insertCategory(String name, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESC, desc);
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    public List<com.example.cartnote.model.Category> getAllCategories() {
        List<com.example.cartnote.model.Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(new com.example.cartnote.model.Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public long insertItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_IS_CHECKED, item.getIsChecked());
        values.put(COLUMN_DEADLINE, item.getDeadline());
        values.put(COLUMN_IMAGE_URI, item.getImageUri());
        values.put(COLUMN_CATEGORY_ID, item.getCategoryId());
        return db.insert(TABLE_ITEMS, null, values);
    }

    public List<CartItem> getItemsByCategory(int categoryId) {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));
                item.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                item.setIsChecked(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED)));
                item.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
                item.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public List<CartItem> getAllItems() {
        List<CartItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));
                item.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                item.setIsChecked(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED)));
                item.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
                item.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public int updateItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_IS_CHECKED, item.getIsChecked());
        values.put(COLUMN_DEADLINE, item.getDeadline());
        values.put(COLUMN_IMAGE_URI, item.getImageUri());
        values.put(COLUMN_CATEGORY_ID, item.getCategoryId());
        return db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}

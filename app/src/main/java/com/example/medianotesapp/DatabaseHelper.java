package com.example.medianotesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "NotesDB_6";
    public static final String TABLE_NAME = "notes_6";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "image_path TEXT, " +
                "date TEXT, " +
                "reminder_flag INTEGER)"); // Required for Roll 6
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- ADD THESE TO DatabaseHelper.java ---

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateNote(int id, String title, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("image_path", imagePath);
        // Date and reminder_flag stay the same on update for this assignment

        db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}

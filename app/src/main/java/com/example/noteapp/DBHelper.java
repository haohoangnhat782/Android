package com.example.noteapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TIME_CREATED = "timeCreated";

    //Câu SQL để tạo bảng
    private static final String TABLE_CREATE = "create table " + TABLE_NAME + " (" +
            COLUMN_TITLE + " text not null, " +
            COLUMN_CONTENT + " text not null, " +
            COLUMN_TIME_CREATED + " interger primary key not null);";   //Dùng timeCreated để làm khóa chính

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

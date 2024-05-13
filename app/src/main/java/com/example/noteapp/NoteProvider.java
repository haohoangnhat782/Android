package com.example.noteapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.Context;
import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.noteapp.DBHelper;

public class NoteProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.notes.provider";
    private static final String PATH_NOTE_LIST = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_NOTE_LIST);

    private SQLiteDatabase database;
    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI("com.example.notes.provider", "notes", NOTES);
    }

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (database != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                break;
            case NOTE_ID:
                queryBuilder.appendWhere(DBHelper.COLUMN_TIME_CREATED + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBHelper.TABLE_NAME, null, values);
        if (id > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                rowsDeleted = database.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsDeleted = database.delete(DBHelper.TABLE_NAME, DBHelper.COLUMN_TIME_CREATED + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                rowsUpdated = database.update(DBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (id != null) {
                    rowsUpdated = database.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_TIME_CREATED + " = ?", new String[]{id});
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}

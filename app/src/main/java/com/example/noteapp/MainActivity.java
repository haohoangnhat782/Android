package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    private ArrayList<Note> notes = new ArrayList<Note>();

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private TextView emptyNoteListTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    //Gọi onResume() để cập nhật lại dữ liệu hiển thị sau khi thêm ghi chú
    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }
    private void initView() {
        recyclerView = findViewById(R.id.notesRecyclerView);

        //Lấy danh sách dữ liệu các ghi chú
        notes = getNoteFiles();

        //Khởi tạo adapter để hiển thị dữ liệu ở RecyclerView
        noteAdapter = new NoteAdapter(getApplicationContext(), notes);
        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add) {

           startActivity(new Intent(MainActivity.this, AddNoteActivity.class));

            return true;
        }
        if (id == R.id.note) {
            startActivity(new Intent(MainActivity.this, Timer.class));
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    public ArrayList<Note> getNoteFiles() {
        ArrayList<Note> noteFiles = new ArrayList<Note>();

        try {
            //Dùng ContentResolver để thao tác với dữ liệu
            ContentResolver contentResolver = getContentResolver();

            Uri uri = NoteProvider.CONTENT_URI;
            String[] projection = {DBHelper.COLUMN_TITLE, DBHelper.COLUMN_CONTENT, DBHelper.COLUMN_TIME_CREATED};    //Các dữ liệu cột cần lấy
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = DBHelper.COLUMN_TIME_CREATED + " DESC";;   //Kiểu sắp xếp (nên để theo thời gian giảm dần)

            //Cho cursor chạy để tìm hàng dữ liệu thỏa với điều kiện trong database
            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT));
                    long timeCreated = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_TIME_CREATED));
                    noteFiles.add(new Note(title, content, timeCreated));

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch(Exception e) {
            Log.d("MyTag", "Lỗi khi lấy danh sách ghi chú: " + e.toString());
        }

        return noteFiles;
    }

}
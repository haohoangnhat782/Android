package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {
    private EditText titleInput, contentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        initView();
    }

    private void initView() {
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save) {

            String noteTitle = titleInput.getText().toString();
            String noteContent = contentInput.getText().toString();

            //Kiểm tra thông tin nhập bị thiếu
            if(noteTitle.equals(""))
                Toast.makeText(getApplicationContext(), "Please enter your title!", Toast.LENGTH_SHORT).show();
            else if(noteContent.equals(""))
                Toast.makeText(getApplicationContext(), "Please enter your content!", Toast.LENGTH_SHORT).show();
            else {
                long timeCreated = System.currentTimeMillis();
                Note note = new Note(noteTitle, noteContent, timeCreated);

                try {
                    Uri uri = NoteProvider.CONTENT_URI;

                    //Thêm các giá trị cần lưu vào ContentValues
                    ContentValues values = new ContentValues();
                    values.put("title", noteTitle);
                    values.put("content", noteContent);
                    values.put("timeCreated", timeCreated);

                    //Dùng ContentResolver gọi hàm insert để thêm
                    getContentResolver().insert(uri, values);
                } catch(Exception e) {
                    Log.d("MyTag", "Lỗi khi thêm ghi chú: " + e.toString());
                }

                Toast.makeText(getApplicationContext(), "Note saved!", Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
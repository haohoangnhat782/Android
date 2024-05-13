package com.example.noteapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {
    AlertDialog alertDialog;
    private long timeCreated;
    private EditText titleInput, contentInput;
    private Button saveNoteBtn, deleteNoteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();
    }

    private void initView() {
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        saveNoteBtn = findViewById(R.id.saveNoteBtn);
        deleteNoteBtn = findViewById(R.id.deleteNoteBtn);

        getSelectedNote();

        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String content = contentInput.getText().toString();

                try {
                    ContentResolver contentResolver = getContentResolver();

                    Uri uri = NoteProvider.CONTENT_URI;
                    String[] projection = null;    //Các dữ liệu cột cần lấy
                    String selection = "timeCreated = ?";
                    String[] selectionArgs = new String[]{timeCreated + ""};

                    ContentValues values = new ContentValues();

                    values.put(DBHelper.COLUMN_TITLE, title);
                    values.put(DBHelper.COLUMN_CONTENT, content);

                    int rowsUpdated = contentResolver.update(uri, values, selection, selectionArgs);
                } catch (Exception e) {
                    Log.d("MyTag", e.toString());
                }

                Toast.makeText(getApplicationContext(), "Note saved!", Toast.LENGTH_SHORT).show();
                finish();
                Log.d("MyTag", "Dong act tu nut SAVE");
            }
        });

        deleteNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
                    builder.setMessage("Do you sure want to delete?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xử lý khi người dùng chọn "Yes"
                            try {
                                Uri uri = NoteProvider.CONTENT_URI;

                                String selection = "timeCreated = ?";
                                String[] selectionArgs = new String[] { timeCreated + "" };

                                ContentResolver contentResolver = getContentResolver();
                                int rowsDeleted = contentResolver.delete(uri, selection, selectionArgs);
                            } catch(Exception e) {
                                Log.d("MyTag", "Lỗi khi xóa: " + e.toString());
                            }

                            Toast.makeText(getApplicationContext(), "Note deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    // Thêm nút No
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch(Exception e) {
                    Log.d("MyTag", "Lỗi khi nhấn nút DELETE: " + e.toString());
                }
            }
        });


    }

    private void getSelectedNote() {
        try {
            Intent intent = getIntent();
            Bundle myBundle = intent.getBundleExtra("myBundle");
            timeCreated = myBundle.getLong("timeCreated");

            ContentResolver contentResolver = getContentResolver();

            Uri uri = NoteProvider.CONTENT_URI;
            String[] projection = null;    //Các dữ liệu cột cần lấy
            String selection = "timeCreated = ?";
            String[] selectionArgs = new String[]{timeCreated + ""};
            String sortOrder = null;;   //Kiểu sắp xếp (nên để theo thời gian giảm dần)

            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

            String title = "", content = "";

            if (cursor != null && cursor.moveToFirst()) {
                title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE));
                content = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT));
            } else Log.d("MyTag", "Cursor rỗng");

            if (cursor != null) {
                cursor.close();
            }

            titleInput.setText(title);
            contentInput.setText(content);
        } catch(Exception e) {
            Log.d("MyTag", "Lỗi khi lấy ghi chú được chọn: " + e.toString());
        }
    }
}
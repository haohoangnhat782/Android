package com.example.week10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Adapter.fileAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private fileAdapter fileListAdapter;
    private List<MyFile> fileList;
    public File currentDirectory;

    public boolean hasMusicFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        fileList = new ArrayList<>();
        fileListAdapter = new fileAdapter(fileList, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fileListAdapter);


        // Đặt thư mục hiện tại là thư mục gốc
        currentDirectory = Environment.getExternalStorageDirectory();

        // Kiểm tra xem quyền truy cập vào bộ nhớ nội bộ đã được cấp hay chưa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, kiểm tra xem người dùng đã từng từ chối quyền hay chưa
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Hiển thị thông báo giải thích cho người dùng về việc cần quyền truy cập vào bộ nhớ nội bộ
                // Ví dụ: sử dụng AlertDialog để hiển thị thông báo

                new AlertDialog.Builder(this)
                        .setTitle("Cấp quyền truy cập bộ nhớ")
                        .setMessage("Ứng dụng cần truy cập vào bộ nhớ để ...")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            // Yêu cầu quyền truy cập vào bộ nhớ nội bộ
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_REQUEST_CODE);
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            // Xử lý khi người dùng từ chối cấp quyền
                            // Ví dụ: thông báo cho người dùng biết rằng quyền truy cập bị từ chối
                        })
                        .create()
                        .show();
            } else {
                // Yêu cầu quyền truy cập vào bộ nhớ nội bộ
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Quyền truy cập vào bộ nhớ nội bộ đã được cấp
            loadFiles();
        }

        Button buttonUp = findViewById(R.id.buttonUp);
        Button buttonSelect = findViewById(R.id.buttonSelect);
        Button buttonExit = findViewById(R.id.buttonExit);



        buttonUp.setOnClickListener(v -> {
            // Thực hiện logic khi nút Lên được nhấn
            // Ví dụ: Trở về thư mục cha
            // Gọi phương thức traverseParentDirectory() để duyệt thư mục cha
            traverseParentDirectory();

        });



        buttonSelect.setOnClickListener(v -> {
            boolean hasMusicFile = false;
            for (MyFile file : fileList) {
                if (isMusicFile(file)) {
                    hasMusicFile = true;
                    break;
                }
            }
            if (hasMusicFile) {
                // Thực hiện logic khi nút Chọn được nhấn
                // Lấy đường dẫn file nhạc từ currentDirectory hoặc từ một nguồn khác trong MainActivity
                // Thay thế bằng nguồn đường dẫn thực tế
                // Chuyển sang MusicPlayerActivity và truyền đường dẫn file nhạc qua Intent
                String musicFilePath = currentDirectory.getPath();
                Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                intent.putExtra("musicPath", musicFilePath);
                startActivity(intent);
            }
        });

        buttonExit.setOnClickListener(v -> {
            // Thực hiện logic khi nút Thoát được nhấn
            // Ví dụ: Thoát khỏi ứng dụng
            finish();
        });
    }
    private void loadFiles() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        traverseDirectory(new File(rootPath));


    }



    @SuppressLint("NotifyDataSetChanged")
    public void traverseDirectory(File directory) {
        // Xóa danh sách tệp hiện tại
        fileList.clear();

        // Kiểm tra xem thư mục có tồn tại không
        if (directory.exists()) {
            // Lấy danh sách tất cả các tệp và thư mục trong thư mục hiện tại
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()&& !file.isHidden()) {
                        // Nếu là thư mục, thêm vào danh sách thư mục
                        fileList.add(new MyFile(file.getName(), file.getPath(), true));
                    }
                }
                for (File file : files) {
                    if (file.isFile() && !file.isHidden()) {
                        // Nếu là tệp tin, thêm vào danh sách tệp tin
                        fileList.add(new MyFile(file.getName(), file.getPath(), false));
                    }
                }

            }
        }

        // Cập nhật adapter để hiển thị danh sách thư mục
        fileListAdapter.notifyDataSetChanged();

        // Cập nhật đường dẫn hiện tại vào TextView "vitri"
        TextView vitriTextView = findViewById(R.id.vitri);
        vitriTextView.setText(directory.getAbsolutePath());

        // Cập nhật thư mục hiện tại
        currentDirectory = directory;
        fileListAdapter.setCurrentDirectory(currentDirectory);
    }
    private boolean isMusicFile(MyFile file) {
        String extension = file.getExtension();
        return extension != null && (extension.equalsIgnoreCase(".mp3")
                || extension.equalsIgnoreCase(".wav")
                || extension.equalsIgnoreCase(".flac"));
    }

    private void traverseParentDirectory() {


        // Kiểm tra xem thư mục cha có tồn tại không
        File parentDirectory = currentDirectory.getParentFile();
        if (parentDirectory != null) {
            fileList.clear();
            traverseDirectory(parentDirectory);
            currentDirectory = parentDirectory;
            fileListAdapter.setCurrentDirectory(currentDirectory);

        }else {
            Toast.makeText(this, "This is the root directory", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFiles();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
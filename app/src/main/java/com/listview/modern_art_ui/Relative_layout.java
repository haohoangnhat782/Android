package com.listview.modern_art_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

public class Relative_layout extends AppCompatActivity {

    private TextView vuonga1;
    private TextView vuonga2;
    private TextView vuonga3;
    private TextView vuonga4;
    private TextView vuonga5;
    private SeekBar seekBara1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_layout);
        vuonga1=findViewById(R.id.txt_vuonga1);
        vuonga2=findViewById(R.id.txt_vuonga2);
        vuonga3=findViewById(R.id.txt_vuonga3);
        vuonga4=findViewById(R.id.txt_vuonga4);
        vuonga5=findViewById(R.id.txt_vuonga5);


        seekBara1 = findViewById(R.id.seekBara1);

        seekBara1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeRectanglesColor(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void changeRectanglesColor(int color) {
        vuonga1.setBackgroundColor(Color.argb(100, color, 0, 0));
        vuonga2.setBackgroundColor(Color.argb(100, 0, color, 90));
        vuonga3.setBackgroundColor(Color.argb(100, 41, 91, color));
        vuonga4.setBackgroundColor(Color.argb(100, 94, color, 21));
        vuonga5.setBackgroundColor(Color.argb(100, 242, color, 231));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuMoreInfo) {
            showMoreInformationDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showMoreInformationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhóm 5");
        builder.setMessage("Làm về đổi màu khi kéo seekbar");

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
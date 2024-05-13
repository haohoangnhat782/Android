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

public class Table_layout extends AppCompatActivity {
    private TextView vuong1;
    private TextView vuong2;
    private TextView vuong3;
    private TextView vuong4;
    private TextView vuong5;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_layout);
        vuong1=findViewById(R.id.txt_vuongb1);
        vuong2=findViewById(R.id.txt_vuongb2);
        vuong3=findViewById(R.id.txt_vuongb3);
        vuong4=findViewById(R.id.txt_vuongb4);
        vuong5=findViewById(R.id.txt_vuongb5);


        seekBar = findViewById(R.id.seekBarb1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        vuong1.setBackgroundColor(Color.argb(100, color, 0, 0));
        vuong2.setBackgroundColor(Color.argb(100, 0, color, 90));
        vuong3.setBackgroundColor(Color.argb(100, 41, 91, color));
        vuong4.setBackgroundColor(Color.argb(100, 94, color, 21));
        vuong5.setBackgroundColor(Color.argb(100, 242, color, 231));
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
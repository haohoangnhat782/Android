package com.example.noteapp;

import static android.widget.Toast.makeText;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Timer extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
private  Button btn_hen;
    private  Button btn_chon;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private   int currentHour;
    private    int currentMinute;
   private   int hourDifference;
   private int minuteDifference;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
         textView =  findViewById(R.id.txt_gio);
        notification_cannel();

        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(this,broadcast_receiver.class),PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        btn_chon =  findViewById(R.id.btn_chon);
        btn_chon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });


    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Lấy giờ và phút hiện tại
        Calendar currentTime = Calendar.getInstance();
        currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        currentMinute = currentTime.get(Calendar.MINUTE);


         hourDifference = hourOfDay - currentHour;
         minuteDifference = minute - currentMinute;


       textView.setText( hourOfDay+" Giờ "+ minute+" Phút");
//        textView.setText( hourDifference +" Giờ "+    minuteDifference+" Phút");
        if (hourDifference == 0) {
            set_notification_alarm(minuteDifference * 60 * 1000);
        } else if (minuteDifference == 0) {
            set_notification_alarm(hourDifference * 60 * 60 * 1000);
        } else if (hourDifference != 0 && minuteDifference != 0) {
            set_notification_alarm((hourDifference * 60 * 60 * 1000) + (minuteDifference * 60 * 1000));
        } else {
            Toast.makeText(Timer.this, "Chọn thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
        }



    }


    public void set_notification_alarm(long interval){
        long triggerAtMillis = System.currentTimeMillis() + interval;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
    private void notification_cannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String description = "Reminder Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notification",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
package com.example.noteapp;

public class NoteTime {
    private long reminderTime; // Thời gian hẹn giờ

    public NoteTime(String title, String content, long timeCreated, long reminderTime) {
        // Khởi tạo các trường dữ liệu
        this.reminderTime = reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public long getReminderTime() {
        return reminderTime;
    }
}

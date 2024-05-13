package com.example.noteapp;

public class Note {
    private String title, content;
    private long timeCreated;

    public Note() {

    }

    public Note(String title, String content, long timeCreated) {
        this.title = title;
        this.content = content;
        this.timeCreated = timeCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}

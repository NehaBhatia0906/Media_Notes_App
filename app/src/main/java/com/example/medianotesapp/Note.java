package com.example.medianotesapp;

public class Note {
    public int id;
    public String title;
    public String description;
    public String imagePath;
    public String date;
    public int reminderFlag; // Required for Roll No 6 customization

    public Note(int id, String title, String description, String imagePath, String date, int reminderFlag) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.date = date;
        this.reminderFlag = reminderFlag;
    }
}
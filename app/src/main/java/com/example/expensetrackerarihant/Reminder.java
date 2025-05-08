package com.example.expensetrackerarihant;

public class Reminder {
    private String date;
    private String time;
    private String category;
    private String description;

    // Required empty constructor for Firestore
    public Reminder() {
    }

    public Reminder(String date, String time, String category, String description) {
        this.date = date;
        this.time = time;
        this.category = category;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.example.expensetrackerarihant;

public class Transaction {
    private String date, category, description, type;
    private double amount;

    public Transaction() {
        // Empty constructor for Firestore
    }

    public Transaction(String date, String category, String description, String type, double amount) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}


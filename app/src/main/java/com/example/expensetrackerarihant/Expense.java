package com.example.expensetrackerarihant;

public class Expense {
    private String date;
    private String category;
    private String description;
    private String type;
    private double amount;

    public Expense() {
        // Needed for Firebase deserialization
    }

    public Expense(String date, String category, String description, String type, double amount) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }

    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
}

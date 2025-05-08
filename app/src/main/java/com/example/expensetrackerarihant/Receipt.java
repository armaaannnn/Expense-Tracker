package com.example.expensetrackerarihant;

public class Receipt {
    private String category;
    private String date;
    private int amount;

    // Required empty constructor for Firestore
    public Receipt() {
    }

    public Receipt(String category, String date, int amount) {
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

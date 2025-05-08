package com.example.expensetrackerarihant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddExpense;
    private RecyclerView recyclerView;
    private TextView tvBalance, tvIncomeBalance, tvExpenseBalance;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        setupBottomNavigation();
        setupFab();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddExpense = findViewById(R.id.fab_add_expense);
        recyclerView = findViewById(R.id.recyclerView);
        tvBalance = findViewById(R.id.tv_balance);
        tvIncomeBalance = findViewById(R.id.tv_income_balance);
        tvExpenseBalance = findViewById(R.id.tv_expense_balance);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);
    }

    private void setupFab() {
        fabAddExpense.setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class));
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_reminder) {
                startActivity(new Intent(DashboardActivity.this, ReminderActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(DashboardActivity.this, StatisticsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchExpenses(); // 🔥 Always refresh on screen resume
    }

    private void fetchExpenses() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("expenses") // ✅ Correct collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        expenseList.addAll(queryDocumentSnapshots.toObjects(Expense.class));
                        expenseAdapter.notifyDataSetChanged();
                    }
                    calculateAndUpdateBalance();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(DashboardActivity.this, "Error fetching expenses: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void calculateAndUpdateBalance() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (Expense expense : expenseList) {
            if (expense != null && expense.getType() != null) {
                if (expense.getType().equalsIgnoreCase("Income")) {
                    totalIncome += expense.getAmount();
                } else if (expense.getType().equalsIgnoreCase("Expense")) {
                    totalExpense += expense.getAmount();
                }
            }
        }

        double balance = totalIncome - totalExpense;

        tvBalance.setText(String.format("₹%.2f", balance));
        tvIncomeBalance.setText(String.format("Income: ₹%.2f", totalIncome));
        tvExpenseBalance.setText(String.format("Expense: ₹%.2f", totalExpense));
    }
}


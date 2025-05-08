package com.example.expensetrackerarihant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private TextView titleText, dateText;
    private RadioGroup expenseIncomeGroup;
    private RadioButton expenseRadioButton, incomeRadioButton;
    private Spinner categorySpinner;
    private EditText descriptionEditText, totalAmountEditText;
    private Button saveButton, cancelButton;
    private ImageButton backButton, settingsButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.dateText);
        expenseIncomeGroup = findViewById(R.id.expenseIncomeGroup);
        expenseRadioButton = findViewById(R.id.expenseRadioButton);
        incomeRadioButton = findViewById(R.id.incomeRadioButton);
        categorySpinner = findViewById(R.id.categorySpinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);
        settingsButton = findViewById(R.id.settingsButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        expenseRadioButton.setOnClickListener(v -> titleText.setText("Add New Expense"));
        incomeRadioButton.setOnClickListener(v -> titleText.setText("Add New Income"));

        dateText.setOnClickListener(v -> showDatePickerDialog());

        backButton.setOnClickListener(v -> onBackPressed());

        cancelButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveTransaction());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddExpenseActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth++; // Month is 0-indexed
                    String selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    dateText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveTransaction() {
        int selectedId = expenseIncomeGroup.getCheckedRadioButtonId();
        String transactionType = "";

        if (selectedId == R.id.expenseRadioButton) {
            transactionType = "Expense";
        } else if (selectedId == R.id.incomeRadioButton) {
            transactionType = "Income";
        } else {
            Toast.makeText(this, "Please select Expense or Income", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = dateText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String description = descriptionEditText.getText().toString().trim();
        String amountText = totalAmountEditText.getText().toString().trim();

        if (date.isEmpty() || amountText.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("date", date);
        transaction.put("category", category);
        transaction.put("description", description);
        transaction.put("amount", amount);
        transaction.put("type", transactionType);

        db.collection("users")
                .document(userId)
                .collection("expenses") // ✅ Now saving into 'expenses' collection
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Transaction Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

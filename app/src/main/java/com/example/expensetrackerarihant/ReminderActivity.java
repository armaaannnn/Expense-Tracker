package com.example.expensetrackerarihant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private TextView dateText, timeText;
    private Spinner categorySpinner;
    private EditText descriptionInput;
    private RecyclerView reminderRecyclerView;
    private Button editButton, saveButton;
    private FloatingActionButton fabAddReminder;
    private BottomNavigationView bottomNavigation;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference reminderRef;

    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        categorySpinner = findViewById(R.id.categorySpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        fabAddReminder = findViewById(R.id.fabAddReminder);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Spinner
        setupCategorySpinner();

        // RecyclerView
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderRecyclerView.setAdapter(reminderAdapter);

        // Firestore collection reference per user
        String userId = auth.getCurrentUser().getUid();
        reminderRef = db.collection("users").document(userId).collection("reminders");

        loadRemindersFromFirestore();

        findViewById(R.id.datePickerButton).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.timePickerButton).setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> saveReminder());

        editButton.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Clicked", Toast.LENGTH_SHORT).show();
        });

        fabAddReminder.setOnClickListener(v -> {
            Toast.makeText(this, "Add Reminder FAB Clicked", Toast.LENGTH_SHORT).show();
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(this, DashboardActivity.class));
                    return true;
                case R.id.nav_reminder:
                    return true;
                case R.id.nav_statistics:
                    startActivity(new Intent(this, StatisticsActivity.class));
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
            }
            return false;
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
            dateText.setText(dateStr);
        }, year, month, day).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY), minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String timeStr = String.format("%02d:%02d", hourOfDay, minute1);
            timeText.setText(timeStr);
        }, hour, minute, false).show();
    }

    private void setupCategorySpinner() {
        String[] categories = {"Groceries", "Bills", "Workout", "Meeting", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void saveReminder() {
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String description = descriptionInput.getText().toString();

        if (date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder(date, time, category, description);

        reminderRef.add(reminder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
                    loadRemindersFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save reminder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadRemindersFromFirestore() {
        reminderList.clear();
        reminderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Reminder reminder = doc.toObject(Reminder.class);
                    reminderList.add(reminder);
                }
                reminderAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

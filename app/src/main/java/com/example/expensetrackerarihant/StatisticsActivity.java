package com.example.expensetrackerarihant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView receiptRecyclerView;
    EditText searchEditText;
    ReceiptAdapter receiptAdapter;
    ImageView backIcon, settingsIcon;

    FirebaseFirestore db;
    FirebaseAuth auth;

    List<Receipt> receiptList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Firebase init
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        receiptRecyclerView = findViewById(R.id.receiptRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        backIcon = findViewById(R.id.backIcon);
        settingsIcon = findViewById(R.id.settingsIcon);

        // Set up RecyclerView
        receiptAdapter = new ReceiptAdapter(receiptList);
        receiptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receiptRecyclerView.setAdapter(receiptAdapter);

        // Fetch data from Firestore
        loadReceiptsFromFirestore();

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                receiptAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Toolbar icons
        backIcon.setOnClickListener(v -> onBackPressed());
        settingsIcon.setOnClickListener(v -> {
            // Handle settings click
        });

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(StatisticsActivity.this, DashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_reminder) {
                startActivity(new Intent(StatisticsActivity.this, ReminderActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(StatisticsActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_statistics) {
                return true;
            }
            return false;
        });
    }

    private void loadReceiptsFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        CollectionReference receiptsRef = db.collection("users").document(userId).collection("receipts");

        receiptsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            receiptList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Receipt receipt = doc.toObject(Receipt.class);
                receiptList.add(receipt);
            }
            receiptAdapter.setOriginalList(receiptList); // Update original list for search
            receiptAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load receipts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}

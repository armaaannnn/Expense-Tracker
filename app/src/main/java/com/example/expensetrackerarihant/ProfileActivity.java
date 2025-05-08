package com.example.expensetrackerarihant;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone;
    Button btnUpdateProfile;
    BottomNavigationView bottomNavigationView;
    ImageView btnBack, btnNotifications;
    TextView txtChangePicture;

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnBack = findViewById(R.id.btnBack);
        btnNotifications = findViewById(R.id.btnNotifications);
        txtChangePicture = findViewById(R.id.txtChangePicture);

        // Load profile data from Firestore
        loadUserProfile();

        // Handle profile update
        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());

        // Optional: Handle back button
        btnBack.setOnClickListener(v -> finish());

        // Optional: Change profile picture
        txtChangePicture.setOnClickListener(v -> {
            // TODO: Open gallery or camera to change profile picture
        });

        // Optional: Open notifications
        btnNotifications.setOnClickListener(v -> {
            // TODO: Open notifications screen
        });

        // Bottom navigation setup
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_reminder) {
                    startActivity(new Intent(ProfileActivity.this, ReminderActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_statistics) {
                    startActivity(new Intent(ProfileActivity.this, StatisticsActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUserProfile() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("mobile");

                        etName.setText(name != null ? name : "");
                        etEmail.setText(email != null ? email : "");
                        etPhone.setText(phone != null ? phone : "");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("mobile", phone);

        db.collection("users").document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}

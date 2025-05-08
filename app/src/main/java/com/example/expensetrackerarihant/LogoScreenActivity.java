package com.example.expensetrackerarihant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LogoScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);

        // Reference to Get Started button
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Set click listener to open your dashboard activity
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogoScreenActivity.this, LoginActivity.class); // Replace with your actual activity
            startActivity(intent);
            finish(); // Close splash screen so it can't be returned to
        });
    }
}

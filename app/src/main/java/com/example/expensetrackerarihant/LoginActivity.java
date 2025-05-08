package com.example.expensetrackerarihant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button btnSignUp;
    CheckBox checkboxTerms;
    TextView tvCreateAccount;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Linking views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        checkboxTerms = findViewById(R.id.checkboxTerms);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        // Sign Up button click (used as login in this context)
        btnSignUp.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkboxTerms.isChecked()) {
                Toast.makeText(this, "You must agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // "Create Account" text click
        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}

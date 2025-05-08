package com.example.expensetrackerarihant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    EditText fullName, email, mobile, dob, password, confirmPassword;
    CheckBox termsCheckBox;
    Button signUpButton;
    TextView loginText;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        fullName = findViewById(R.id.et_full_name);
        email = findViewById(R.id.et_email);
        mobile = findViewById(R.id.et_mobile);
        dob = findViewById(R.id.et_dob);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        termsCheckBox = findViewById(R.id.cb_terms);
        signUpButton = findViewById(R.id.btn_signup);
        loginText = findViewById(R.id.tv_login);

        // Set input filters
        setInputFilters();

        // Set DOB picker
        dob.setOnClickListener(v -> showDatePickerDialog());

        // Handle sign up
        signUpButton.setOnClickListener(v -> {
            String name = fullName.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String phone = mobile.getText().toString().trim();
            String birthDate = dob.getText().toString().trim();
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            // Validation
            if (name.isEmpty() || emailInput.isEmpty() || phone.isEmpty() ||
                    birthDate.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!name.matches("[a-zA-Z ]+")) {
                Toast.makeText(this, "Name should contain only letters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!birthDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                Toast.makeText(this, "Enter DOB in format DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!termsCheckBox.isChecked()) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the email being used
            Log.d("SIGNUP", "Trying to register with email: " + emailInput);

            // Firebase Auth: create user
            mAuth.createUserWithEmailAndPassword(emailInput, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            // Prepare user data (DO NOT store password)
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullName", name);
                            user.put("email", emailInput);
                            user.put("mobile", phone);
                            user.put("dob", birthDate);

                            // Save user data to Firestore
                            db.collection("users").document(uid)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CreateAccountActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(CreateAccountActivity.this, "This email is already registered. Please log in instead.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        // Navigate to login screen
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setInputFilters() {
        // Full Name: Only letters and spaces
        fullName.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
        });

        // Mobile: Only digits and max length 10
        mobile.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    dob.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }
}

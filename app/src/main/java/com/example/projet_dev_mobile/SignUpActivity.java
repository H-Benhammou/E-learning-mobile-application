package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupFirstName, signupLastName, signupPhone;
    private Button signupButton;
    private TextView loginRedirectText;

    AutoCompleteTextView roleDropdown;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupFirstName = findViewById(R.id.signup_first_name);
        signupLastName = findViewById(R.id.signup_last_name);
        signupPhone = findViewById(R.id.signup_phone);
        roleDropdown = findViewById(R.id.signup_role);

        String[] roles = {"Learner", "Educator"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, roles);

        roleDropdown.setAdapter(adapter);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String firstName = signupFirstName.getText().toString().trim();
                String lastName = signupLastName.getText().toString().trim();
                String phone = signupPhone.getText().toString().trim();
                String selectedRole = roleDropdown.getText().toString().trim();

                if (selectedRole.isEmpty()) {
                    roleDropdown.setError("Please select a role");
                    return;
                }

                if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }

                if (firstName.isEmpty()) {
                    signupFirstName.setError("First name is required");
                    return;
                }
                if (lastName.isEmpty()) {
                    signupLastName.setError("Last name is required");
                    return;
                }
                if (phone.isEmpty()) {
                    signupPhone.setError("Phone number is required");
                    return;
                }

                if (password.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();

                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", firstName);
                                user.put("lastName", lastName);
                                user.put("phone", phone);
                                user.put("email", email);
                                user.put("role", selectedRole);

                                db.collection("users").document(uid).set(user)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });


    }


}
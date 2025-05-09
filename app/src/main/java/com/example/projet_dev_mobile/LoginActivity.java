package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!password.isEmpty()) {
                    loginUser(email, password);
                } else {
                    loginPassword.setError("Password cannot be empty");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Email cannot be empty");
            } else {
                loginEmail.setError("Email must be valid");
            }
        });

        signupRedirectText.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> getUserDetailsByEmail(email))
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show());
    }

    private void getUserDetailsByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    if (!queryDocuments.isEmpty()) {
                        String firstName = queryDocuments.getDocuments().get(0).getString("firstName");
                        String lastName = queryDocuments.getDocuments().get(0).getString("lastName");
                        String role = queryDocuments.getDocuments().get(0).getString("role");

                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        startHomeActivity(firstName, lastName, role);
                    } else {
                        Toast.makeText(LoginActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Failed to fetch user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void startHomeActivity(String firstName, String lastName, String role) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }
}

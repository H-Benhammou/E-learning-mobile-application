package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstnameTextView, lastnameTextView, emailTextView, phoneTextView, passwordTextView;

    private Button ProfileBtn, deleteAccountBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize TextViews
        firstnameTextView = findViewById(R.id.firstnameTextView);
        lastnameTextView = findViewById(R.id.lastnameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        passwordTextView = findViewById(R.id.passwordTextView); // NOTE: Password cannot be fetched from Firebase

        ProfileBtn = findViewById(R.id.editProfileButton);
        deleteAccountBtn = findViewById(R.id.deleteAccountButton);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            emailTextView.setText(email);
            fetchUserDetailsByEmail(email);
        } else {
            Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }

        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        deleteAccountBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete your account ?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        if (currentUser != null) {
                            String email = currentUser.getEmail();

                            // 1. Supprimer le document Firestore (basé sur email)
                            db.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                                doc.getReference().delete(); // Supprimer chaque document correspondant
                                            }

                                            // 2. Supprimer le compte Authentification
                                            currentUser.delete()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(this, "Account deleted succesfully", Toast.LENGTH_SHORT).show();

                                                            // 3. Redirection vers MainActivity
                                                            Intent intent = new Intent(this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(this, "Error while deleting Auth: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(this, "User not found ins Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void fetchUserDetailsByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                        String firstname = doc.getString("firstName");
                        String lastname = doc.getString("lastName");
                        String phone = doc.getString("phone");
                        String password = doc.getString("password");

                        // Set data into TextViews
                        firstnameTextView.setText(firstname);
                        lastnameTextView.setText(lastname);
                        phoneTextView.setText(phone);
                        passwordTextView.setText(password); // Firebase doesn't expose passwords

                    } else {
                        Toast.makeText(this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


}

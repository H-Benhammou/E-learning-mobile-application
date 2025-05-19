package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPhone, editPassword, newPassword;
    private Button btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        newPassword = findViewById(R.id.newPassword);
        editPassword = findViewById(R.id.editPassword);
        btnUpdate = findViewById(R.id.saveButton);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Set email field from the auth user
            editEmail.setText(currentUser.getEmail());
            // Load additional profile data from Firestore
            loadProfileData();
        } else {
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    /**
     * Loads the user profile data from Firestore using the user's UID.
     */
    private void loadProfileData() {
        String uid = currentUser.getUid();

        // Here we assume that you store user details in a document with the UID as the document id.
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                editFirstName.setText(documentSnapshot.getString("firstName"));
                editLastName.setText(documentSnapshot.getString("lastName"));
                editPhone.setText(documentSnapshot.getString("phone"));
            } else {
                Toast.makeText(EditProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditProfileActivity.this, "Error loading data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Updates both the Firestore user profile and the Firebase Authentication email (if changed).
     */
    private void updateProfile() {
        String newFirstName = editFirstName.getText().toString().trim();
        String newLastName = editLastName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String currentPass = editPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();

        if (newFirstName.isEmpty() || newLastName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || currentPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentEmail = currentUser.getEmail();
        boolean emailChanged = !newEmail.equals(currentEmail);
        boolean passwordChanged = !newPass.isEmpty();

        // Reauthenticate with current credentials
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPass);
        currentUser.reauthenticate(credential).addOnSuccessListener(unused -> {
            // Update Firestore
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", newFirstName);
            updates.put("lastName", newLastName);
            updates.put("email", newEmail);
            updates.put("phone", newPhone);

            docRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Firestore profile updated.", Toast.LENGTH_SHORT).show();

                        // Update email in Firebase Auth
                        if (emailChanged) {
                            currentUser.verifyBeforeUpdateEmail(newEmail)
                                    .addOnSuccessListener(aVoid1 ->
                                            Toast.makeText(this, "Email updated in Authentication.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        // Update password in Firebase Auth
                        if (passwordChanged) {
                            currentUser.updatePassword(newPass)
                                    .addOnSuccessListener(aVoid1 ->
                                            Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        Intent intent = new Intent(this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update Firestore profile: " + e.getMessage(), Toast.LENGTH_LONG).show());

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

}

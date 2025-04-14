package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logoutBtn;
    FirebaseAuth authlogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize the welcome TextView
        TextView welcomeText = findViewById(R.id.welcome_text);

        logoutBtn = findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authlogout.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        // Get the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Extract the first name and last name
            String firstName = extras.getString("firstName", "");
            String lastName = extras.getString("lastName", "");

            // Set the welcome message
            String welcomeMessage = "Welcome, " + firstName + " " + lastName + " !";
            welcomeText.setText(welcomeMessage);
        }

        // Log out button logic


    }
}
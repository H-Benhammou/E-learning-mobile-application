package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx. appcompat. widget. Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logoutBtn, profileBtn;
    FirebaseAuth authlogout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);







        // Handle Logout button separately if needed
        findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.profile_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        // Initialize the welcome TextView
        TextView welcomeText = findViewById(R.id.welcome_text);

        logoutBtn = findViewById(R.id.logout_button);

        profileBtn = findViewById(R.id.profile_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authlogout.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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
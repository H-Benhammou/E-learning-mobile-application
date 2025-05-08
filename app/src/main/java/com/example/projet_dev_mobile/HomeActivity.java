package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logoutBtn, courseBtn;
    private FirebaseAuth authlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialisation des vues
        TextView welcomeText = findViewById(R.id.welcome_text);
        logoutBtn = findViewById(R.id.logout_button);
        courseBtn = findViewById(R.id.course_button);
        authlogout = FirebaseAuth.getInstance();

        // Action bouton déconnexion
        logoutBtn.setOnClickListener(view -> {
            authlogout.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        // Action bouton pour afficher la liste des cours
        courseBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
            startActivity(intent);
        });

        // Récupération et affichage du nom de l'utilisateur
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String firstName = extras.getString("firstName", "");
            String lastName = extras.getString("lastName", "");
            String welcomeMessage = "Welcome, " + firstName + " " + lastName + "!";
            welcomeText.setText(welcomeMessage);
        }
    }
}

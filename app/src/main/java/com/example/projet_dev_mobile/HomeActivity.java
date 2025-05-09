package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logoutBtn;
    private FirebaseAuth authlogout;
    private LinearLayout dashboardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        TextView welcomeText = findViewById(R.id.welcome_text);
        logoutBtn = findViewById(R.id.logout_button);
        dashboardContainer = findViewById(R.id.dashboard_container);
        authlogout = FirebaseAuth.getInstance();

        // Vérifie si l'utilisateur est connecté
        if (authlogout.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Récupère les extras (s’il y en a)
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String welcomeMessage = "Welcome!";

        if (extras != null) {
            // Vérifie si on a demandé une section spécifique
            String targetSection = extras.getString("target_section", "");
            if ("learner_dashboard".equals(targetSection)) {
                welcomeMessage = "Welcome, Learner!";
                showLearnerDashboard();
            } else {
                String firstName = extras.getString("firstName", "");
                String lastName = extras.getString("lastName", "");
                String role = extras.getString("role", "learner");

                welcomeMessage = "Welcome, " + firstName + " " + lastName + "!";

                if (role.equalsIgnoreCase("educator")) {
                    showEducatorDashboard();
                } else {
                    showLearnerDashboard();
                }
            }
        } else {
            // Si pas d’extras, affiche par défaut le learner dashboard
            showLearnerDashboard();
        }

        welcomeText.setText(welcomeMessage);

        // Bouton logout
        logoutBtn.setOnClickListener(view -> {
            authlogout.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showEducatorDashboard() {
        dashboardContainer.removeAllViews();
        addButton("Gestion du Profil");
        addButton("Catalogue de Cours");
        addButton("Gestion du Contenu de Formation");
        addButton("Suivi des Apprenants");
        addButton("Commentaires et Évaluations");
    }

    private void showLearnerDashboard() {
        dashboardContainer.removeAllViews();
        addButton("Gestion du Profil");
        addButton("Catalogue de Cours");
        addButton("Suivi de cours");
    }

    private void addButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setBackgroundTintList(getResources().getColorStateList(R.color.purple));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 0);
        button.setLayoutParams(params);

        button.setOnClickListener(v -> {
            if (text.equals("Suivi de cours")) {
                startActivity(new Intent(HomeActivity.this, CourseTrackingActivity.class));
            } else {
                Toast.makeText(HomeActivity.this, text + " (à implémenter)", Toast.LENGTH_SHORT).show();
            }
        });

        dashboardContainer.addView(button);
    }
}

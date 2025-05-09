package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CourseTrackingActivity extends AppCompatActivity {

    private CardView cardVideos, cardDocuments, cardQuiz, cardTests, cardResume, cardHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_tracking);

        cardVideos = findViewById(R.id.card_videos);
        cardDocuments = findViewById(R.id.card_documents);
        cardTests = findViewById(R.id.card_tests);
        cardHome = findViewById(R.id.card_home);

        cardVideos.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoListActivity.class);
            startActivity(intent);
        });

        cardDocuments.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentListActivity.class);
            startActivity(intent);
        });

        cardTests.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestListActivity.class);
            startActivity(intent);
        });

        cardHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("target_section", "learner_dashboard"); // Ajout pour forcer learner
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

package com.example.projet_dev_mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class CourseDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetailsActivity";

    private ImageView courseImage;
    private TextView courseTitle;
    private TextView courseDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        // Bind des views
        courseImage = findViewById(R.id.detailCourseImage);
        courseTitle = findViewById(R.id.detailCourseTitle);
        courseDescription = findViewById(R.id.detailCourseDescription);

        // Récupérer les extras passés par l’intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Log des données reçues
        Log.d(TAG, "Titre reçu : " + title);
        Log.d(TAG, "Description reçue : " + description);
        Log.d(TAG, "Image URL reçue : " + imageUrl);

        // Set des données dans les vues
        courseTitle.setText(title);
        courseDescription.setText(description);

        Glide.with(this)
                .load(imageUrl)
                .into(courseImage);
    }
}

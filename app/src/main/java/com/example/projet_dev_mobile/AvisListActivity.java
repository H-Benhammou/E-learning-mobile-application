package com.example.projet_dev_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AvisListActivity extends AppCompatActivity {

    private RecyclerView avisRecyclerView;
    private AvisAdapter avisAdapter;
    private List<Avis> avisList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avis_list);

        // Initialize views
        avisRecyclerView = findViewById(R.id.avisRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Setup RecyclerView
        avisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        avisList = new ArrayList<>();
        avisAdapter = new AvisAdapter(this, avisList);
        avisRecyclerView.setAdapter(avisAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get courseId from intent
        String courseId = getIntent().getStringExtra("courseId");
        if (courseId == null) {
            Toast.makeText(this, "Erreur: ID du cours non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        avisRecyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);

        // Fetch comments from Firestore
        db.collection("courses")
                .document(courseId)
                .collection("comments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        emptyStateTextView.setVisibility(View.VISIBLE);
                        avisRecyclerView.setVisibility(View.GONE);
                        return;
                    }

                    avisRecyclerView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setVisibility(View.GONE);
                    
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String userName = doc.getString("userName");
                        String comment = doc.getString("comment");
                        Double rating = doc.getDouble("rating");

                        if (userName != null && comment != null && rating != null) {
                            avisList.add(new Avis(userName, comment, rating));
                        }
                    }
                    avisAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erreur lors du chargement des commentaires: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}

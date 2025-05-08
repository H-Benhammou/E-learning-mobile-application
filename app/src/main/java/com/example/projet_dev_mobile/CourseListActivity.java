package com.example.projet_dev_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        // Initialisation RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation liste et adapter
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);
        recyclerView.setAdapter(courseAdapter);

        // Initialisation Firestore
        db = FirebaseFirestore.getInstance();

        // Charger les cours
        loadCourses();
    }

    private void loadCourses() {
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Course course = doc.toObject(Course.class);
                            course.setId(doc.getId()); // Important : récupérer l'ID Firestore
                            courseList.add(course);

                            // Log pour debug
                            Log.d("CourseDebug", "Course loaded: " + course.getTitle() + " | ID: " + course.getId());
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Erreur : ", task.getException());
                    }
                });
    }
}

package com.example.projet_dev_mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseTitle.setText(course.getTitle());
        holder.courseDescription.setText(course.getDescription());

        Glide.with(context)
                .load(course.getImageUrl())
                .into(holder.courseImage);

        // Bouton commenter → ouvrir un dialog
        holder.commentButton.setOnClickListener(v -> {
            showCommentDialog(course.getId());
        });

        // Bouton voir les avis
        holder.voirAvisButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AvisListActivity.class);
            intent.putExtra("courseId", course.getId());
            // Important si le context n'est pas une Activity :
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    private void showCommentDialog(String courseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_comment, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        EditText commentEditText = dialogView.findViewById(R.id.dialogCommentEditText);
        Button submitButton = dialogView.findViewById(R.id.dialogSubmitButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = commentEditText.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(context, "Veuillez écrire un commentaire.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "Vous devez être connecté pour commenter", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user details from Firestore
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                    
                    // Create comment data
                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("userId", currentUser.getUid());
                    commentData.put("userName", userName);
                    commentData.put("comment", comment);
                    commentData.put("rating", rating);
                    commentData.put("timestamp", System.currentTimeMillis());

                    // Save to Firestore
                    FirebaseFirestore.getInstance()
                        .collection("courses")
                        .document(courseId)
                        .collection("comments")
                        .add(commentData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(context, "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle, courseDescription;
        Button commentButton;
        Button voirAvisButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            commentButton = itemView.findViewById(R.id.commentButton);
            voirAvisButton = itemView.findViewById(R.id.voirAvisButton);
        }
    }
}

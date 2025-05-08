package com.example.projet_dev_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvisAdapter extends RecyclerView.Adapter<AvisAdapter.AvisViewHolder> {

    private Context context;
    private List<Avis> avisList;

    public AvisAdapter(Context context, List<Avis> avisList) {
        this.context = context;
        this.avisList = avisList;
    }

    @NonNull
    @Override
    public AvisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avis_item, parent, false);
        return new AvisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisViewHolder holder, int position) {
        Avis avis = avisList.get(position);
        holder.userNameTextView.setText(avis.getUserName());
        holder.commentTextView.setText(avis.getComment());
        holder.ratingBar.setRating((float) avis.getRating());
    }

    @Override
    public int getItemCount() {
        return avisList.size();
    }

    public class AvisViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, commentTextView;
        RatingBar ratingBar;

        public AvisViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

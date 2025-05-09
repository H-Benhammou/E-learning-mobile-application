package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView; // Si tu utilises CardView pour l'item
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_dev_mobile.R; // Pour accéder à tes layouts et IDs
import com.example.project_dev_mobile.model.Categorie; // Ton modèle Categorie
import com.squareup.picasso.Picasso; // Si tu utilises Picasso

import java.util.ArrayList;
import java.util.List;

public class CategorieAdapter extends RecyclerView.Adapter<CategorieAdapter.CategorieViewHolder> {

    private List<Categorie> categoriesList;
    private final OnCategorieClickListener listener;

    // Interface pour gérer les clics sur les catégories
    public interface OnCategorieClickListener {
        void onCategorieClick(Categorie categorie);
    }

    public CategorieAdapter(List<Categorie> categoriesInitiales, OnCategorieClickListener listener) {
        // Il est préférable de travailler avec une nouvelle instance de liste pour éviter
        // les modifications externes inattendues de la liste fournie.
        this.categoriesList = new ArrayList<>(categoriesInitiales);
        this.listener = listener;
    }

    /**
     * Met à jour la liste des catégories affichées par l'adaptateur.
     * L'appel à notifyDataSetChanged() doit être fait par l'activité ou le fragment
     * après avoir appelé cette méthode.
     * @param nouvellesCategories La nouvelle liste de catégories à afficher.
     */
    public void setCategories(List<Categorie> nouvellesCategories) {
        this.categoriesList.clear();
        if (nouvellesCategories != null) {
            this.categoriesList.addAll(nouvellesCategories);
        }
        // L'appel à notifyDataSetChanged() est généralement fait par l'activité/fragment
        // après avoir mis à jour les données de l'adapter.
        // Si tu préfères que l'adapter le fasse, décommente la ligne suivante :
        // notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategorieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflater le layout de l'item de catégorie
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categorie, parent, false); // Assure-toi que item_categorie.xml existe
        return new CategorieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategorieViewHolder holder, int position) {
        // Récupérer la catégorie à la position actuelle
        Categorie categorieCourante = categoriesList.get(position);
        // Lier les données de la catégorie au ViewHolder
        holder.bind(categorieCourante, listener);
    }

    @Override
    public int getItemCount() {
        // Retourner le nombre total de catégories
        return categoriesList != null ? categoriesList.size() : 0;
    }

    // ViewHolder pour les items de catégorie
    static class CategorieViewHolder extends RecyclerView.ViewHolder {
        // Déclarer les vues de ton item_categorie.xml
        // Adapte ces vues à ce que tu as réellement dans ton layout item_categorie.xml
        CardView cardViewCategorie; // Optionnel, si ton item est un CardView
        ImageView imageViewCategorie;
        TextView textViewNomCategorie;
        // TextView textViewDescriptionCategorie; // Optionnel, si tu affiches aussi la description

        public CategorieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialiser les vues à partir de itemView.findViewById()
            // Assure-toi que les IDs correspondent à ceux dans item_categorie.xml
            cardViewCategorie = itemView.findViewById(R.id.cardViewCategorie); // Remplace par l'ID de ton CardView si tu en as un
            imageViewCategorie = itemView.findViewById(R.id.imageViewCategorie); // Remplace par l'ID de ton ImageView
            textViewNomCategorie = itemView.findViewById(R.id.textViewNomCategorie);   // Remplace par l'ID de ton TextView pour le nom
            // textViewDescriptionCategorie = itemView.findViewById(R.id.textViewDescriptionCategorie); // Si tu as une description
        }

        // Méthode pour lier les données d'une catégorie aux vues du ViewHolder
        public void bind(final Categorie categorie, final OnCategorieClickListener listener) {
            // Afficher le nom de la catégorie
            if (categorie.getNom() != null) {
                textViewNomCategorie.setText(categorie.getNom());
            } else {
                textViewNomCategorie.setText("Nom indisponible"); // Valeur par défaut
            }

            // Afficher la description (si tu as une TextView pour ça)
            // if (textViewDescriptionCategorie != null && categorie.getDescription() != null) {
            //    textViewDescriptionCategorie.setText(categorie.getDescription());
            // }

            // Charger l'image de la catégorie avec Picasso (ou une autre bibliothèque)
            if (categorie.getImageUrl() != null && !categorie.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(categorie.getImageUrl())
                        .placeholder(R.drawable.placeholder_image) // Crée placeholder_image.png dans res/drawable
                        .error(R.drawable.error_image)         // Crée error_image.png dans res/drawable
                        .fit() // Ajuste l'image pour qu'elle remplisse l'ImageView et soit coupée si nécessaire
                        .centerCrop() // Centre l'image coupée
                        .into(imageViewCategorie);
            } else {
                // Si pas d'URL, afficher une image par défaut ou cacher l'ImageView
                imageViewCategorie.setImageResource(R.drawable.default_category_image); // Crée default_category_image.png
            }

            // Définir le listener de clic pour l'item entier (ou pour le CardView)
            View itemViewToClick = (cardViewCategorie != null) ? cardViewCategorie : itemView;
            itemViewToClick.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategorieClick(categorie);
                }
            });
        }
    }
}
package com.example.project_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
// SearchView d'androidx si tu utilises Material Components, sinon android.widget.SearchView
import androidx.appcompat.widget.SearchView; // Ou android.widget.SearchView

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_dev_mobile.adapters.CategorieAdapter;
import com.example.project_dev_mobile.adapters.CoursPopulaireAdapter; // Assure-toi que ce nom est correct
import com.example.project_dev_mobile.model.Categorie;
import com.example.project_dev_mobile.model.Cours;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CatalogueActivity extends AppCompatActivity {

    private static final String TAG = "CatalogueActivity";

    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewCours;
    private CategorieAdapter categorieAdapter;
    private CoursPopulaireAdapter coursPopulaireAdapter; // Ton adaptateur pour les cours
    private ProgressBar progressBar;
    private SearchView searchView;
    private TabLayout tabLayout;

    private CatalogueManager catalogueManager;
    private String currentNiveauFilter = "Tous"; // Pour garder une trace du filtre actuel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue); // Assure-toi que ce layout existe

        // Initialiser le gestionnaire de catalogue
        catalogueManager = CatalogueManager.getInstance();

        // Initialiser les vues
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);

        // Configurer RecyclerView pour les catégories
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categorieAdapter = new CategorieAdapter(new ArrayList<>(), this::onCategorieClicked);
        recyclerViewCategories.setAdapter(categorieAdapter);

        // Configurer RecyclerView pour les cours
        recyclerViewCours = findViewById(R.id.recyclerViewCours); // Assure-toi que cet ID est dans ton layout
        recyclerViewCours.setLayoutManager(new GridLayoutManager(this, 2)); // Grille de 2 colonnes
        coursPopulaireAdapter = new CoursPopulaireAdapter(new ArrayList<>(), this::onCoursClicked);
        recyclerViewCours.setAdapter(coursPopulaireAdapter);

        // Configurer la recherche et les onglets
        setupSearchView();
        setupTabLayout();

        // Charger les données initiales
        chargerDonneesInitiales();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                lancerRechercheCours(query);
                searchView.clearFocus(); // Cache le clavier
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Si le texte de recherche est vidé, recharger les cours selon le filtre d'onglet actuel
                if (newText.isEmpty()) {
                    handleTabSelection(currentNiveauFilter);
                }
                return true;
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Tous"));
        tabLayout.addTab(tabLayout.newTab().setText("Débutant"));
        tabLayout.addTab(tabLayout.newTab().setText("Intermédiaire"));
        tabLayout.addTab(tabLayout.newTab().setText("Avancé"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    currentNiveauFilter = tab.getText().toString();
                    handleTabSelection(currentNiveauFilter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optionnel: recharger si l'utilisateur reclique sur le même onglet
                if (tab.getText() != null) {
                    currentNiveauFilter = tab.getText().toString();
                    handleTabSelection(currentNiveauFilter);
                }
            }
        });
    }

    private void handleTabSelection(String niveauFilter) {
        if ("Tous".equalsIgnoreCase(niveauFilter)) {
            // Charger tous les cours (sans filtre de niveau spécifique, mais toujours publiés)
            // Utilise le cache local s'il est rempli et à jour, sinon recharge
            List<Cours> tousLesCoursCharges = catalogueManager.getTousLesCoursCache();
            if (!tousLesCoursCharges.isEmpty()) {
                coursPopulaireAdapter.setCours(tousLesCoursCharges);
                coursPopulaireAdapter.notifyDataSetChanged();
            } else {
                chargerTousLesCours(); // Recharge depuis Firestore si le cache est vide
            }
        } else {
            filtrerCoursParNiveauLocalement(niveauFilter);
        }
    }

    private void chargerDonneesInitiales() {
        Log.d(TAG, "Chargement des données initiales...");
        chargerCategories();
        chargerTousLesCours(); // Charge la liste initiale de tous les cours publiés
    }

    private void chargerCategories() {
        progressBar.setVisibility(View.VISIBLE);
        catalogueManager.chargerCategories(new CatalogueManager.CatalogueCallback() {
            @Override
            public void onCategoriesLoaded(List<Categorie> categories) {
                Log.d(TAG, "Catégories chargées: " + categories.size());
                categorieAdapter.setCategories(categories); // Méthode dans l'adapter pour mettre à jour la liste
                categorieAdapter.notifyDataSetChanged();
                // Ne pas cacher la progressBar ici, attendre que les cours soient aussi chargés
                // ou gérer la visibilité de la progressBar de manière plus fine
            }

            @Override
            public void onCoursLoaded(List<Cours> cours) { /* Non utilisé dans ce callback spécifique */ }

            @Override
            public void onError(Exception e) {
                // Cacher la progressBar même en cas d'erreur sur les catégories
                // pour ne pas bloquer l'UI si le chargement des cours réussit (ou échoue aussi)
                if (recyclerViewCours.getAdapter() == null || recyclerViewCours.getAdapter().getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.e(TAG, "Erreur chargement catégories", e);
                Toast.makeText(CatalogueActivity.this, "Erreur chargement catégories: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void chargerTousLesCours() {
        progressBar.setVisibility(View.VISIBLE);
        catalogueManager.chargerTousLesCours(new CatalogueManager.CatalogueCallback() {
            @Override
            public void onCategoriesLoaded(List<Categorie> categories) { /* Non utilisé */ }

            @Override
            public void onCoursLoaded(List<Cours> cours) {
                Log.d(TAG, "Tous les cours chargés: " + cours.size());
                coursPopulaireAdapter.setCours(cours); // Méthode dans l'adapter
                coursPopulaireAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE); // Cacher la progressBar après le chargement des cours
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Erreur chargement tous les cours", e);
                Toast.makeText(CatalogueActivity.this, "Erreur chargement cours: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void lancerRechercheCours(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Si la query est vide, réafficher les cours selon le filtre actuel
            handleTabSelection(currentNiveauFilter);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        catalogueManager.rechercherCours(query, new CatalogueManager.CatalogueCallback() {
            @Override
            public void onCategoriesLoaded(List<Categorie> categories) { /* Non utilisé */ }

            @Override
            public void onCoursLoaded(List<Cours> cours) {
                Log.d(TAG, "Résultats recherche pour '" + query + "': " + cours.size());
                coursPopulaireAdapter.setCours(cours);
                coursPopulaireAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if (cours.isEmpty()) {
                    Toast.makeText(CatalogueActivity.this, "Aucun cours trouvé pour '" + query + "'", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Erreur recherche cours", e);
                Toast.makeText(CatalogueActivity.this, "Erreur de recherche: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filtrerCoursParNiveauLocalement(String niveau) {
        // Cette méthode filtre la liste des cours déjà en cache dans CatalogueManager
        List<Cours> tousLesCoursCharges = catalogueManager.getTousLesCoursCache();
        List<Cours> coursFiltres = new ArrayList<>();

        if (tousLesCoursCharges.isEmpty() && !"Tous".equalsIgnoreCase(niveau)) {
            Log.w(TAG, "Cache de cours vide, tentative de filtrage. Recharge des cours nécessaire.");
            // Optionnellement, forcer un rechargement si le cache est vide et qu'on veut filtrer
            // chargerTousLesCours(); // Attention aux boucles si mal géré
            Toast.makeText(this, "Données des cours en cours de chargement, veuillez patienter.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE); // Montrer la barre en attendant
            return; // Sortir pour attendre que le chargement se fasse
        }


        for (Cours cours : tousLesCoursCharges) {
            if (cours.getNiveau() != null && cours.getNiveau().equalsIgnoreCase(niveau)) {
                coursFiltres.add(cours);
            }
        }

        Log.d(TAG, "Filtrage pour niveau '" + niveau + "': " + coursFiltres.size() + " cours trouvés.");
        coursPopulaireAdapter.setCours(coursFiltres);
        coursPopulaireAdapter.notifyDataSetChanged();

        if (coursFiltres.isEmpty() && !"Tous".equalsIgnoreCase(niveau)) {
            Toast.makeText(CatalogueActivity.this, "Aucun cours trouvé pour le niveau " + niveau, Toast.LENGTH_SHORT).show();
        }
    }

    // --- Gestion des Clics sur les Items ---
    private void onCategorieClicked(Categorie categorie) {
        Log.d(TAG, "Catégorie cliquée: " + categorie.getNom() + " (ID: " + categorie.getId() + ")");
        Intent intent = new Intent(CatalogueActivity.this, CoursParCategorieActivity.class);
        intent.putExtra("CATEGORIE_ID", categorie.getId());
        intent.putExtra("CATEGORIE_NOM", categorie.getNom());
        startActivity(intent);
    }

    private void onCoursClicked(Cours cours) {
        Log.d(TAG, "Cours cliqué: " + cours.getTitre() + " (ID: " + cours.getId() + ")");
        Intent intent = new Intent(CatalogueActivity.this, DetailCoursActivity.class);
        intent.putExtra("COURS_ID", cours.getId());
        startActivity(intent);
    }

    // Optionnel: rafraîchir les données quand l'activité revient au premier plan
    // Cela peut être utile si des données sont modifiées dans d'autres activités.
    @Override
    protected void onResume() {
        super.onResume();
        // Tu peux décider si un rechargement complet est toujours nécessaire ici.
        // Parfois, il vaut mieux avoir un mécanisme de "swipe to refresh" ou un bouton.
        // Pour l'instant, on ne recharge pas automatiquement à chaque onResume pour éviter des appels réseau excessifs.
        // Si tu as besoin d'un rafraîchissement, décommente:
        // chargerDonneesInitiales();
        Log.d(TAG, "onResume appelé.");
    }
}
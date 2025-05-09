package com.example.project_dev_mobile;

package com.example.project_dev_mobile;

import android.util.Log;
import androidx.annotation.NonNull;

// Importe tes modèles du package model
import com.example.project_dev_mobile.model.Categorie;
import com.example.project_dev_mobile.model.Cours;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks; // Pour créer des tâches échouées ou réussies manuellement
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue; // Pour les opérations sur les arrays (arrayUnion, arrayRemove)
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch; // Pour les opérations atomiques groupées

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CatalogueManager {
    private static final String TAG = "CatalogueManager";
    private static final String CATEGORIES_COLLECTION = "categories"; // Nom de la collection des catégories
    private static final String COURS_COLLECTION = "cours";           // Nom de la collection des cours

    private static CatalogueManager instance;

    private FirebaseFirestore db;
    private List<Categorie> localCategoriesCache;
    private List<Cours> localTousLesCoursCache;

    private CatalogueManager() {
        db = FirebaseFirestore.getInstance();
        localCategoriesCache = new ArrayList<>();
        localTousLesCoursCache = new ArrayList<>();
    }

    public static synchronized CatalogueManager getInstance() {
        if (instance == null) {
            instance = new CatalogueManager();
        }
        return instance;
    }

    public interface CatalogueCallback {
        void onCategoriesLoaded(List<Categorie> categories);
        void onCoursLoaded(List<Cours> cours);
        void onError(Exception e);
    }

    // --- MÉTHODES DE LECTURE (CHARGEMENT) ---

    public void chargerCategories(final CatalogueCallback callback) {
        db.collection(CATEGORIES_COLLECTION)
                .orderBy("nom") // Tri par nom alphabétique
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localCategoriesCache.clear();
                        List<Categorie> categoriesFromFirestore = new ArrayList<>();
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categorie categorie = document.toObject(Categorie.class);
                                categoriesFromFirestore.add(categorie);
                            }
                        }
                        localCategoriesCache.addAll(categoriesFromFirestore);
                        if (callback != null) {
                            callback.onCategoriesLoaded(new ArrayList<>(localCategoriesCache));
                        }
                    } else {
                        Log.w(TAG, "Erreur chargement catégories.", task.getException());
                        if (callback != null) {
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    public void chargerTousLesCours(final CatalogueCallback callback) {
        db.collection(COURS_COLLECTION)
                .whereEqualTo("estPublie", true)
                .orderBy("dateCreation", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localTousLesCoursCache.clear();
                        List<Cours> coursFromFirestore = new ArrayList<>();
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Cours cours = document.toObject(Cours.class);
                                coursFromFirestore.add(cours);
                            }
                        }
                        localTousLesCoursCache.addAll(coursFromFirestore);
                        if (callback != null) {
                            callback.onCoursLoaded(new ArrayList<>(localTousLesCoursCache));
                        }
                    } else {
                        Log.w(TAG, "Erreur chargement tous les cours.", task.getException());
                        if (callback != null) {
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    public void chargerCoursParCategorie(String categorieId, final CatalogueCallback callback) {
        if (categorieId == null || categorieId.trim().isEmpty()) {
            if (callback != null) callback.onError(new IllegalArgumentException("ID de catégorie ne peut être vide."));
            return;
        }
        db.collection(COURS_COLLECTION)
                .whereEqualTo("categorieId", categorieId)
                .whereEqualTo("estPublie", true)
                .orderBy("titre")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cours> coursDeLaCategorie = new ArrayList<>();
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Cours cours = document.toObject(Cours.class);
                                coursDeLaCategorie.add(cours);
                            }
                        }
                        if (callback != null) {
                            callback.onCoursLoaded(coursDeLaCategorie);
                        }
                    } else {
                        Log.w(TAG, "Erreur chargement cours pour catégorie: " + categorieId, task.getException());
                        if (callback != null) {
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    public void obtenirCoursDetaille(String coursId, final OnCompleteListener<DocumentSnapshot> callback) {
        if (coursId == null || coursId.trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("ID du cours ne peut être vide.")));
            return;
        }
        db.collection(COURS_COLLECTION).document(coursId)
                .get()
                .addOnCompleteListener(callback);
    }

    public void rechercherCours(String queryText, final CatalogueCallback callback) {
        if (queryText == null || queryText.trim().isEmpty()) {
            chargerTousLesCours(callback);
            return;
        }
        final String queryLower = queryText.toLowerCase().trim();

        db.collection(COURS_COLLECTION)
                .whereEqualTo("estPublie", true)
                // Firestore ne permet pas des requêtes OR complexes ou des LIKE %%.
                // Cette recherche est une approximation. Pour une vraie recherche, Algolia est mieux.
                // Ici, on charge tous les cours et on filtre côté client, ce qui n'est pas optimal pour de grandes datasets.
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Cours> resultatsFiltres = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Cours cours = document.toObject(Cours.class);
                            if ((cours.getTitre() != null && cours.getTitre().toLowerCase().contains(queryLower)) ||
                                    (cours.getDescription() != null && cours.getDescription().toLowerCase().contains(queryLower))) {
                                resultatsFiltres.add(cours);
                            }
                        }
                        if (callback != null) {
                            callback.onCoursLoaded(resultatsFiltres);
                        }
                    } else {
                        Log.w(TAG, "Erreur recherche cours.", task.getException());
                        if (callback != null) {
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    public List<Cours> getTousLesCoursCache() {
        return new ArrayList<>(this.localTousLesCoursCache);
    }

    // --- MÉTHODES D'ÉCRITURE (CRUD) ---

    // === CATÉGORIES ===
    public void ajouterCategorie(Categorie categorie, final OnCompleteListener<DocumentReference> callback) {
        if (categorie == null) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("Categorie ne peut pas être nulle.")));
            return;
        }
        db.collection(CATEGORIES_COLLECTION)
                .add(categorie) // Firestore génère l'ID
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Catégorie ajoutée avec ID: " + Objects.requireNonNull(task.getResult()).getId());
                        // Rafraîchir le cache local des catégories si nécessaire (ou laisser l'UI le faire)
                        // chargerCategories(null); // Exemple simple, pourrait être plus optimisé
                    } else {
                        Log.w(TAG, "Erreur ajout catégorie", task.getException());
                    }
                    if (callback != null) {
                        callback.onComplete(task);
                    }
                });
    }

    public void mettreAJourCategorie(Categorie categorie, final OnCompleteListener<Void> callback) {
        if (categorie == null || categorie.getId() == null || categorie.getId().trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("ID de catégorie invalide pour mise à jour.")));
            return;
        }
        db.collection(CATEGORIES_COLLECTION).document(categorie.getId())
                .set(categorie) // .set() écrase le document. Utilise .update() pour des champs spécifiques.
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Catégorie mise à jour: " + categorie.getId());
                        // Mettre à jour le cache local
                        for (int i = 0; i < localCategoriesCache.size(); i++) {
                            if (localCategoriesCache.get(i).getId().equals(categorie.getId())) {
                                localCategoriesCache.set(i, categorie);
                                break;
                            }
                        }
                    } else {
                        Log.w(TAG, "Erreur màj catégorie", task.getException());
                    }
                    if (callback != null) {
                        callback.onComplete(task);
                    }
                });
    }

    public void supprimerCategorie(String categorieId, final OnCompleteListener<Void> callback) {
        if (categorieId == null || categorieId.trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("ID de catégorie invalide pour suppression.")));
            return;
        }
        // ATTENTION: Supprimer une catégorie devrait idéalement aussi gérer les cours orphelins.
        // Soit les supprimer, soit les assigner à une catégorie "Non classé", soit empêcher la suppression si elle contient des cours.
        // Pour la simplicité ici, on supprime juste la catégorie.
        db.collection(CATEGORIES_COLLECTION).document(categorieId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Catégorie supprimée: " + categorieId);
                        localCategoriesCache.removeIf(cat -> cat.getId().equals(categorieId));
                    } else {
                        Log.w(TAG, "Erreur suppression catégorie", task.getException());
                    }
                    if (callback != null) {
                        callback.onComplete(task);
                    }
                });
    }

    // === COURS ===
    public void ajouterCours(Cours cours, final OnCompleteListener<DocumentReference> callback) {
        if (cours == null || cours.getCategorieId() == null || cours.getCategorieId().trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("Cours ou ID de catégorie invalide.")));
            return;
        }

        // Générer une référence pour le nouveau cours (pour obtenir l'ID avant l'écriture si besoin)
        DocumentReference nouveauCoursRef = db.collection(COURS_COLLECTION).document();
        // cours.setId(nouveauCoursRef.getId()); // @DocumentId s'en chargera à la lecture. Si tu as besoin de l'ID avant, c'est une option.

        WriteBatch batch = db.batch();

        // 1. Ajouter le cours
        batch.set(nouveauCoursRef, cours);

        // 2. Mettre à jour la catégorie parente pour ajouter l'ID de ce nouveau cours
        DocumentReference categorieRef = db.collection(CATEGORIES_COLLECTION).document(cours.getCategorieId());
        batch.update(categorieRef, "coursIds", FieldValue.arrayUnion(nouveauCoursRef.getId()));

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Cours ajouté avec ID: " + nouveauCoursRef.getId() + " et catégorie mise à jour.");
                // Rafraîchir les caches si besoin
            } else {
                Log.w(TAG, "Erreur ajout cours (batch)", task.getException());
            }
            if (callback != null) {
                // Le callback pour add attend un DocumentReference, mais le batch ne retourne pas ça directement.
                // On passe la référence qu'on a créée.
                if (task.isSuccessful()) {
                    Task<DocumentReference> resultTask = Tasks.forResult(nouveauCoursRef);
                    callback.onComplete(resultTask);
                } else {
                    callback.onComplete(Tasks.forException(Objects.requireNonNull(task.getException())));
                }
            }
        });
    }

    public void mettreAJourCours(Cours cours, final OnCompleteListener<Void> callback) {
        if (cours == null || cours.getId() == null || cours.getId().trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("ID de cours invalide pour mise à jour.")));
            return;
        }
        db.collection(COURS_COLLECTION).document(cours.getId())
                .set(cours) // .set() écrase. Utilise .update() pour des champs spécifiques si besoin.
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Cours mis à jour: " + cours.getId());
                        // Mettre à jour le cache local des cours
                        for (int i = 0; i < localTousLesCoursCache.size(); i++) {
                            if (localTousLesCoursCache.get(i).getId().equals(cours.getId())) {
                                localTousLesCoursCache.set(i, cours);
                                break;
                            }
                        }
                    } else {
                        Log.w(TAG, "Erreur màj cours", task.getException());
                    }
                    if (callback != null) {
                        callback.onComplete(task);
                    }
                });
    }

    public void supprimerCours(String coursId, String categorieParenteId, final OnCompleteListener<Void> callback) {
        if (coursId == null || coursId.trim().isEmpty() || categorieParenteId == null || categorieParenteId.trim().isEmpty()) {
            if (callback != null) callback.onComplete(Tasks.forException(new IllegalArgumentException("ID de cours ou de catégorie invalide pour suppression.")));
            return;
        }

        WriteBatch batch = db.batch();

        // 1. Supprimer le document du cours
        DocumentReference coursRef = db.collection(COURS_COLLECTION).document(coursId);
        batch.delete(coursRef);

        // 2. Mettre à jour la catégorie parente pour enlever l'ID de ce cours
        DocumentReference categorieRef = db.collection(CATEGORIES_COLLECTION).document(categorieParenteId);
        batch.update(categorieRef, "coursIds", FieldValue.arrayRemove(coursId));

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Cours supprimé: " + coursId + " et catégorie mise à jour.");
                localTousLesCoursCache.removeIf(c -> c.getId().equals(coursId));
            } else {
                Log.w(TAG, "Erreur suppression cours (batch)", task.getException());
            }
            if (callback != null) {
                callback.onComplete(task);
            }
        });
    }

    // Tu pourrais aussi avoir des méthodes pour gérer les chapitres et le contenu,
    // soit ici, soit dans des Managers dédiés (ChapitreManager, ContentManager).
}
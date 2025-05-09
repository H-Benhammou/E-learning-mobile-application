package com.example.project_dev_mobile.model; // Assure-toi que le package est correct

import com.google.firebase.firestore.DocumentId; // Import pour l'annotation
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Categorie implements Serializable {

    @DocumentId // Firestore remplira ce champ avec l'ID du document lors de la lecture
    private String id;
    private String nom;
    private String description;
    private String imageUrl;
    private List<String> coursIds; // Liste des IDs des cours appartenant à cette catégorie

    // Constructeur vide public OBLIGATOIRE pour Firestore
    public Categorie() {
        this.coursIds = new ArrayList<>(); // Initialiser les listes est une bonne pratique
    }

    // Constructeur pour la création manuelle d'objets (l'ID sera géré par Firestore)
    public Categorie(String nom, String description, String imageUrl) {
        this.nom = nom;
        this.description = description;
        this.imageUrl = imageUrl;
        this.coursIds = new ArrayList<>();
    }

    // Getters et Setters publics pour TOUS les champs
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getCoursIds() {
        // Toujours retourner une liste, jamais null, pour éviter les NullPointerExceptions
        if (coursIds == null) {
            coursIds = new ArrayList<>();
        }
        return coursIds;
    }

    public void setCoursIds(List<String> coursIds) {
        this.coursIds = coursIds;
    }

    // Méthodes utilitaires (optionnelles mais pratiques)
    public void ajouterCoursId(String coursId) {
        if (this.coursIds == null) {
            this.coursIds = new ArrayList<>();
        }
        if (coursId != null && !this.coursIds.contains(coursId)) {
            this.coursIds.add(coursId);
        }
    }

    public void supprimerCoursId(String coursId) {
        if (this.coursIds != null && coursId != null) {
            this.coursIds.remove(coursId);
        }
    }
}
package com.example.project_dev_mobile.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp; // Pour la date de création gérée par le serveur
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cours implements Serializable {

    @DocumentId
    private String id;
    private String titre;
    private String description;
    private String imageUrl;
    private String categorieId; // ID de la catégorie parente
    private String professeurId; // ID de l'utilisateur professeur
    @ServerTimestamp // Firestore remplira ce champ avec le timestamp du serveur à la création/màj
    private Date dateCreation;
    private float evaluation;
    private int nombreEvaluations;
    private List<String> chapitresIds; // Renommé pour plus de clarté : liste des IDs des chapitres
    private int dureeMinutes;
    private String niveau; // e.g., "Débutant", "Intermédiaire", "Avancé"
    private boolean estPublie;
    private List<String> etudiantsInscrits; // Liste des IDs des étudiants inscrits

    // Constructeur vide public OBLIGATOIRE pour Firestore
    public Cours() {
        this.chapitresIds = new ArrayList<>();
        this.etudiantsInscrits = new ArrayList<>();
        // dateCreation sera automatiquement settée par Firestore si @ServerTimestamp est présent
        // ou peut être initialisée ici si @ServerTimestamp n'est pas utilisé sur ce champ.
        // this.dateCreation = new Date(); // Décommenter si pas de @ServerTimestamp sur dateCreation
    }

    // Constructeur pour la création manuelle d'objets (l'ID sera géré par Firestore)
    public Cours(String titre, String description, String imageUrl, String categorieId,
                 String professeurId, int dureeMinutes, String niveau) {
        this.titre = titre;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categorieId = categorieId;
        this.professeurId = professeurId;
        this.dureeMinutes = dureeMinutes;
        this.niveau = niveau;
        this.estPublie = false; // Par défaut, un cours n'est pas publié
        this.evaluation = 0.0f;
        this.nombreEvaluations = 0;
        this.chapitresIds = new ArrayList<>();
        this.etudiantsInscrits = new ArrayList<>();
        // this.dateCreation = new Date(); // Laisser @ServerTimestamp gérer cela de préférence
    }

    // Getters et Setters publics pour TOUS les champs

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
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

    public String getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(String categorieId) {
        this.categorieId = categorieId;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public int getNombreEvaluations() {
        return nombreEvaluations;
    }

    public void setNombreEvaluations(int nombreEvaluations) {
        this.nombreEvaluations = nombreEvaluations;
    }

    public List<String> getChapitresIds() { // Nom du getter mis à jour
        if (chapitresIds == null) {
            chapitresIds = new ArrayList<>();
        }
        return chapitresIds;
    }

    public void setChapitresIds(List<String> chapitresIds) { // Nom du setter mis à jour
        this.chapitresIds = chapitresIds;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public boolean isEstPublie() { // Convention pour les getters de booléens
        return estPublie;
    }

    public void setEstPublie(boolean estPublie) {
        this.estPublie = estPublie;
    }

    public List<String> getEtudiantsInscrits() {
        if (etudiantsInscrits == null) {
            etudiantsInscrits = new ArrayList<>();
        }
        return etudiantsInscrits;
    }

    public void setEtudiantsInscrits(List<String> etudiantsInscrits) {
        this.etudiantsInscrits = etudiantsInscrits;
    }

    // Méthodes utilitaires
    public void inscrireEtudiant(String etudiantId) {
        if (this.etudiantsInscrits == null) {
            this.etudiantsInscrits = new ArrayList<>();
        }
        if (etudiantId != null && !this.etudiantsInscrits.contains(etudiantId)) {
            this.etudiantsInscrits.add(etudiantId);
        }
    }

    public void desinscrireEtudiant(String etudiantId) {
        if (this.etudiantsInscrits != null && etudiantId != null) {
            this.etudiantsInscrits.remove(etudiantId);
        }
    }

    public void ajouterChapitreId(String chapitreId) {
        if (this.chapitresIds == null) {
            this.chapitresIds = new ArrayList<>();
        }
        if (chapitreId != null && !this.chapitresIds.contains(chapitreId)) {
            this.chapitresIds.add(chapitreId);
        }
    }

    public void ajouterEvaluation(float nouvelleNote) {
        if (nouvelleNote >= 0 && nouvelleNote <= 5) { // Supposons une évaluation sur 5
            float totalPoints = this.evaluation * this.nombreEvaluations;
            this.nombreEvaluations++;
            this.evaluation = (totalPoints + nouvelleNote) / this.nombreEvaluations;
        }
    }
}
package com.example.project_dev_mobile;

class Cours {
    private String id;
    private String nom;
    private String description;
    private int duree; // Durée en heures

    public Cours(String id, String nom, String description, int duree) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.duree = duree;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public int getDuree() { return duree; }
}

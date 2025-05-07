package com.example.project_dev_mobile;

import java.util.HashMap;
import java.util.Map;

class Etudiant extends User {
    private Map<String, Double> progression; // Clé = ID du cours, Valeur = % complété

    public Etudiant(String id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
        this.progression = new HashMap<>();
    }

    public Map<String, Double> getProgression() { return progression; }
    public void setProgression(String coursId, Double pourcentage) {
        this.progression.put(coursId, pourcentage);
    }
}

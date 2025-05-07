package com.example.project_dev_mobile;

import java.util.ArrayList;
import java.util.List;

class Categorie {
    private String nom;
    private List<Cours> cours;

    public Categorie(String nom) {
        this.nom = nom;
        this.cours = new ArrayList<>();
    }

    public void ajouterCours(Cours cours) {
        this.cours.add(cours);
    }

    public List<Cours> getCours() {
        return cours;
    }
}


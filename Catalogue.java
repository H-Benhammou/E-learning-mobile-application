package com.example.project_dev_mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Catalogue {
    private Map<String, Categorie> categories;

    public Catalogue() {
        this.categories = new HashMap<>();
    }

    public void ajouterCategorie(String nom) {
        if (!categories.containsKey(nom)) {
            categories.put(nom, new Categorie(nom));
        }
    }

    public void ajouterCoursDansCategorie(String categorieNom, Cours cours) {
        if (categories.containsKey(categorieNom)) {
            categories.get(categorieNom).ajouterCours(cours);
        }
    }

    public List<Cours> getCoursParCategorie(String categorieNom) {
        return categories.getOrDefault(categorieNom, new Categorie("Vide")).getCours();
    }

    public List<Cours> rechercherCoursParMotCle(String motCle) {
        List<Cours> resultats = new ArrayList<>();
        for (Categorie categorie : categories.values()) {
            for (Cours cours : categorie.getCours()) {
                if (cours.getNom().toLowerCase().contains(motCle.toLowerCase()) ||
                        cours.getDescription().toLowerCase().contains(motCle.toLowerCase())) {
                    resultats.add(cours);
                }
            }
        }
        return resultats;
    }
}


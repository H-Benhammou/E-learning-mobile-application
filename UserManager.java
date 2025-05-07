package com.example.project_dev_mobile;
import java.util.HashMap;
import java.util.Map;

class UserManager {
    private Map<String, User> utilisateurs;

    public UserManager() {
        this.utilisateurs = new HashMap<>();
    }

    public void ajouterUtilisateur(User user) {
        utilisateurs.put(user.getId(), user);
    }

    public User getUtilisateur(String id) {
        return utilisateurs.get(id);
    }

    public void modifierProfil(String userId, String champ, String nouvelleValeur) {
        User user = utilisateurs.get(userId);
        if (user != null) {
            switch (champ.toLowerCase()) {
                case "nom":
                    user.setNom(nouvelleValeur);
                    break;
                case "email":
                    user.setEmail(nouvelleValeur);
                    break;
                case "motdepasse":
                    user.setMotDePasse(nouvelleValeur);
                    break;
                default:
                    System.out.println("Champ non valide");
            }
        }
    }

    public Double getProgression(String etudiantId, String coursId) {
        User user = utilisateurs.get(etudiantId);
        if (user instanceof Etudiant) {
            return ((Etudiant) user).getProgression().getOrDefault(coursId, 0.0);
        }
        return null;
    }

    public void setProgression(String etudiantId, String coursId, Double progression) {
        User user = utilisateurs.get(etudiantId);
        if (user instanceof Etudiant) {
            ((Etudiant) user).setProgression(coursId, progression);
        }
    }
}


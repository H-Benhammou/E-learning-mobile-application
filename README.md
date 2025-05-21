
# Learnify : A mobile application for personalized skill acquisition

## Présentation du projet

**Learnify** est une application mobile éducative conçue pour favoriser l’apprentissage de nouvelles compétences grâce à un écosystème intelligent, interactif et centré sur l’utilisateur. L'application propose des cours diversifiés et interactifs, des quiz adaptés et un assistant conversationnel basé sur l’IA nommé **AskNova**.

Elle s’adresse à trois types d’acteurs principaux, chacun disposant de fonctionnalités spécifiques selon son rôle dans l’écosystème :

---

## 1. Rôles et Fonctionnalités

### 🔧 Administrateur (Admin)

- Gérer les comptes utilisateurs : activation, désactivation, rôles.
- Valider ou supprimer les cours proposés par les éducateurs.
- Modérer les commentaires et les évaluations inappropriées.
- Accéder aux statistiques d'utilisation globales.
- Gérer le contenu global de la plateforme : catégories, paramètres, règles.

### 👨‍🏫 Éducateur (Educator)

- Créer, modifier et supprimer des cours (textes, vidéos, quiz...).
- Répondre aux questions et commentaires des apprenants.
- Gérer les informations de son profil professionnel.

### 👩‍🎓 Apprenant (Learner)

- S’inscrire, se connecter et gérer son profil personnel.
- Explorer le catalogue de cours, s’y inscrire librement.
- Suivre les contenus pédagogiques (vidéos, documents, quiz).
- Évaluer les cours suivis et publier des commentaires.
- Recevoir des recommandations personnalisées.
- Interagir avec **AskNova**, l’assistant intelligent.

---



## 3. Technologies utilisées

### 🎯 Mobile (Android)

- **Java**
- **Firebase Authentication** – gestion des connexions utilisateurs.
- **Cloud Firestore** – stockage de données temps réel.
- **RecyclerView**, **LiveData**, **ViewModel** – UI dynamique.
- **IBM Watson Assistant V2** – chatbot AskNova.

---

## 4. Écrans principaux

- `MainActivity` – point d'entrée de l'application.
- `LoginActivity`, `SignupActivity` – authentification.
- `HomeActivity` – tableau de bord principal.
- `CourseListActivity`, `CourseDetailsActivity` – navigation dans les cours.
- `AddActivity` – ajout de contenu (formateurs).
- `ChatbotActivity` – interface avec AskNova.

---

## 5. Configuration de l’application

### Prérequis

- Android Studio*
- SDK Android API 33+
- Compte Firebase actif
- Accès à IBM Watson Assistant (API Key, Assistant ID, URL)

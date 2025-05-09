// CoursParCategorieActivity.java
// ... imports ...
import com.example.project_dev_mobile.adapters.CoursAdapter; // Ou le nom de ton adaptateur de cours
import com.example.project_dev_mobile.model.Cours;

public class CoursParCategorieActivity extends AppCompatActivity {

    private TextView textViewTitreCategorie; // Pour afficher le nom de la catégorie
    private RecyclerView recyclerViewCoursParCategorie;
    private CoursAdapter coursAdapter; // Assure-toi que c'est le bon nom d'adapter
    private ProgressBar progressBarCoursParCategorie;

    private CatalogueManager catalogueManager;
    private String categorieIdRecu;
    private String categorieNomRecu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cours_par_categorie); // Assure-toi que ce layout existe

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        categorieIdRecu = intent.getStringExtra("CATEGORIE_ID");
        categorieNomRecu = intent.getStringExtra("CATEGORIE_NOM");

        // Vérifier si les données nécessaires sont présentes
        if (categorieIdRecu == null || categorieIdRecu.trim().isEmpty()) {
            Toast.makeText(this, "Erreur: ID de catégorie manquant.", Toast.LENGTH_LONG).show();
            Log.e("CoursParCatActivity", "ID de catégorie manquant dans l'intent.");
            finish(); // Fermer l'activité si l'ID est manquant
            return;
        }

        // Initialiser le gestionnaire de catalogue
        catalogueManager = CatalogueManager.getInstance();

        // Initialiser les vues
        textViewTitreCategorie = findViewById(R.id.textViewTitreCategorie); // ID pour le titre de la catégorie
        if (categorieNomRecu != null) {
            textViewTitreCategorie.setText(categorieNomRecu);
        } else {
            textViewTitreCategorie.setText("Cours de la catégorie"); // Titre par défaut
        }

        progressBarCoursParCategorie = findViewById(R.id.progressBarCoursParCategorie); // ID pour la ProgressBar

        // Configurer RecyclerView pour les cours
        recyclerViewCoursParCategorie = findViewById(R.id.recyclerViewCoursParCategorie); // ID du RecyclerView
        recyclerViewCoursParCategorie.setLayoutManager(new GridLayoutManager(this, 2)); // Ou LinearLayoutManager
        // Initialiser avec une liste vide, sera remplie par le callback
        coursAdapter = new CoursAdapter(new ArrayList<>(), this::onCoursClicked); // Utilise ton adaptateur de cours
        recyclerViewCoursParCategorie.setAdapter(coursAdapter);

        // Charger les cours de la catégorie spécifique
        chargerCoursDeLaCategorie();
    }

    private void chargerCoursDeLaCategorie() {
        Log.d("CoursParCatActivity", "Chargement des cours pour catégorie ID: " + categorieIdRecu);
        progressBarCoursParCategorie.setVisibility(View.VISIBLE);

        catalogueManager.chargerCoursParCategorie(categorieIdRecu, new CatalogueManager.CatalogueCallback() {
            @Override
            public void onCategoriesLoaded(List<com.example.project_dev_mobile.model.Categorie> categories) {
                // Non utilisé dans ce callback spécifique
            }

            @Override
            public void onCoursLoaded(List<Cours> cours) {
                Log.d("CoursParCatActivity", "Cours chargés pour " + categorieNomRecu + ": " + cours.size());
                coursAdapter.setCours(cours); // Méthode dans l'adapter pour mettre à jour la liste
                coursAdapter.notifyDataSetChanged();
                progressBarCoursParCategorie.setVisibility(View.GONE);

                if (cours.isEmpty()) {
                    Toast.makeText(CoursParCategorieActivity.this,
                            "Aucun cours trouvé pour " + categorieNomRecu, Toast.LENGTH_SHORT).show();
                    // Tu pourrais afficher un message "Aucun cours" dans le layout ici
                }
            }

            @Override
            public void onError(Exception e) {
                progressBarCoursParCategorie.setVisibility(View.GONE);
                Log.e("CoursParCatActivity", "Erreur chargement cours pour catégorie " + categorieIdRecu, e);
                Toast.makeText(CoursParCategorieActivity.this,
                        "Erreur chargement des cours: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Gestion du Clic sur un Cours ---
    private void onCoursClicked(Cours cours) {
        Log.d("CoursParCatActivity", "Cours cliqué: " + cours.getTitre() + " (ID: " + cours.getId() + ")");
        Intent intent = new Intent(CoursParCategorieActivity.this, DetailCoursActivity.class);
        intent.putExtra("COURS_ID", cours.getId()); // Passe l'ID du cours à DetailCoursActivity
        startActivity(intent);
    }

    // Optionnel: ajouter un bouton retour dans l'ActionBar
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //    if (item.getItemId() == android.R.id.home) {
    //        onBackPressed(); // Ou finish();
    //        return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    // }
}
package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class DocumentListActivity extends AppCompatActivity {

    private ListView documentListView;
    private Button buttonBack;

    private String[] documentTitles = {
            "Cours Informatique L3 - Sol et Eau",
            "Introduction à la plateforme Android",
            "IHM et composants graphiques",
            "Gestion des menus",
            "ListViews",
            "SQLite"
    };

    private String[] documentUrls = {
            "https://www.univ-chlef.dz/fsnv/wp-content/uploads/cours-informatique-L3-Sol-et-Eau_compressed-1.pdf",
            "https://www.technologuepro.com/cours-developpement-applications-mobiles/chapitre-1-introduction-plateforme-android.html",
            "https://www.technologuepro.com/cours-developpement-applications-mobiles/chapitre-3-ihm-composants-graphiques-plateforme-android.html",
            "https://www.technologuepro.com/cours-developpement-applications-mobiles/chapitre-5-gestion-menus-application-android.html",
            "https://www.technologuepro.com/cours-developpement-applications-mobiles/chapitre-7-listviews.html",
            "https://www.technologuepro.com/cours-developpement-applications-mobiles/chapitre-8-sqlite.html"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        documentListView = findViewById(R.id.document_list);
        buttonBack = findViewById(R.id.button_back);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                documentTitles
        );
        documentListView.setAdapter(adapter);

        documentListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = documentUrls[position];
            Intent intent = new Intent(DocumentListActivity.this, PdfViewActivity.class);
            intent.putExtra("pdf_url", url);
            startActivity(intent);
        });

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(DocumentListActivity.this, CourseTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

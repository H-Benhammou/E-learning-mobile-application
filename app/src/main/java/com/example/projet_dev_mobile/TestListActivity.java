package com.example.projet_dev_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class TestListActivity extends AppCompatActivity {

    private ListView testListView;
    private Button buttonBack;

    private String[] testTitles = {
            "Test 1 - Base de Données",
            "Test 2 - Java avancé",
            "Test 3 - Développement Android",
            "Test 4 - Développement WEB",
            "Test 5 - Réseaux informatiques"
    };

    private String[] testUrls = {
            "http://jennsenfire.free.fr/Cours%20Fac/L2S3/Informatique/Dossier%20Prof/L3%20AES%20EM%20INFORMATIQUE%20BD%202012%202013/ENONCE%20TD%20L3G%20L3EM.pdf",
            "https://www.exemple.com/test2.pdf",
            "https://www.exemple.com/test3.pdf",
            "https://www.exemple.com/test4.pdf",
            "https://www.exemple.com/test5.pdf"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        testListView = findViewById(R.id.test_list);
        buttonBack = findViewById(R.id.button_back);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                testTitles
        );
        testListView.setAdapter(adapter);

        testListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = testUrls[position];
            Intent intent = new Intent(TestListActivity.this, PdfViewActivity.class);
            intent.putExtra("pdf_url", url);
            startActivity(intent);
        });

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(TestListActivity.this, CourseTrackingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

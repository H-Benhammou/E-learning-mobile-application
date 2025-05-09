package com.example.projet_dev_mobile;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class DocumentViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        String fileName = getIntent().getStringExtra("pdf_file");
        if (fileName != null) {
            // Charger le PDF depuis assets en utilisant Google Docs Viewer
            String url = "file:///android_asset/" + fileName;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            // Google Docs viewer pour afficher les PDF sans dépendance
            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" +
                    "file:///android_asset/" + fileName;
            webView.loadUrl(googleDocsUrl);
        }
    }
}

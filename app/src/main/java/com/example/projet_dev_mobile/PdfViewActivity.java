package com.example.projet_dev_mobile;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PdfViewActivity extends AppCompatActivity {

    private WebView pdfWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfWebView = findViewById(R.id.pdf_webview);

        WebSettings webSettings = pdfWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        pdfWebView.setWebViewClient(new WebViewClient());

        String pdfUrl = getIntent().getStringExtra("pdf_url");

        String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;

        pdfWebView.loadUrl(googleDocsUrl);
    }
}

package com.example.beaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class news extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        webView=findViewById(R.id.news);
        this.webView = (WebView) findViewById(R.id.news);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClient webViewClient=new WebViewClient();
        webView.setWebViewClient(webViewClient);

        webView.loadUrl("https://www.nmiet.edu.in/news.php");
    }
}

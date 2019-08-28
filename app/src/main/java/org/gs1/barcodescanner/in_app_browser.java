package org.gs1.barcodescanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class in_app_browser extends AppCompatActivity {

    private WebView web;

    static String search_gtin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_app_browser);

        //Opens a in app browser version of resolver
        //this is the same page that would if someone went to resolvers product page on the computer
        web = (WebView) findViewById(R.id.webView);
        web.getSettings().setLoadsImagesAutomatically(true);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.loadUrl("https://id.gs1.org/gtin/" + search_gtin + "?linkType=all");//builds resolver url
    }
}

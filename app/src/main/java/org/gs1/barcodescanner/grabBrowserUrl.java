package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


public class grabBrowserUrl extends AppCompatActivity {

    private WebView web;
    String current_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_browser_url);

        web = (WebView) findViewById(R.id.webView);
        web.getSettings().setLoadsImagesAutomatically(true);

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);


        web.loadUrl("https://www.google.com/");
        web.setWebViewClient(new WebViewClient());

        Button btn_geturl;
        btn_geturl = findViewById(R.id.btn_geturl);
        btn_geturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_url = web.getUrl();
                Toast toast = Toast.makeText(getApplicationContext(),current_url, Toast.LENGTH_SHORT);
                toast.show();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.web_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_back:
                onBackPressed();
                break;

            case R.id.menu_forward:
                onForwardPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed(){
        if(web.canGoForward()){
            web.goForward();
        } else {
            Toast.makeText(this, "Can't go further", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(web.canGoBack()){
            web.goBack();
        }
        else{
            finish();
        }
    }
}

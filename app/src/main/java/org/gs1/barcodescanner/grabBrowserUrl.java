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
    static String current_url;
    String sid;
    String new_uri;
    String link;
    String gtin;
    String item_description;
    String attribute_name;
    String uri_response_id;
    String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_browser_url);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        new_uri = intent.getStringExtra("new_uri");
        product_id = intent.getStringExtra("product_id");
        link = intent.getStringExtra("link");
        gtin = intent.getStringExtra("gtin");
        item_description = intent.getStringExtra("item_description");
        attribute_name = intent.getStringExtra("attribute_name");
        uri_response_id = intent.getStringExtra("uri_response_id");

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

                Intent mIntent = getIntent();
                String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");
                if (previousActivity.equals("A")) {
                    Intent intent = new Intent(getApplicationContext(), addProductPage1.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("new_uri", new_uri);
                    intent.putExtra("gtin", gtin);
                    intent.putExtra("item_description", item_description);
                    startActivity(intent);
                }
                if(previousActivity.equals("editLinkPage")){
                    Intent intent = new Intent(getApplicationContext(), editLinkPage.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("link", current_url);
                    intent.putExtra("attribute_name", attribute_name);
                    intent.putExtra("uri_response_id", uri_response_id);
                    startActivity(intent);
                }
                if(previousActivity.equals("addNewLink")){
                    Intent intent = new Intent(getApplicationContext(), addNewLink.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("link", current_url);
                    intent.putExtra("product_id", product_id);
                    intent.putExtra("attribute_name", attribute_name);
                    intent.putExtra("uri_response_id", uri_response_id);
                    startActivity(intent);
                }
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

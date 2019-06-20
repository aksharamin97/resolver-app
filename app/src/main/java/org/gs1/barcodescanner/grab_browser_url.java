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


public class grab_browser_url extends AppCompatActivity {

    private WebView web;
    static String current_url;
    String sid;
    String link;
    String gtin;
    String alt_attribute_name;
    String uri_response_id;
    String product_name;
    String uri_request_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grab_browser_url);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        link = intent.getStringExtra("link");
        gtin = intent.getStringExtra("gtin");
        alt_attribute_name = intent.getStringExtra("alt_attribute_name");
        uri_response_id = intent.getStringExtra("uri_response_id");


        product_name = intent.getStringExtra("product_name");
        uri_request_id = intent.getStringExtra("uri_request_id");


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
                if (previousActivity.equals("add_new_product_page2")) {
                    Intent intent = new Intent(getApplicationContext(), add_new_product_page2.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("uri_request_id", uri_request_id);
                    intent.putExtra("gtin", gtin);
                    intent.putExtra("product_name", product_name);
                    intent.putExtra("alt_attribute_name", alt_attribute_name);
                    startActivity(intent);
                }
                if (previousActivity.equals("edit_link_page")) {
                    Intent intent = new Intent(getApplicationContext(), edit_link_page.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("link", current_url);
                    intent.putExtra("alt_attribute_name", alt_attribute_name);
                    intent.putExtra("uri_response_id", uri_response_id);
                    startActivity(intent);
                }
                if (previousActivity.equals("add_new_link_page")) {
                    Intent intent = new Intent(getApplicationContext(), add_new_link_page.class);
                    intent.putExtra("sid", sid);
                    intent.putExtra("link", current_url);
                    intent.putExtra("alt_attribute_name", alt_attribute_name);
                    intent.putExtra("uri_request_id", uri_request_id);
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

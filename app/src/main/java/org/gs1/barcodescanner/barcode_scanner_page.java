package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class barcode_scanner_page extends AppCompatActivity implements ZXingScannerView.ResultHandler {

//Barcode scanner api

    ZXingScannerView ScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);


    }

    @Override
    public void handleResult(Result result) {
        Intent mIntent = getIntent();
        String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");//Tracks previous activity
        //Brings user to landing page after scanning barcode
        //if previous activity was main page then go to consumer landing page
        if (previousActivity.equals("main_page")) {
            in_app_browser.search_gtin = result.getText();
            Intent intent = new Intent(getApplicationContext(), consumer_landing_page.class);
            startActivity(intent);
            onBackPressed();
        }

        //Next to if statements are similar. Allows product owner to get gtin for products by scanning barcode when creating or editing a product
        //if previous activity was add new product page 1 go back to add product page one
        if (previousActivity.equals("add_new_product_page1")){
            Intent mintent = getIntent();
            String sid = mintent.getStringExtra("sid");
            String uri_request_id = mintent.getStringExtra("uri_request_id");

            //passes the scanned GTIN as an intent so it can be used in add new product page 1
            Intent intent = new Intent(getApplicationContext(), add_new_product_page1.class);
            intent.putExtra("sid", sid);
            intent.putExtra("uri_request_id", uri_request_id);
            intent.putExtra("GTIN", result.getText());
            startActivity(intent);
            onBackPressed();
        }
        //if previous activity was edit product info page go back to edit product info page
        if(previousActivity.equals("edit_product_info_page")){
            Intent mintent = getIntent();
            String sid = mintent.getStringExtra("sid");
            String product_name = mintent.getStringExtra("product_name");
            String uri_request_id = mintent.getStringExtra("uri_request_id");

            //passes the scanned GTIN as an intent so it can be used in edit product info page
            Intent intent = new Intent(getApplicationContext(), edit_product_info_page.class);
            intent.putExtra("sid", sid);
            intent.putExtra("gtin", result.getText());
            intent.putExtra("product_name", product_name);
            intent.putExtra("uri_request_id", uri_request_id);
            startActivity(intent);
            onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}

package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class barcode_scanner_page extends AppCompatActivity implements ZXingScannerView.ResultHandler {



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
        String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity.equals("main_page")) {
            in_app_browser.search_gtin = result.getText();
            Intent intent = new Intent(getApplicationContext(), consumer_landing_page.class);
            startActivity(intent);
            onBackPressed();
        }
        else{
            add_new_product_page1.scannedGTIN = result.getText();
            Intent intent = new Intent(getApplicationContext(), add_new_product_page1.class);
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

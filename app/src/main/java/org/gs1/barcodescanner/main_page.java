package org.gs1.barcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class main_page extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

//////////////////////////////////////////////////////////////////////////////////////////////////////
        //Scan a Product Button
        RelativeLayout btn_scan_product;
        btn_scan_product = findViewById(R.id.btn_scan_product);

        //Checks for camera permissions
        //If the permission is not  already granted then ask,if it is already granted then just do nothing for the permission section
        if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            //After this function call,it will ask for permission and whether it granted or not,this response is handle in onRequestPermissionsResult() which we overrided.
        }

        //When button is pressed go to barcode scanner page
        btn_scan_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(Permission is granted) else-> request
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), barcode_scanner_page.class);
                    intent.putExtra("FROM_ACTIVITY", "main_page");
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////
        //Log A Product Button
        RelativeLayout btn_log_product;
        btn_log_product = findViewById(R.id.btn_log_product);

        btn_log_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If page is not previously logged go to login page
                if (login_page.session_id.equals("") || dashboard.jsonString.equals(""))
                    startActivity(new Intent(getApplicationContext(), login_page.class));
                    //Else go directly to dashboard. sid is carried in order to get to dashboard
                else {
                    Intent intent = new Intent(getApplicationContext(), dashboard.class);
                    intent.putExtra("sid", login_page.session_id);
                    startActivity(intent);
                }
            }
        });


//////////////////////////////////////////////////////////////////////////////////////////////////////
        //GTIN search bar
        final EditText editText_search_gtin;
        editText_search_gtin = findViewById(R.id.editText_search_gtin);

        //GTIN is searched when enter button is pressed. OnKeyListener and is waiting for Key.KEYCODE_ENTER
        //Once enter is pressed GTIN is taken and sent to consumer landing page
        editText_search_gtin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        in_app_browser.search_gtin = editText_search_gtin.getText().toString();
                        startActivity(new Intent(getApplicationContext(), consumer_landing_page.class));
                        return true;
                    }
                }
                return false;
            }
        });
    }

    //Disables back button
    @Override
    public void onBackPressed() {
    }

}

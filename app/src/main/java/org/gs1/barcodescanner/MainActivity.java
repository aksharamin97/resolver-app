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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout btn_scan;
        btn_scan = findViewById(R.id.btn_scan);

        //If the permission is not  already granted then ask,if it is already granted then just do nothing for the permission section
        if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            //After this function call,it will ask for permission and whether it granted or not,this response is handle in onRequestPermissionsResult() which we overrided.
        }


        btn_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //if(Permission is granted) else-> request
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }
        });

        RelativeLayout btn_log;
        btn_log = findViewById(R.id.btn_log);

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginActivity.session_id.compareTo("") == 0 || dashboard.jsonString.compareTo("") == 0)
                    startActivity(new Intent(getApplicationContext(), loginActivity.class));
                else {
                    Intent intent = new Intent(getApplicationContext(), dashboard.class);
                    intent.putExtra("sid", loginActivity.session_id);
                    startActivity(intent);
                }
            }
        });
        final EditText editText;
        editText = findViewById(R.id.editText);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        mybrowser.gtin = editText.getText().toString();
                        startActivity(new Intent(getApplicationContext(), landingPage.class));
                        return true;
                    }
                }
                return false;
            }
        });
    }
}

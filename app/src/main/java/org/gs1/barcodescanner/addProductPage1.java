package org.gs1.barcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addProductPage1 extends AppCompatActivity {

    Button btn_prev;
    Button btn_next;
    EditText gtin;

    Button getLink;
    EditText link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page1);

//        btn_next  = (Button) findViewById(R.id.btn_next);
//
//        btn_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), addProductPage.class));
//            }
//        });

        Button btn = (Button)findViewById(R.id.get_link);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String test = gtin.getText().toString();
                startActivity(new Intent(getApplicationContext(), grabBrowserUrl.class));

            }
        });


        link = (EditText)findViewById(R.id.link);
        link.setText(grabBrowserUrl.current_url);
    }
}
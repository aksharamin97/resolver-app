package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addProductPage1 extends AppCompatActivity {

    Button btn_save;
    Button get_link;
    Button btn_add;
    Button btn_live;

    EditText link;
    EditText alt_attribute_name;

    String url = "https://data.gs1.org/api/api.php";
    String sid;
    String new_uri;
    String gtin;
    String item_description;

    JSONObject body1;
    JSONObject body2;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page1);
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        new_uri = intent.getStringExtra("new_uri");
        gtin = intent.getStringExtra("gtin");
        item_description = intent.getStringExtra("item_description");
        link = (EditText)findViewById(R.id.link);
        alt_attribute_name = (EditText)findViewById(R.id.alt_attribute_name);


        btn_save  = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                call(sid, new_uri, link, alt_attribute_name);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });


        btn_live = (Button)findViewById(R.id.btn_golive);
        btn_live.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                call(sid, new_uri, link, alt_attribute_name);

                body2 = new JSONObject();
                try {
                    body2.put("command", "save_existing_uri_request");
                    body2.put("session_id", sid);
                    body2.put("uri_request_id", new_uri);
                    body2.put("alpha_code", "gtin");
                    body2.put("alpha_value", gtin);
                    body2.put("item_description", item_description);
                    body2.put("include_in_sitemap", "1");
                    body2.put("active", "1");
                    body2.put("uri_prefix_1", "");
                    body2.put("uri_suffix_1", "");
                    body2.put("uri_prefix_2", "");
                    body2.put("uri_suffix_2", "");
                    body2.put("uri_prefix_3", "");
                    body2.put("uri_suffix_3", "");
                    body2.put("uri_prefix_4", "");
                    body2.put("uri_suffix_4", "");
                } catch (JSONException e) {
                    Log.d("OKHTTP3", "JSON Exception");
                    e.printStackTrace();
                }
                RequestBody req_body2 = RequestBody.create(JSON, body2.toString());
                Request request2 = new Request.Builder()
                        .url(url)
                        .post(req_body2)
                        .build();
                client.newCall(request2).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        System.out.println("Call 2 Error");
                    }
//
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            final String jsonString3 = response.body().string();
                            addProductPage1.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Product Active", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                intent.putExtra("new_uri", new_uri);
                startActivity(intent);
            }
        });


        btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(sid, new_uri, link, alt_attribute_name);
                Intent intent = new Intent(getApplicationContext(), addProductPage1.class);
                intent.putExtra("sid", sid);
                intent.putExtra("new_uri", new_uri);
                startActivity(intent);
            }
        });


        get_link = (Button)findViewById(R.id.get_link);
        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grabBrowserUrl.class);
                intent.putExtra("sid", sid);
                intent.putExtra("new_uri", new_uri);
                intent.putExtra("gtin", gtin);
                intent.putExtra("item_description", item_description);
                startActivity(intent);

            }
        });


//        link = (EditText)findViewById(R.id.link);
        link.setText(grabBrowserUrl.current_url);

//        ImageView logo;
//        logo = (ImageView)findViewById(R.id.banner_logo);
//        logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void call(String sid, String new_uri, EditText link, EditText alt_attribute_name) {
        try {
            body1 = new JSONObject();
            body1.put("command", "save_new_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_request_id", new_uri);
            body1.put("attribute_id", "1");
            body1.put("iana_language", "en");
            body1.put("destination_uri", link.getText().toString());
            body1.put("default_uri", "1");
            body1.put("alt_attribute_name", alt_attribute_name.getText().toString());
            body1.put("active_start_date", "");
            body1.put("active_end_date", "");
            body1.put("forward_request_querystrings", "1");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        RequestBody req_body1 = RequestBody.create(JSON, body1.toString());
        Request request1 = new Request.Builder()
                .url(url)
                .post(req_body1)
                .build();
//
        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("Call 1 Error");
            }
            //
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String jsonString1 = response.body().string();
                    System.out.println(jsonString1);

                    addProductPage1.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(),"Link Added", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
//                            else{
//                                addProductPage.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Intent intent = new Intent(getApplicationContext(), dashboard.class);
//                                        intent.putExtra("sid", sid);
//                                        startActivity(intent);
//                                    }
//                                });
//                            }
                }
            }
        });
    }

}
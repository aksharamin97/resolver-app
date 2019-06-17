package org.gs1.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class addProductPage extends AppCompatActivity {
    String sid;
    EditText gtin;
    EditText item_description;
    JSONObject body;
    String url = "https://data.gs1.org/api/api.php";
    OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    JSONObject body1;
    JSONObject body2;
    JSONObject body3;
    String new_uri;

    Button getLink;
    EditText link;
    Toast toast;

    //    static Integer new_product_id;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_page);

        Intent intent = getIntent();
//        String last_product_id = intent.getStringExtra("last_product_id");

        sid = intent.getStringExtra("sid");
//        new_product_id = Integer.parseInt(last_product_id) + 1;
//        System.out.println("product_id =   " + new_product_id);

        item_description = (EditText) findViewById(R.id.product_name);
        gtin = (EditText) findViewById(R.id.gtin);

        Button save = (Button) findViewById(R.id.btn_next);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gtin.getText().toString().compareTo("") != 0 && item_description.getText().toString().compareTo("") != 0) {

                    body1 = new JSONObject();
                    try {
                        body1.put("command", "new_uri_request");
                        body1.put("session_id", sid);

                    } catch (JSONException e) {
                        Log.d("OKHTTP3", "JSON Exception");
                        e.printStackTrace();
                    }
                    RequestBody req_body1 = RequestBody.create(JSON, body1.toString());
                    Request request1 = new Request.Builder()
                            .url(url)
                            .post(req_body1)
                            .build();

                    client.newCall(request1).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            System.out.println("Call 1 Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String jsonString1 = response.body().string();
                                System.out.println("new_uri_request:   " + jsonString1);
//                            System.out.println(jsonString1.getClass().getName());
                                try {
                                    JSONObject jsonObject = new JSONObject(jsonString1);
                                    new_uri = jsonObject.getString("new_uri_request_id");
                                    System.out.println(new_uri);

                                } catch (JSONException e) {
                                    System.out.println("error in json obj");
                                }
                                System.out.println("outside");

                                body2 = new JSONObject();
                                try {
                                    body2.put("command", "save_existing_uri_request");
                                    body2.put("session_id", sid);
                                    body2.put("uri_request_id", new_uri);
                                    body2.put("alpha_code", "gtin");
                                    body2.put("alpha_value", gtin.getText().toString());
                                    body2.put("item_description", item_description.getText().toString());
                                    body2.put("include_in_sitemap", "1");
                                    body2.put("active", "0");
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

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            final String jsonString3 = response.body().string();
                                            System.out.println("save_existing_uri_request:   " + jsonString3);

                                        }
                                    }
                                });

                                addProductPage.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Product Added", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(getApplicationContext(), addProductPage1.class);
                                        intent.putExtra("sid", sid);
                                        startActivity(intent);
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
/////////////////////////////////////////////////////////////////////////////////////////
//                body2 = new JSONObject();
//                try {
//                    body2.put("command", "get_request_uri_data");
//                    body2.put("session_id", sid);
//                    body2.put("uri_request_id", new_product_id);
//                } catch (JSONException e) {
//                    Log.d("OKHTTP3", "JSON Exception");
//                    e.printStackTrace();
//                }
//                RequestBody req_body2 = RequestBody.create(JSON, body2.toString());
//                Request request2 = new Request.Builder()
//                        .url(url)
//                        .post(req_body2)
//                        .build();
//
//                client.newCall(request2).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                        System.out.println("Call 2 Error");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()){
//                            final String jsonString2 = response.body().string();
//                            System.out.println("get_request_uri_data:   " + jsonString2);
//
//                            addProductPage.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast toast = Toast.makeText(getApplicationContext(),"Request URI Successful", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//                            });
////                            else{
////                                addProductPage.this.runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        Intent intent = new Intent(getApplicationContext(), dashboard.class);
////                                        intent.putExtra("sid", sid);
////                                        startActivity(intent);
////                                    }
////                                });
////                            }
//                        }
//                    }
//                });
//
///////////////////////////////////////////////////////////////////////////////////////////


//                body3 = new JSONObject();
//                try {
//                    body3.put("command", "save_existing_uri_request");
//                    body3.put("session_id", sid);
//                    body3.put("uri_request_id", new_uri);
//                    body3.put("alpha_code", "gtin");
//                    body3.put("alpha_value", gtin.getText().toString());
//                    body3.put("item_description", item_description.getText().toString());
//                    body3.put("include_in_sitemap", "1");
//                    body3.put("active", "0");
//                    body3.put("uri_prefix_1", "");
//                    body3.put("uri_suffix_1", "");
//                    body3.put("uri_prefix_2", "");
//                    body3.put("uri_suffix_2", "");
//                    body3.put("uri_prefix_3", "");
//                    body3.put("uri_suffix_3", "");
//                    body3.put("uri_prefix_4", "");
//                    body3.put("uri_suffix_4", "");
//                } catch (JSONException e) {
//                    Log.d("OKHTTP3", "JSON Exception");
//                    e.printStackTrace();
//                }
//                RequestBody req_body3 = RequestBody.create(JSON, body3.toString());
//                Request request3 = new Request.Builder()
//                        .url(url)
//                        .post(req_body3)
//                        .build();
//
//                client.newCall(request3).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                        System.out.println("Call 3 Error");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()){
//                            final String jsonString3 = response.body().string();
//                            System.out.println("save_existing_uri_request:   " + jsonString3);
//
////                            addProductPage.this.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast toast = Toast.makeText(getApplicationContext(),"Saving URI Successful", Toast.LENGTH_SHORT);
////                                    toast.show();
////                                }
////                            });
////                            else{
////                                addProductPage.this.runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        Intent intent = new Intent(getApplicationContext(), dashboard.class);
////                                        intent.putExtra("sid", sid);
////                                        startActivity(intent);
////                                    }
////                                });
////                            }
//                        }
//                    }
//                });


//                final MediaType JSON = MediaType.parse("application/json charset=utf-8");
//                body = new JSONObject();
//                try {
//                    body.put("command", "save_existing_uri_request");
//                    body.put("session_id", sid);
//                    body.put("uri_request_id", new_product_id.toString());
//                    body.put("alpha_code", "gtin");
//                    body.put("alpha_value", gtin.getText().toString());
//                    body.put("item_description", item_description.getText().toString());
//                    body.put("include_in_sitemap", "1");
//                    body.put("active", "0");
//                    body.put("uri_prefix_1", "");
//                    body.put("uri_suffix_1", "");
//                    body.put("uri_prefix_2", "");
//                    body.put("uri_suffix_2", "");
//                    body.put("uri_prefix_3", "");
//                    body.put("uri_suffix_3", "");
//                    body.put("uri_prefix_4", "");
//                    body.put("uri_suffix_4", "");
//
//                } catch (JSONException e) {
//                    Log.d("OKHTTP3", "JSON Exception");
//                    e.printStackTrace();
//                }
//
//                RequestBody req_body = RequestBody.create(JSON, body.toString());
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(req_body)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                        System.out.println("API call failed");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            System.out.println(body.toString());
//                            System.out.println(response.body().string());
//
////                            Toast toast = Toast.makeText(getApplicationContext(),"Product Added", Toast.LENGTH_SHORT);
////                            toast.show();
//                            Intent intent = new Intent(getApplicationContext(), dashboard.class);
//                            startActivity(intent);
//                        }
//                    }
//                });
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Missing Credentials", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        ImageView logo;
        logo = (ImageView) findViewById(R.id.banner_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

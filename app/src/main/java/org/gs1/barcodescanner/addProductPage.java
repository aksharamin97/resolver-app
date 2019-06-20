package org.gs1.barcodescanner;

import android.annotation.SuppressLint;
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


public class addProductPage extends AppCompatActivity {
    String sid;
    String GTIN;
    String name;
    String product_id;

    EditText gtin;
    static String scannedGTIN;
    EditText item_description;
    String url = "https://data.gs1.org/api/api.php";
    OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    JSONObject body1;
    JSONObject body2;
    JSONObject body3;

    String new_uri;

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
        GTIN = intent.getStringExtra("GTIN");
        name = intent.getStringExtra("name");
        product_id = intent.getStringExtra("product_id");
        System.out.println(GTIN);
        System.out.println(name);
//        System.out.println("page 1:   " + sid);
//        new_product_id = Integer.parseInt(last_product_id) + 1;
//        System.out.println("product_id =   " + new_product_id);

        item_description = (EditText) findViewById(R.id.product_name);
        gtin = (EditText) findViewById(R.id.gtin);
        gtin.setText(scannedGTIN);

        item_description.setText(name);
        gtin.setText(GTIN);


        Button next = (Button) findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gtin.getText().toString().equals("") || item_description.getText().toString().equals("")) {

                    toast = Toast.makeText(getApplicationContext(), "Missing Credentials", Toast.LENGTH_SHORT);
                    toast.show();
                }

                else {
                    toast = Toast.makeText(getApplicationContext(), "Missing Credentials", Toast.LENGTH_SHORT);
                    toast.show();

                    if (checkDigit(gtin.getText().toString())) {

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
//                                System.out.println("new_uri_request:   " + jsonString1);
//                            System.out.println(jsonString1.getClass().getName());
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString1);
                                        new_uri = jsonObject.getString("new_uri_request_id");
//                                    System.out.println(new_uri);

                                    } catch (JSONException e) {
                                        System.out.println("error in json obj");
                                    }
//                                System.out.println("outside");

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
//                                            System.out.println("save_existing_uri_request:   " + jsonString3);

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
                                            intent.putExtra("new_uri", new_uri);
                                            intent.putExtra("gtin", gtin.getText().toString());
                                            intent.putExtra("item_description", item_description.getText().toString());
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
/////////////////////////////////////////////////////////////////////////////////////////
//API CALL TEMPLATE
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
                    }
                }
            }//end of onclick
        });//end of set on click listener





        Button scanGTIN = (Button)findViewById(R.id.scanGTIN);
        scanGTIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanCodeActivity.class);
                intent.putExtra("FROM_ACTIVITY", "B");
                startActivity(intent);
            }
        });

        scannedGTIN = "";

    }

    private boolean checkDigit(String gtin) {
        int odds = 0;
        int evens = 0;
        char num;
        int sum;
        int nearestTen;
        int rem;
        int checkDigit;
        boolean pass;


        for(int i = 0; i<gtin.length() -1; i++){
            if(i%2 == 0){
                num = gtin.charAt(i);
                odds += (num - '0') * 3;
            }
            else{
                num = gtin.charAt(i);
                evens += (num - '0') * 1;
            }
        }
        sum = odds + evens;
        rem = sum%10;
        nearestTen =  (sum - rem)+10;
        checkDigit = nearestTen -  sum;
        if(checkDigit == 10) {
            checkDigit = 0;
        }

        if(Integer.parseInt(gtin.substring(gtin.length()-1)) == checkDigit)
            return pass = true;
        else {
            toast = Toast.makeText(getApplicationContext(), "Invalid check digit. Digit should be " + checkDigit+ "", Toast.LENGTH_SHORT);
            toast.show();
            return pass = false;
        }

    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), dashboard.class);
        intent.putExtra("sid", loginActivity.session_id);
        startActivity(intent);
    }
}

package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class product_page extends AppCompatActivity {


    JSONArray json_array;
    String link;
    String attribute_type;
    ArrayList<HashMap<String, String>> product_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String gtin = intent.getStringExtra("gtin");
        String active = intent.getStringExtra("active");
        final String product_id = intent.getStringExtra("product_id");
        String sid = intent.getStringExtra("sid");
        String last_product_id = intent.getStringExtra("last_product_id");



//        TextView t_name = (TextView)findViewById(R.id.name);
//        t_name.setText(name);
//        TextView t_gtin = (TextView)findViewById(R.id.gtin);
//        t_gtin.setText(gtin);
//        TextView t_active = (TextView)findViewById(R.id.active);
//        t_active.setText(active);
//        TextView t_pid = (TextView)findViewById(R.id.product_id);
//        t_pid.setText(product_id);



        OkHttpClient client = new OkHttpClient();

        final MediaType JSON = MediaType.parse("application/json charset=utf-8");
        JSONObject body = new JSONObject();
        try {
            body.put("command", "get_response_uri_data");
            body.put("session_id", sid);
            body.put("uri_request_id", product_id);
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        final String url = "https://data.gs1.org/api/api.php";
        RequestBody req_body = RequestBody.create(JSON, body.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(req_body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonString = response.body().string();
                    try{
                        json_array = new JSONArray(jsonString);
                        final HashMap<String, String> product = new HashMap<>();
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            link = jsonobject.getString("destination_uri");
                            attribute_type =  jsonobject.getString("alt_attribute_name");

//                            product.put("name", name);
                            product.put(link, attribute_type);
//                            System.out.println(product);
//                            product_list.add(product);
                        }
//                        System.out.println(product);
                        final TableLayout tl = (TableLayout)findViewById(R.id.tl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (String i : product.keySet()) {
                                    TableRow row = new TableRow(product_page.this);
                                    TextView tv_type = new TextView(product_page.this);
                                    TextView tv_link = new TextView(product_page.this);
//                                    System.out.println(i);
//                                    System.out.println(product.get(i));
                                    tv_type.setText(product.get(i));
                                    tv_link.setText(i);
                                    row.addView(tv_type);
                                    row.addView(tv_link);
                                    tl.addView(row);
                                }
                        }
                        });
//
                    }
                    catch (JSONException e) {
                        System.out.println("FAIL" + e);
                    }

                }
            }
        });
    }
}

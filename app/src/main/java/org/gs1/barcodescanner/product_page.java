package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
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

public class product_page extends AppCompatActivity {


    JSONArray json_array;
    String link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String gtin = intent.getStringExtra("gtin");
        String active = intent.getStringExtra("active");
        String product_id = intent.getStringExtra("product_id");
        String sid = intent.getStringExtra("sid");


        TextView t_name = (TextView)findViewById(R.id.name);
        t_name.setText(name);
        TextView t_gtin = (TextView)findViewById(R.id.gtin);
        t_gtin.setText(gtin);
        TextView t_active = (TextView)findViewById(R.id.active);
        t_active.setText(active);
        TextView t_pid = (TextView)findViewById(R.id.product_id);
        t_pid.setText(product_id);



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
//                        System.out.println(json_array);
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
//                            System.out.println(jsonobject);
                            link = jsonobject.getString("destination_uri");
                            System.out.println(link);
                        }
                    }
                    catch (JSONException e) {
                        System.out.println("FAIL" + e);
                    }

                }
            }
        });
    }
}

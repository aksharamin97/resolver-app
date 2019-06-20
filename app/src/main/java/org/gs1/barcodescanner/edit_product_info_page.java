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

public class edit_product_info_page extends AppCompatActivity {

    JSONObject body1;
    String url = "https://data.gs1.org/api/api.php";
    OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.parse("application/json charset=utf-8");

    Toast toast;

    Button scanGTIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product_info_page);

        Intent intent = getIntent();
        final String product_name = intent.getStringExtra("product_name");
        final String gtin = intent.getStringExtra("gtin");
        final String uri_request_id = intent.getStringExtra("uri_request_id");
        final String sid = intent.getStringExtra("sid");

        final EditText GTIN = (EditText)findViewById(R.id.gtin);
        final EditText item_description = (EditText)findViewById(R.id.product_name);

        GTIN.setText(gtin);
        item_description.setText(product_name);

        Button save = (Button)findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(GTIN.getText().toString().substring(gtin.length() - 1)) == add_new_product_page1.checkDigit(GTIN.getText().toString())){
                    body1 = new JSONObject();
                try {
                    body1.put("command", "save_existing_uri_request");
                    body1.put("session_id", sid);
                    body1.put("uri_request_id", uri_request_id);
                    body1.put("alpha_code", "gtin");
                    body1.put("alpha_value", GTIN.getText().toString());
                    body1.put("item_description", item_description.getText().toString());
                    body1.put("include_in_sitemap", "1");
                    body1.put("active", "0");
                    body1.put("uri_prefix_1", "");
                    body1.put("uri_suffix_1", "");
                    body1.put("uri_prefix_2", "");
                    body1.put("uri_suffix_2", "");
                    body1.put("uri_prefix_3", "");
                    body1.put("uri_suffix_3", "");
                    body1.put("uri_prefix_4", "");
                    body1.put("uri_suffix_4", "");
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
                            final String jsonString3 = response.body().string();
                            System.out.println(jsonString3);
                            edit_product_info_page.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Product Saved", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Intent intent = new Intent(getApplicationContext(), dashboard.class);
                                    intent.putExtra("sid", sid);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
              }
                else {
                    toast = Toast.makeText(getApplicationContext(), "Invalid check digit. Digit should be " + add_new_product_page1.checkDigit(GTIN.getText().toString())+ "", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        scanGTIN = (Button)findViewById(R.id.scanGTIN);
        scanGTIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), barcode_scanner_page.class);
                intent.putExtra("product_name", product_name);
                intent.putExtra("uri_request_id", uri_request_id);
                intent.putExtra("sid", sid);
                intent.putExtra("FROM_ACTIVITY", "edit_product_info_page");
                startActivity(intent);
            }
        });

    }
}

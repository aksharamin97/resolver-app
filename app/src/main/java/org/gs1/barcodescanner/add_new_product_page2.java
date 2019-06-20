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

public class add_new_product_page2 extends AppCompatActivity {

    Button btn_save;
    Button get_link;
    Button btn_add;
    String product_name;
    EditText link;
    EditText add_alt_attribute_name;
    String url = "https://data.gs1.org/api/api.php";
    String sid;
    String uri_request_id;
    String gtin;
    String alt_attribute_name;
    JSONObject body1;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_product_page2);
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        uri_request_id = intent.getStringExtra("uri_request_id");
        gtin = intent.getStringExtra("gtin");
        product_name = intent.getStringExtra("product_name");
        alt_attribute_name = intent.getStringExtra("alt_attribute_name");
        link = (EditText)findViewById(R.id.link);

        System.out.println("id   " + uri_request_id);
        System.out.println("gtin   " + gtin);
        System.out.println("product_name   " + product_name);
        System.out.println("sid   " + sid);

        add_alt_attribute_name = (EditText)findViewById(R.id.alt_attribute_name);
        add_alt_attribute_name.setText(alt_attribute_name);

        btn_save  = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK  "+link.getText().toString());
                System.out.println("Attribute ALT    " +add_alt_attribute_name.getText().toString());
                link_call(sid, uri_request_id, link, add_alt_attribute_name);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });


        btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link_call(sid, uri_request_id, link, add_alt_attribute_name);
                Intent intent = new Intent(getApplicationContext(), add_new_product_page2.class);
                intent.putExtra("sid", sid);
                intent.putExtra("uri_request_id", uri_request_id);
                startActivity(intent);
            }
        });


        get_link = (Button)findViewById(R.id.get_link);
        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grab_browser_url.class);
                intent.putExtra("sid", sid);
                intent.putExtra("uri_request_id", uri_request_id);
                intent.putExtra("gtin", gtin);
                intent.putExtra("product_name", product_name);
                intent.putExtra("alt_attribute_name",add_alt_attribute_name.getText().toString());
                intent.putExtra("FROM_ACTIVITY", "add_new_product_page2");
                startActivity(intent);
            }
        });


        link.setText(grab_browser_url.current_url);
        grab_browser_url.current_url = "";

    }

    private void link_call(String sid, String uri_request_id, EditText link, EditText add_alt_attribute_name) {
        try {
            body1 = new JSONObject();
            body1.put("command", "save_new_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_request_id", uri_request_id);
            body1.put("attribute_id", "1");
            body1.put("iana_language", "en");
            body1.put("destination_uri", link.getText().toString());
            body1.put("default_uri", "1");
            body1.put("alt_attribute_name", add_alt_attribute_name.getText().toString());
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

                    add_new_product_page2.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(),"Link Added", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                }
            }
        });
    }

}
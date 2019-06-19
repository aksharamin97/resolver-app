package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class editLinkPage extends AppCompatActivity {

    EditText gtin;

    Button editGetLink;
    Button edit_btn_save;

    EditText edit_link;
    EditText edit_link_type;
    TextView edit_title_link_type;
    EditText edit_item_description;

    String url = "https://data.gs1.org/api/api.php";
    String sid;
    String new_uri;
    JSONObject body1;
    JSONObject body2;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_link_page);

        Intent intent = getIntent();
//        final String link_type = intent.getStringExtra("link_type");
        final String sid = intent.getStringExtra("sid");
        final String link = intent.getStringExtra("link");
        final String uri_response_id = intent.getStringExtra("uri_response_id");
        final String product_id = intent.getStringExtra("product_id");
        final String attribute_name = intent.getStringExtra("attribute_name");


        edit_title_link_type = (TextView) findViewById(R.id.edit_title_link_type);
        edit_link = (EditText) findViewById(R.id.edit_link);
        edit_item_description = (EditText) findViewById(R.id.edit_item_description);
        edit_link_type = (EditText) findViewById(R.id.edit_link_type);

        edit_item_description.setText(attribute_name);
        System.out.println(attribute_name);
        edit_title_link_type.setText(sid);
        edit_link.setText(link);
        System.out.println("LINK:    " + link);
        edit_link_type.setText("productDescriptionPage");
//        edit_item_description.setText(link_type);

        edit_btn_save = (Button) findViewById(R.id.edit_btn_save);
        edit_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(sid, uri_response_id, edit_link, edit_item_description);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                grabBrowserUrl.current_url = "";
            }
        });

        Button get_link = (Button) findViewById(R.id.edit_get_link);

        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grabBrowserUrl.class);
                intent.putExtra("sid", sid);
                intent.putExtra("attribute_name", edit_item_description.getText().toString());
                intent.putExtra("uri_response_id", uri_response_id);
//                intent.putExtra("new_uri", new_uri);
//                intent.putExtra("link", link);
                intent.putExtra("FROM_ACTIVITY", "B");
                startActivity(intent);
                edit_link.setText(grabBrowserUrl.current_url);
            }
        });

        Button delete = (Button)findViewById(R.id.edit_btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body2 = new JSONObject();
                try {
                    body2.put("command", "delete_uri_response");
                    body2.put("session_id", sid);
                    body2.put("uri_response_id", uri_response_id);
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
                            final String jsonString1 = response.body().string();
                            System.out.println(jsonString1);

                            editLinkPage.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Link Deleted", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Intent intent = new Intent(getApplicationContext(), dashboard.class);
                                    intent.putExtra("sid", sid);
//                                    intent.putExtra("product_id", product_id);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });

            }
        });


    }

    private void call(String sid, String uri_response_id, EditText link, EditText item_description) {
        body1 = new JSONObject();
//        System.out.println(sid);
//        System.out.println(uri_response_id);
//        System.out.println(link.getText().toString());
//        System.out.println(item_description.getText().toString());
        try {
            body1.put("command", "save_existing_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_response_id", uri_response_id);
            body1.put("attribute_id", "1");
            body1.put("iana_language", "en");
            body1.put("default_language_flag", "0");
            body1.put("destination_uri", link.getText().toString());
            body1.put("default_uri", "1");
            body1.put("alt_attribute_name", item_description.getText().toString());
            body1.put("active_start_date", "");
            body1.put("active_end_date", "");
            body1.put("forward_request_querystrings", "1");
            body1.put("active", "1");
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
                if (response.isSuccessful()) {
                    final String jsonString1 = response.body().string();
                    System.out.println(jsonString1);

                    editLinkPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "Link Added", Toast.LENGTH_SHORT);
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), product_page.class);
        intent.putExtra("sid", sid);
        startActivity(intent);
    }
}

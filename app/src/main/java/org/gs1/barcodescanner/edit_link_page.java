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

public class edit_link_page extends AppCompatActivity {


    Button edit_btn_save;

    EditText edit_link;
    EditText edit_link_type;
    TextView edit_title_link_type;
    EditText edit_alt_attribute_name;
    EditText link_type;
    EditText edit_picker_enddate;
    EditText edit_picker_startdate;

    String url = "https://data.gs1.org/api/api.php";
    String sid;
    JSONObject body1;
    JSONObject body2;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_link_page);

        Intent intent = getIntent();
        final String sid = intent.getStringExtra("sid");
        final String link = intent.getStringExtra("link");
        final String uri_response_id = intent.getStringExtra("uri_response_id");
        final String alt_attribute_name = intent.getStringExtra("alt_attribute_name");

        //Link type start date and end date are hardcoded and unchangeable for current version
        //back end is not built for these so example text is in place
        link_type = (EditText)findViewById(R.id.edit_link_type);
        link_type.setEnabled(false);
        edit_picker_enddate = (EditText)findViewById(R.id.edit_picker_enddate);
        edit_picker_enddate.setEnabled(false);
        edit_picker_startdate = (EditText)findViewById(R.id.edit_picker_startdate);
        edit_picker_startdate.setEnabled(false);


        //Front end back end connections
        edit_title_link_type = (TextView) findViewById(R.id.edit_title_link_type);
        edit_link = (EditText) findViewById(R.id.edit_link);
        edit_alt_attribute_name = findViewById(R.id.edit_alt_attribute_name);
        edit_link_type = (EditText) findViewById(R.id.edit_link_type);

        edit_alt_attribute_name.setText(alt_attribute_name);
        edit_title_link_type.setText("Edit Link");
        edit_link.setText(link);
        edit_link_type.setText("productDescriptionPage"); //Hardcoded for now

        edit_btn_save = (Button) findViewById(R.id.edit_btn_save);
        //on press product is save with new link
        edit_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_call(sid, uri_response_id, edit_link, edit_alt_attribute_name);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                grab_browser_url.current_url = "";
            }
        });

        Button get_link = (Button) findViewById(R.id.edit_get_link);
        //on press user is sent to in app browser where user can find a link and place it in textview
        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grab_browser_url.class);
                intent.putExtra("sid", sid);
                intent.putExtra("alt_attribute_name", edit_alt_attribute_name.getText().toString());
                intent.putExtra("uri_response_id", uri_response_id);
                intent.putExtra("FROM_ACTIVITY", "edit_link_page");
                startActivity(intent);
                edit_link.setText(grab_browser_url.current_url);
            }
        });

        Button delete = (Button)findViewById(R.id.edit_btn_delete);
        //on press user can delete a link
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

                            edit_link_page.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Link Deleted", Toast.LENGTH_SHORT);
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
        });


    }

    private void save_call(String sid, String uri_response_id, EditText link, EditText item_description) {
        body1 = new JSONObject();
        try {
            body1.put("command", "save_existing_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_response_id", uri_response_id);
            body1.put("attribute_id", "1");//attribute id is hardcoded and unchangeable. value '1' correlates to 'productdescription'
            body1.put("iana_language", "en");//default language is also hardcoded to english. Front end is solely visual
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

            //on successful api response toast is displayed
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString1 = response.body().string();
                    System.out.println(jsonString1);

                    edit_link_page.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "Link Added", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                }
            }
        });
    }

}

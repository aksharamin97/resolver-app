package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class product_link_page extends AppCompatActivity {


    JSONArray json_array;
    String link;
    String uri_response_id;
    String alt_attribute_name;
    String url = "https://data.gs1.org/api/api.php";
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    Button deleteBtn;
    Button edit_product;
    TextView add_link;

    ArrayList<HashMap<String, String>> list;
    ListView linkLv;
    TextView product_title;
    TextView productUri;
    Button btn_active_suspend;
    String active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_link_page_lv);

        Intent intent = getIntent();
        final String product_name = intent.getStringExtra("product_name");
        final String gtin = intent.getStringExtra("gtin");
        active = intent.getStringExtra("active");
        final String uri_request_id = intent.getStringExtra("uri_request_id");
        final String sid = intent.getStringExtra("sid");
        list = new ArrayList<>();

        product_title = findViewById(R.id.product_title);
        productUri = (TextView) findViewById(R.id.productGTIN);
        linkLv = (ListView) findViewById(R.id.linkLv);
        btn_active_suspend = (Button) findViewById(R.id.btn_active_suspend);

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        OkHttpClient client = new OkHttpClient();

        final MediaType JSON = MediaType.parse("application/json charset=utf-8");
        JSONObject body = new JSONObject();
        try {
            body.put("command", "get_response_uri_data");
            body.put("session_id", sid);
            body.put("uri_request_id", uri_request_id);
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
//                    System.out.println(jsonString);
                    try {
                        json_array = new JSONArray(jsonString);
//                        final HashMap<String, String> product = new HashMap<>();
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            link = jsonobject.getString("destination_uri");
                            uri_response_id = jsonobject.getString("uri_response_id");
                            alt_attribute_name = jsonobject.getString("alt_attribute_name");

                            HashMap<String, String> dict = new HashMap<>();
                            dict.put("alt_attribute_name", alt_attribute_name);
                            dict.put("link", link);
                            dict.put("uri_response_id", uri_response_id);
                            dict.put("uri_request_id", uri_request_id);
                            list.add(dict);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                product_title.setText(product_name);
                                productUri.setText("https://id.gs1.org/gtin/" + gtin);
                                ListAdapter adapter = new SimpleAdapter(
                                        product_link_page.this, list,
                                        R.layout.product_link_page_lv_item, new String[]{"alt_attribute_name", "link"}
                                        , new int[]{R.id.alt_attribute_name, R.id.link});

                                linkLv.setAdapter(adapter);

                                if (adapter == null) {
                                    // pre-condition
                                    return;
                                }

                                int totalHeight = 0;
                                int desiredWidth = MeasureSpec.makeMeasureSpec(linkLv.getWidth(), MeasureSpec.AT_MOST);
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    View listItem = adapter.getView(i, null, linkLv);
                                    listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                                    totalHeight += listItem.getMeasuredHeight();
                                }

                                ViewGroup.LayoutParams params = linkLv.getLayoutParams();
                                params.height = totalHeight + (linkLv.getDividerHeight() * (adapter.getCount() - 1));
                                linkLv.setLayoutParams(params);
                                linkLv.requestLayout();


                                linkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Intent intent = new Intent(getApplicationContext(), edit_link_page.class);
                                        intent.putExtra("sid", sid);
                                        intent.putExtra("active", active);
                                        intent.putExtra("alt_attribute_name", list.get(position).get("alt_attribute_name"));
                                        intent.putExtra("link", list.get(position).get("link"));
                                        intent.putExtra("uri_response_id", list.get(position).get("uri_response_id"));
                                        startActivity(intent);

                                    }
                                });
                            }
                        });
//
                    } catch (JSONException e) {
                        System.out.println("FAIL" + e);
                    }

                }
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        edit_product = (Button) findViewById(R.id.btn_edit_product);
        add_link = (TextView) findViewById(R.id.add_link);

        edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), edit_product_info_page.class);
                intent.putExtra("sid", sid);
                intent.putExtra("product_name", product_name);
                intent.putExtra("gtin", gtin);
                intent.putExtra("uri_request_id", uri_request_id);
                startActivity(intent);
            }
        });

        add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), add_new_link_page.class);
                intent.putExtra("sid", sid);
                intent.putExtra("gtin", gtin);
                intent.putExtra("uri_request_id", uri_request_id);
                startActivity(intent);
            }
        });


        productUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in_app_browser.search_gtin = gtin;
                startActivity(new Intent(getApplicationContext(), in_app_browser.class));
            }
        });
        if (active.equals("1")) {
            btn_active_suspend.setText("Suspend");
        }
        if (active.equals("0")) {
            btn_active_suspend.setText("Go Live");
        }

        btn_active_suspend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (active.equals("1")) {
                    setStatus(sid, uri_request_id, gtin, product_name, "0");
                    btn_active_suspend.setText("Active");
                }
                if (active.equals("0")) {
                    setStatus(sid, uri_request_id, gtin, product_name, "1");
                    btn_active_suspend.setText("Suspend");
                }

            }
        });


        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(product_link_page.this, "Not yet implemented", Toast.LENGTH_LONG).show();
            }
        });
    }



    private void setStatus(String sid, String new_uri, String gtin, String
            item_description, String status) {
        JSONObject body = new JSONObject();
        try {
            body.put("command", "save_existing_uri_request");
            body.put("session_id", sid);
            body.put("uri_request_id", new_uri);
            body.put("alpha_code", "gtin");
            body.put("alpha_value", gtin);
            body.put("item_description", item_description);
            body.put("include_in_sitemap", "1");
            body.put("active", status);
            body.put("uri_prefix_1", "");
            body.put("uri_suffix_1", "");
            body.put("uri_prefix_2", "");
            body.put("uri_suffix_2", "");
            body.put("uri_prefix_3", "");
            body.put("uri_suffix_3", "");
            body.put("uri_prefix_4", "");
            body.put("uri_suffix_4", "");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        RequestBody req_body = RequestBody.create(JSON, body.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(req_body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("Call 2 Error");
            }

            //
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString3 = response.body().string();
                    System.out.println(jsonString3);
                    product_link_page.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "Status Changed", Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    });
                }
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), dashboard.class);
        intent.putExtra("active", active);
        intent.putExtra("sid", login_page.session_id);
        startActivity(intent);
    }

}

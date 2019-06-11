package org.gs1.barcodescanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class product_page extends AppCompatActivity {


    JSONArray json_array;
    String link;
    String attribute_type;
//    ArrayList<HashMap<String, String>> product_list;

    Button viewInBrowser;
    Button deleteBtn;

    ArrayList<HashMap<String, String>> linkList;
    ListView linkLv;
    TextView productTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String gtin = intent.getStringExtra("gtin");
        String active = intent.getStringExtra("active");
        final String product_id = intent.getStringExtra("product_id");
        String sid = intent.getStringExtra("sid");
        linkList = new ArrayList<>();

        productTitle = (TextView)findViewById(R.id.productTitle);
        linkLv = (ListView)findViewById(R.id.linkLv);


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
                    System.out.println(jsonString);
                    try{
                        json_array = new JSONArray(jsonString);
//                        final HashMap<String, String> product = new HashMap<>();
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            link = jsonobject.getString("destination_uri");
                            attribute_type =  jsonobject.getString("alt_attribute_name");
                            HashMap<String, String> product = new HashMap<>();
//                            product.put("name", name);
                            product.put("link_type", attribute_type);
                            product.put("link", link);
//                            product.put(link, attribute_type);
                            linkList.add(product);
//                            System.out.println(product);
//                            product_list.add(product);
                        }
//                        System.out.println(product);
//                        final TableLayout tl = (TableLayout)findViewById(R.id.tl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                for (String i : product.keySet()) {
//                                    TableRow row = new TableRow(product_page.this);
//                                    TextView tv_type = new TextView(product_page.this);
//                                    TextView tv_link = new TextView(product_page.this);
////                                    System.out.println(i);
////                                    System.out.println(product.get(i));
//                                    tv_type.setText(product.get(i));
//                                    tv_link.setText(i);
//                                    row.addView(tv_type);
//                                    row.addView(tv_link);
//                                    tl.addView(row);
//                                }
                                System.out.println(linkList);
                                productTitle.setText(name);
                                ListAdapter adapter = new SimpleAdapter(
                                        product_page.this, linkList,
                                        R.layout.activity_product_page_item, new String[]{"link_type", "link"}
                                        , new int[]{R.id.linkType,
                                        R.id.link});

                                linkLv.setAdapter(adapter);
                                linkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        System.out.println(list.get(position).get("name"));
                                        Uri webaddress = Uri.parse(linkList.get(position).get("link"));
                                        Intent intent = new Intent(Intent.ACTION_VIEW, webaddress);
                                        if(intent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(intent);
                                        }
                                    }
                                });
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

        viewInBrowser = findViewById(R.id.viewInBrowser);
        viewInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mybrowser.gtin = gtin;
                startActivity(new Intent(getApplicationContext(), mybrowser.class));
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(product_page.this, "Not yet implemented", Toast.LENGTH_LONG).show();
            }
        });

    }
}

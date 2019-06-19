package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

public class product_page extends AppCompatActivity {


    JSONArray json_array;
    String link;
    String uri_response_id;
    String attribute_type;
//    ArrayList<HashMap<String, String>> product_list;

    Button viewInBrowser;
    Button deleteBtn;
    Button edit_product;

    ArrayList<HashMap<String, String>> linkList;
    ListView linkLv;
    TextView productTitle;
    TextView productUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String gtin = intent.getStringExtra("gtin");
        String active = intent.getStringExtra("active");
        final String product_id = intent.getStringExtra("product_id");
        final String sid = intent.getStringExtra("sid");
        linkList = new ArrayList<>();

        productTitle = (TextView)findViewById(R.id.productTitle);
        productUri = (TextView) findViewById(R.id.productGTIN);
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
//                    System.out.println(jsonString);
                    try{
                        json_array = new JSONArray(jsonString);
//                        final HashMap<String, String> product = new HashMap<>();
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            link = jsonobject.getString("destination_uri");
                            uri_response_id = jsonobject.getString("uri_response_id");
                            attribute_type =  jsonobject.getString("alt_attribute_name");

                            HashMap<String, String> product = new HashMap<>();
                            product.put("link_type", attribute_type);
                            product.put("link", link);
                            product.put("uri_response_id", uri_response_id);
                            product.put("product_id", product_id);
                            linkList.add(product);
//                            product_list.add(product);
                        }
                        System.out.println(linkList);
//                        System.out.println(product);
//                        final TableLayout tl = (TableLayout)findViewById(R.id.tl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                productTitle.setText(name);
                                productUri.setText("https://id.gs1.org/gtin/" + gtin);
                                ListAdapter adapter = new SimpleAdapter(
                                        product_page.this, linkList,
                                        R.layout.activity_product_page_item, new String[]{"link_type", "link"}
                                        , new int[]{R.id.linkType, R.id.link});

                                linkLv.setAdapter(adapter);
                                linkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        System.out.println(list.get(position).get("name"));

                                        Intent intent = new Intent(getApplicationContext(), editLinkPage.class);
                                        intent.putExtra("sid", sid);
//                                        intent.putExtra("attribute_type", linkList.get(position).get("attribute_type"));
                                        intent.putExtra("link_type", linkList.get(position).get("link_type"));
                                        intent.putExtra("link", linkList.get(position).get("link"));
                                        intent.putExtra("uri_response_id", linkList.get(position).get("uri_response_id"));
                                        intent.putExtra("product_id", linkList.get(position).get("product_id"));

                                        startActivity(intent);

////                                        System.out.println(position);
//                                            Intent intent = new Intent(getApplicationContext(), product_page.class);
//                                            intent.putExtra("name", list.get(position).get("name"));
//                                            intent.putExtra("gtin", list.get(position).get("gtin"));
//                                            intent.putExtra("active", list.get(position).get("active"));
//                                            intent.putExtra("product_id", list.get(position).get("product_id"));
//                                            intent.putExtra("sid", sid);
//                                            startActivity(intent);
////                                        Toast toast = Toast.makeText(getApplicationContext(),list.get(position).get("name"), Toast.LENGTH_SHORT);
////                                        toast.show();
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

        edit_product = (Button)findViewById(R.id.btn_edit_product);
        edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addProductPage.class);
                intent.putExtra("sid", sid);
                intent.putExtra("name", name);
                intent.putExtra("GTIN", gtin);
                intent.putExtra("product_id", product_id);
                startActivity(intent);
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

        ImageView logo;
        logo = (ImageView)findViewById(R.id.banner_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

}

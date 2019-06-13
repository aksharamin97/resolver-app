package org.gs1.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
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


public class dashboard extends AppCompatActivity {
    String name;
    String gtin;
    String active;
    String product_id;
    String last_product_id;
    JSONArray json_array;
//    HashMap<String, String> hash = new HashMap<>();
    String url = "https://data.gs1.org/api/api.php";
    ArrayList<HashMap<String, String>> list;
//    ArrayList<String> product_list = new ArrayList<String>();
    ListView lv;
//    String titlePosition;


    Button addProduct;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///changed///
        setContentView(R.layout.dashboard_lv);
        list = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        final String sid = intent.getStringExtra("sid");
        //String sid = "8391xulq7aklik7w3ibf3b5m42yl3mjvv3swssea8cy7317wbu";
        OkHttpClient client = new OkHttpClient();

        final MediaType JSON = MediaType.parse("application/json charset=utf-8");
        JSONObject body = new JSONObject();
        try {
            body.put("command", "get_uri_list");
            body.put("session_id", sid);
            body.put("first_line_number", "0");
            body.put("max_number_of_lines", "1000");
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonString = response.body().string();
                    try {
                        json_array = new JSONArray(jsonString);
//                        JSONObject jsonobject = json_array.getJSONObject(0);
//                        name = jsonobject.getString("item_description");
//                        gtin = jsonobject.getString("alpha_value");
//                        System.out.println(name);
//                        System.out.println(gtin);
//                        addRow(name, gtin);
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            name = jsonobject.getString("item_description");
                            gtin = jsonobject.getString("alpha_value");
                            active = jsonobject.getString("active");
                            product_id = jsonobject.getString("uri_request_id");
//                            System.out.println("name in loop = " + name);
//                            System.out.println("gtin in loop = " + gtin);
                            HashMap<String, String> contact = new HashMap<>();
                            HashMap<String, Bitmap> productStat = new HashMap<>();
                            contact.put("name", name);
                            contact.put("gtin", gtin);
//                            contact.put("active", active);
                            contact.put("product_id", product_id);
                            if(active.compareTo("1")==0) {
                                contact.put("status", "Active");
                                contact.put("active", "•        ");
                                contact.put("suspending", "");
                            }
                            else{
                                contact.put("status", "Suspending");
                                contact.put("active", "");
                                contact.put("suspending", "•        ");
                            }

//                            System.out.println("hash/dict = " + contact);
                            list.add(contact);
//                            System.out.println("Current Contact List in loop = " + list);
//                            list.put(name, gtin);
//                            product_list.add(product_id);

                        }
//                        System.out.println("list     " + list);
//                        Collections.reverse(list);

//                        last_product_id = product_list.get(product_list.size() -1 );

//                        System.out.println("Final Contact List = " + list);

                        runOnUiThread(new Runnable() {

                            @SuppressLint("ResourceType")
                            @Override
                            public void run() {
                                final ListAdapter adapter = new SimpleAdapter(
                                        dashboard.this, list, R.layout.dashboard_lv_item,
                                        new String[]{"name", "gtin", "suspending", "active", "status"},
                                        new int[]{R.id.name, R.id.gtin, R.id.suspending, R.id.active, R.id.status});
//

                                lv.setAdapter(adapter);

                                TextView dashboard_title = findViewById(R.id.dashboard_title);
                                String dashboard_title_text = "You currently have " + list.size() + " products";
                                SpannableString ss = new SpannableString(dashboard_title_text);
                                ForegroundColorSpan fcsPrimary = new ForegroundColorSpan(Color.parseColor("#F26334"));
                                ss.setSpan(fcsPrimary, 18, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                dashboard_title.setText(ss);

                                SearchView sv = (SearchView)findViewById(R.id.dashboard_search_bar);
                                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        ((SimpleAdapter) adapter).getFilter().filter(newText);
                                        return false;
                                    }
                                });

                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        System.out.println(position);
                                        Intent intent = new Intent(getApplicationContext(), product_page.class);
                                        intent.putExtra("name", list.get(position).get("name"));
                                        intent.putExtra("gtin", list.get(position).get("gtin"));
                                        intent.putExtra("active", list.get(position).get("active"));
                                        intent.putExtra("product_id", list.get(position).get("product_id"));
                                        intent.putExtra("sid", sid);
                                        startActivity(intent);
//                                        Toast toast = Toast.makeText(getApplicationContext(),list.get(position).get("name"), Toast.LENGTH_SHORT);
//                                        toast.show();
                                }
                                });


                            }
                        });
                    }
                    catch (JSONException e) {
                        System.out.println("FAIL" + e);
                    }
                }
            }
        });

        addProduct = findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), addProductPage.class);
                intent.putExtra("sid", sid);
//                intent.putExtra("last_product_id", last_product_id);
                startActivity(intent);
            }
        });

    }
}


package org.gs1.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class dashboard extends AppCompatActivity {
    String product_name;
    String gtin;
    String status;
    String uri_request_id;
    JSONArray json_array;
    String url = "https://data.gs1.org/api/api.php";
    ArrayList<HashMap<String, String>> list;
    ListView list_view;
    Button btn_add_new_product;

    static String jsonString = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_lv);
        list = new ArrayList<>();//ArrayList full of Hashmaps which contain product data that is going to be displayed
        list_view = findViewById(R.id.list_view);
////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        final String sid = intent.getStringExtra("sid");

        OkHttpClient client = new OkHttpClient();

        //Resolver API calls
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
        final Request request = new Request.Builder()
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
                //On successful api call
                if (response.isSuccessful()) {
                    jsonString = response.body().string();
                    try {
                        json_array = new JSONArray(jsonString);
                        for (int i = 0; i < json_array.length(); i++) {
                            JSONObject jsonobject = json_array.getJSONObject(i);
                            product_name = jsonobject.getString("item_description");
                            gtin = jsonobject.getString("alpha_value");
                            status = jsonobject.getString("active");
                            uri_request_id = jsonobject.getString("uri_request_id");
                            //Hashmap "dict" is used to store data by key value pairs like python dictionaries
                            //dict's keys and values are both strings
                            HashMap<String, String> dict = new HashMap<>();
                            //adding all product attributes to dict
                            dict.put("product_name", product_name);
                            dict.put("gtin", gtin);
                            dict.put("status1", status);
                            dict.put("uri_request_id", uri_request_id);
                            //if status is active set active to "• "
                            if(status.compareTo("1")==0) {
                                dict.put("status", "Active");
                                dict.put("active", "• ");
                                dict.put("suspending", "");
                            }
                            //if status is Draft set suspending to "• "
                            //this allows a red dot to be printed if a product is suspending and a green dot if product is active
                            else{
                                dict.put("status", "Draft");
                                dict.put("active", "");
                                dict.put("suspending", "• ");
                            }
                            //dict is then added to list
                            list.add(dict);
                            //this is repeated until all products in the json object are added to list
                        }
                        //list is inverted so newest elements are displayed first in list
                        Collections.reverse(list);

                        runOnUiThread(new Runnable() {

                            @SuppressLint("ResourceType")
                            @Override
                            public void run() {
                                //ListAdapters shows what is displayed in each element of list
                                final ListAdapter adapter = new SimpleAdapter(
                                        dashboard.this, list, R.layout.dashboard_lv_item,
                                        new String[]{"product_name", "gtin", "suspending", "active", "status"},
                                        new int[]{R.id.product_name, R.id.gtin, R.id.suspending, R.id.active, R.id.status});
//
                                list_view.setAdapter(adapter);

                                //Displays number of items in list at top of screen
                                TextView dashboard_title = findViewById(R.id.dashboard_title);
                                String dashboard_title_text = "You currently have " + list.size() + " products";
                                SpannableString ss = new SpannableString(dashboard_title_text);
                                ForegroundColorSpan fcsPrimary = new ForegroundColorSpan(Color.parseColor("#F26334"));

                                Scanner sc = new Scanner(String.valueOf(list.size()));
                                int Number, Count = 0;
                                Number = sc.nextInt();

                                while(Number > 0){
                                    Number = Number/10;
                                    Count = Count + 1;
                                }
                                ss.setSpan(fcsPrimary, 18, 19+Count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                dashboard_title.setText(ss);

                                SearchView sv = (SearchView)findViewById(R.id.dashboard_search_bar);
                                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    //On very text change list view is update
                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        ((SimpleAdapter) adapter).getFilter().filter(newText);
                                        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Object new_id = adapter.getItem(position);
                                                Intent intent = new Intent(getApplicationContext(), product_link_page.class);
                                                intent.putExtra("product_name", ((HashMap) new_id).get("product_name").toString());
                                                intent.putExtra("gtin",  ((HashMap) new_id).get("gtin").toString());
                                                intent.putExtra("active",  ((HashMap) new_id).get("status1").toString());
                                                intent.putExtra("uri_request_id",  ((HashMap) new_id).get("uri_request_id").toString());
                                                intent.putExtra("sid", sid);
                                                startActivity(intent);
                                            }
                                        });
                                        return false;
                                    }
                                });

                                //When a product is pressed on the listview product link page is opened along with all necessary information
                                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(getApplicationContext(), product_link_page.class);
                                        intent.putExtra("product_name", list.get(position).get("product_name"));
                                        intent.putExtra("gtin", list.get(position).get("gtin"));
                                        intent.putExtra("active", list.get(position).get("status1"));
                                        intent.putExtra("uri_request_id", list.get(position).get("uri_request_id"));
                                        intent.putExtra("sid", sid);
                                        startActivity(intent);
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

        btn_add_new_product = findViewById(R.id.btn_add_new_product);
        btn_add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), add_new_product_page1.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });

        ImageView logo;
        logo = (ImageView)findViewById(R.id.banner_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), main_page.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), main_page.class);
        startActivity(intent);
    }
}


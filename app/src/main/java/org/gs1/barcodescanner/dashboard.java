package org.gs1.barcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

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
    JSONArray json_array;
//    HashMap<String, String> hash = new HashMap<>();
    String url = "https://data.gs1.org/api/api.php";
    ArrayList<HashMap<String, String>> list;
    ListView lv;

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
            body.put("max_number_of_lines", "20");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON Exception");
            e.printStackTrace();
        }
        String url = "https://data.gs1.org/api/api.php";
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
                            contact.put("name", name);
                            contact.put("gtin", gtin);
                            contact.put("active", active);
                            contact.put("product_id", product_id);
//                            System.out.println("hash/dict = " + contact);
                            list.add(contact);
//                            System.out.println("Current Contact List in loop = " + list);
//                            list.put(name, gtin);

                        }
//                        System.out.println(list);
//                        System.out.println("Final Contact List = " + list);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                ListAdapter adapter = new SimpleAdapter(
                                        dashboard.this, list,
                                        R.layout.dashboard_lv_item, new String[]{"name", "gtin",
                                        "active"}, new int[]{R.id.name,
                                        R.id.gtin, R.id.active});
//
                                lv.setAdapter(adapter);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        System.out.println(list.get(position).get("name"));
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
//                                TableLayout tl = (TableLayout)findViewById(R.id.tl);
//                                for (String i : list.keySet()){
//                                    TableRow row = new TableRow(dashboard.this);
//                                    TextView tv_name = new TextView(dashboard.this);
//                                    TextView tv_gtin = new TextView(dashboard.this);
//                                    tv_name.setText(i);
//                                    tv_gtin.setText((list.get(i)));
//                                    row.addView(tv_name);
//                                    row.addView(tv_gtin);
//                                    tl.addView(row);
//                                }

                            }
                        });
                    }
                    catch (JSONException e) {
                        System.out.println("FAIL" + e);
                    }
                }
            }
        });
/*        adapter = new ArrayAdapter<HashMap<String, String>>(
                dashboard.this,android.R.layout.simple_list_item_1, list
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_dash,menu);
        MenuItem item = menu.findItem(R.id.search_list);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for(int i = 0; i<list.size();i++){
                    if (list.get(i).get("name").toLowerCase().contains(query.toLowerCase())) {
                        //newList.add(list.get(i));
                        Toast.makeText(dashboard.this, "Match found", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(dashboard.this, "No Match found", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);*/
    }
}


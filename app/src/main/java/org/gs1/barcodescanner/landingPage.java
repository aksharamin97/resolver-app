package org.gs1.barcodescanner;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.internal.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class landingPage extends AppCompatActivity {

    Button openBrowser;
    Button getJson;

    private TextView result;
    private TextView landing_title;
    private TextView name;
    private TextView uri;

    JSONObject object;
    String productName;

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> uris = new ArrayList<>();
    ArrayList<HashMap<String, String>> linkList;

    ListView linkLv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        landing_title = (TextView)findViewById(R.id.landing_title);
        linkLv = (ListView)findViewById(R.id.linkLv);
        linkList = new ArrayList<>();


        openBrowser= (Button)findViewById(R.id.getBtn);

        openBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mybrowser.class);
                startActivity(intent);
            }
        });


        getWebsite();

    }

    String scriptcontent;
    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://id.gs1.org/gtin/"+ mybrowser.gtin + "?linkType=all").get();
                    String title = doc.title();
//                    Elements links = doc.select("a[href]");
                    Elements links = doc.select("script[type=\"application/ld+json\"]");
                    scriptcontent = links.first().html();

                    object = new JSONObject(scriptcontent);
//                    System.out.println(object.toString());
                    productName = object.getJSONObject("/").getString("item_name");
//                    System.out.println(object.getJSONObject("/").getJSONObject("responses").getJSONObject("productdescriptionpage").getJSONObject("lang").getJSONObject("en").getString("link"));

                    JSONObject jsonobject = object.getJSONObject("/").getJSONObject("responses");
                    Iterator<String> keys = jsonobject.keys();


                    while (keys.hasNext()) {
                        HashMap<String,String>product = new HashMap<>();
                        String key = keys.next();
                        if (jsonobject.get(key) instanceof JSONObject) {
//                            System.out.println(key);
                            String keyString = key.toString();
                            product.put("title", jsonobject.getJSONObject(keyString).getJSONObject("lang").getJSONObject("en").getString("title"));
                            product.put("link", jsonobject.getJSONObject(keyString).getJSONObject("lang").getJSONObject("en").getString("link"));

                            linkList.add(product);

                        }
                    }
                    System.out.println(linkList);

                    builder.append(title).append("\n");

                    for ( Element link : links){
                        builder.append("Link: ").append(link.attr("href")).append("\n").append("text: ").append(link.text());
                    }

                } catch (IOException e) {
                    builder.append("Error: ").append(e.getMessage()).append("\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        StringBuilder output = new StringBuilder();

                        landing_title.setText(productName);
                        ListAdapter adapter = new SimpleAdapter(
                                landingPage.this, linkList,
                                R.layout.activity_product_page_item, new String[]{"title", "link"}
                                , new int[]{R.id.linkType,
                                R.id.link});

                        linkLv.setAdapter(adapter);
                        linkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                System.out.println(list.get(position).get("name"));
                                Uri webaddress = Uri.parse(linkList.get(position).get("link"));
                                Intent intent = new Intent(Intent.ACTION_VIEW, webaddress);
                                if(intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        });
//
//                        int listSize = titles.size();
//
//                        for (int i = 0; i < listSize; i++){
//                            output.append("\n" + titles.get(i) + ": \n" + uris.get(i) + "\n");
//                            result.setText(output);
//                        }
                    }
                });
            }
        }).start();

    }
}

package org.gs1.barcodescanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class consumer_landing_page extends AppCompatActivity {

    Button btn_go_resolver;
    String product_name;
    JSONObject object;
    private TextView landing_page_title;
    ArrayList<HashMap<String, String>> linkList;
    ListView linkLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_landing_page);

        landing_page_title = findViewById(R.id.landing_page_title);
        linkLv = (ListView)findViewById(R.id.linkLv);
        linkList = new ArrayList<>();
        btn_go_resolver = findViewById(R.id.btn_go_resolver);

        btn_go_resolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), in_app_browser.class);
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
                    Document doc = Jsoup.connect("https://id.gs1.org/gtin/" + in_app_browser.search_gtin + "?linkType=all").get();
                    String title = doc.title();
                    Elements links = doc.select("script[type=\"application/ld+json\"]");
                    scriptcontent = links.first().html();

                    object = new JSONObject(scriptcontent);
                    product_name = object.getJSONObject("/").getString("item_name");
                    JSONObject jsonobject = object.getJSONObject("/").getJSONObject("responses");
                    Iterator<String> keys = jsonobject.keys();
                    while (keys.hasNext()) {
                        HashMap<String,String>product = new HashMap<>();
                        String key = keys.next();
                        if (jsonobject.get(key) instanceof JSONObject) {
                            String keyString = key.toString();
                            product.put("title", jsonobject.getJSONObject(keyString).getJSONObject("lang").getJSONObject("en").getString("title"));
                            product.put("link", jsonobject.getJSONObject(keyString).getJSONObject("lang").getJSONObject("en").getString("link"));
                            linkList.add(product);
                        }
                    }
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
                        landing_page_title.setText(product_name);
                        ListAdapter adapter = new SimpleAdapter(
                                consumer_landing_page.this, linkList,
                                R.layout.product_link_page_lv_item, new String[]{"title", "link"}
                                , new int[]{R.id.alt_attribute_name,
                                R.id.link});

                        linkLv.setAdapter(adapter);
                        linkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Uri webaddress = Uri.parse(linkList.get(position).get("link"));
                                Intent intent = new Intent(Intent.ACTION_VIEW, webaddress);
                                if(intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        }).start();

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
}

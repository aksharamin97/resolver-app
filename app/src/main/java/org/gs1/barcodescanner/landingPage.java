package org.gs1.barcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class landingPage extends AppCompatActivity {

    Button openBrowser;
    Button getJson;
    private TextView result;

    JSONObject object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        result = (TextView)findViewById(R.id.result);

        openBrowser= (Button)findViewById(R.id.getBtn);
        getJson = (Button)findViewById(R.id.getJson);

        openBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mybrowser.class);
                startActivity(intent);
            }
        });

        getJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWebsite();
            }
        });


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

                    System.out.println(object.getClass().getName());

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
//                        result.setText(builder.toString());
                        result.setText(scriptcontent);

                    }
                });
            }
        }).start();

    }

}

package org.gs1.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class add_new_product_page2 extends AppCompatActivity {

    Button btn_save;
    Button get_link;
    Button btn_add;
    String product_name;

    EditText link;
    AutoCompleteTextView link_type;
    EditText add_alt_attribute_name;
    EditText picker_startdate;
    EditText picker_enddate;

    String url = "https://data.gs1.org/api/api.php";
    String sid;
    String uri_request_id;
    String gtin;
    String alt_attribute_name;
    JSONObject body1;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_product_page2);
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        uri_request_id = intent.getStringExtra("uri_request_id");
        gtin = intent.getStringExtra("gtin");
        product_name = intent.getStringExtra("product_name");
        alt_attribute_name = intent.getStringExtra("alt_attribute_name");
        link = (EditText)findViewById(R.id.link);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, add_new_link_page.att_id);

        link_type = (AutoCompleteTextView) findViewById(R.id.link_type);
        link_type.setAdapter(adapter);


        //start date and end date are hardcoded and unchangeable for current version
        //back end is not built for these so example text is in place
        picker_enddate = (EditText)findViewById(R.id.picker_enddate);
        picker_enddate.setEnabled(false);
        picker_startdate = (EditText)findViewById(R.id.picker_startdate);
        picker_startdate.setEnabled(false);

        System.out.println("id   " + uri_request_id);
        System.out.println("gtin   " + gtin);
        System.out.println("product_name   " + product_name);
        System.out.println("sid   " + sid);

        add_alt_attribute_name = (EditText)findViewById(R.id.alt_attribute_name);
        add_alt_attribute_name.setText(alt_attribute_name);

        final HashMap<String, String> attribute_id = new HashMap<>();
        attribute_id.put("productDescriptionPage","1");
        attribute_id.put("nutritionalInformationPage","2");
        attribute_id.put("recipeWebsite","3");
        attribute_id.put("instructionsForUse","4");
        attribute_id.put("b2bData","5");
        attribute_id.put("productJson","6");
        attribute_id.put("activityIdeas","8");
        attribute_id.put("sustainabilityInformation","9");
        attribute_id.put("smartLabel","10");
        attribute_id.put("relatedVideo","11");
        attribute_id.put("consumerData","12");
        attribute_id.put("productMarketingMessage","13");
        attribute_id.put("epil","16");
        attribute_id.put("packshot","17");
        attribute_id.put("productMarketingMessage2","18");
        attribute_id.put("tastingNotes","19");
        attribute_id.put("sameAs","20");
        attribute_id.put("premiumDataService","21");
        attribute_id.put("epcisService","22");
        attribute_id.put("mitVnsWakeHandler","23");
        attribute_id.put("doNotUse","24");
        attribute_id.put("smpc","25");
        attribute_id.put("brandHomepagePatient","26");
        attribute_id.put("promotion","27");
        attribute_id.put("pip","28");
        attribute_id.put("review","29");
        attribute_id.put("recallStatus","30");
        attribute_id.put("safetyInfo","31");
        attribute_id.put("faqs","32");
        attribute_id.put("socialMedia","33");
        attribute_id.put("allergenInfo","34");
        attribute_id.put("traceability","35");
        attribute_id.put("trackAndTrace","36");
        attribute_id.put("hasRetailers","37");
        attribute_id.put("productSustainabilityInfo","38");
        btn_save  = (Button) findViewById(R.id.btn_save);
        //on press product is saved and user is sent back to dashboard where new product should appear
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK  "+link.getText().toString());
                System.out.println("Attribute ALT    " +add_alt_attribute_name.getText().toString());
                link_call(sid, uri_request_id, link, add_alt_attribute_name, attribute_id, link_type);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });


        btn_add = (Button)findViewById(R.id.btn_add);
        //on press all information is saved but a blank add_new_product_page2 is opened
        //user is then allowed to enter another link
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link_call(sid, uri_request_id, link, add_alt_attribute_name, attribute_id, link_type);
                Intent intent = new Intent(getApplicationContext(), add_new_product_page2.class);
                intent.putExtra("sid", sid);
                intent.putExtra("uri_request_id", uri_request_id);
                startActivity(intent);
            }
        });


        get_link = (Button)findViewById(R.id.get_link);
        // on press user is brought to in app browser where they can easily get any link and bring it back to the page
        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grab_browser_url.class);
                intent.putExtra("sid", sid);
                intent.putExtra("uri_request_id", uri_request_id);
                intent.putExtra("gtin", gtin);
                intent.putExtra("product_name", product_name);
                intent.putExtra("alt_attribute_name",add_alt_attribute_name.getText().toString());
                intent.putExtra("FROM_ACTIVITY", "add_new_product_page2");
                startActivity(intent);
            }
        });


        //link editText is set to url picked from grab_browser_url
        link.setText(grab_browser_url.current_url);
        grab_browser_url.current_url = "";

    }

    private void link_call(String sid, String uri_request_id, EditText link, EditText add_alt_attribute_name, HashMap attribute_id, AutoCompleteTextView link_type) {
        try {
            body1 = new JSONObject();
            body1.put("command", "save_new_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_request_id", uri_request_id);
            body1.put("attribute_id", attribute_id.get(link_type.getText().toString()));
            body1.put("iana_language", "en");//default language is hard coded to english. front end only for design
            body1.put("destination_uri", link.getText().toString());
            body1.put("default_uri", "1");
            body1.put("alt_attribute_name", add_alt_attribute_name.getText().toString());
            body1.put("active_start_date", "");
            body1.put("active_end_date", "");
            body1.put("forward_request_querystrings", "1");
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
            //on successful api call toast is displayed saying linked added
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String jsonString1 = response.body().string();
                    System.out.println(jsonString1);

                    add_new_product_page2.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(),"Link Added", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                }
            }
        });
    }

}
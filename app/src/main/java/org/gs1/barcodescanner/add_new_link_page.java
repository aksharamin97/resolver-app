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

import me.dm7.barcodescanner.core.ViewFinderView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class add_new_link_page extends AppCompatActivity {

    //initiating front end connections
    Button btn_save;
    Button get_link;
    EditText link;
    EditText alt_attribute_name;
    AutoCompleteTextView link_type;
    EditText addNewLink_link;
    EditText addNewLink_picker_enddate;
    EditText addNewLink_picker_startdate;

    String url = "https://data.gs1.org/api/api.php";
    String sid;
    String uri_request_id;
    String gtin;

    JSONObject body1;
    MediaType JSON = MediaType.parse("application/json charset=utf-8");
    OkHttpClient client = new OkHttpClient();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_link_page);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        gtin = intent.getStringExtra("gtin");
        uri_request_id = intent.getStringExtra("uri_request_id");
        final String uri_response_id = intent.getStringExtra("uri_response_id");
        final String grab_link = intent.getStringExtra("link");

        //Set up for link type text box
        //when two letters are typed all options containing those letters are shown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, att_id);

        link_type = (AutoCompleteTextView) findViewById(R.id.addNewLink_link_type);
        link_type.setAdapter(adapter);
//        link_type.setEnabled(false);



        //start date and end date are hardcoded and unchangeable for current version
        //back end is not built for these so example text is in place
        addNewLink_picker_enddate = (EditText)findViewById(R.id.addNewLink_picker_enddate);
        addNewLink_picker_enddate.setEnabled(false);
        addNewLink_picker_startdate = (EditText)findViewById(R.id.addNewLink_picker_startdate);
        addNewLink_picker_startdate.setEnabled(false);

        link = findViewById(R.id.addNewLink_link);
        link.setText(grab_link);
//        System.out.println("DIS     " + link.getText().toString());
//        System.out.println("PRODUCT_ID     " + uri_request_id);
//        System.out.println(uri_request_id);
        alt_attribute_name = findViewById(R.id.addNewLink_alt_attribute_name);


        btn_save = findViewById(R.id.addNewLink_btn_save);
        get_link = findViewById(R.id.addNewLink_get_link);

        //This is a hashmap used to to match resolver link types with associated values
        //these key value pairs are hardcoded with the values found on data.gs1.org/ui
        //more efficient way to do this would be using api if possible, due to time constraints this method was used
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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK      " + link.getText().toString());
                System.out.println("ATTRIBUTE _NAME     " + alt_attribute_name.getText().toString());
                link_call(sid, uri_request_id, link, alt_attribute_name, attribute_id, link_type);
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
            }
        });

        //on press user is brought to in app browser that can grab a link and be brought back his page
        get_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), grab_browser_url.class);
                intent.putExtra("sid", sid);
                intent.putExtra("gtin", gtin);
                intent.putExtra("uri_request_id", uri_request_id);
                intent.putExtra("FROM_ACTIVITY", "add_new_link_page");
                startActivity(intent);
            }
        });

    }

    //api call
    private void link_call(String sid, String uri_request_id, EditText link, EditText alt_attribute_name, HashMap attribute_id, AutoCompleteTextView link_type) {
        try {

            body1 = new JSONObject();
            body1.put("command", "save_new_uri_response");
            body1.put("session_id", sid);
            body1.put("uri_request_id", uri_request_id);
            body1.put("attribute_id", attribute_id.get(link_type.getText().toString()));
            body1.put("iana_language", "en");
            body1.put("destination_uri", link.getText().toString());
            body1.put("default_uri", "0");
            body1.put("alt_attribute_name", alt_attribute_name.getText().toString());
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

            //on successful call
            //toast will appear saying link was added
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString1 = response.body().string();
                    System.out.println("OUTPUT    " + jsonString1);

                    add_new_link_page.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "Link Added", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                }
            }
        });
    }

     public static final String[] att_id = new String[]{"productDescriptionPage",
             "nutritionalInformationPage",
             "recipeWebsite",
             "instructionsForUse",
             "b2bData",
             "productJson",
             "activityIdeas",
             "sustainabilityInformation",
             "smartLabel",
             "relatedVideo",
             "consumerData",
             "productMarketingMessage",
             "epil",
             "packshot",
             "productMarketingMessage",
             "tastingNotes",
             "sameAs",
             "premiumDataService",
             "epcisService",
             "mitVnsWakeHandler",
             "doNotUse",
             "smpc",
             "brandHomepagePatient",
             "promotion",
             "pip",
             "review",
             "recallStatus",
             "safetyInfo",
             "faqs",
             "socialMedia",
             "allergenInfo",
             "traceability",
             "trackAndTrace",
             "hasRetailers",
             "productSustainabilityInfo"};



}
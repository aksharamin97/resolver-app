package org.gs1.barcodescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.*;

public class login_page extends AppCompatActivity {

    public EditText username;
    public EditText password;

    private CheckBox logCheck;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String TEXT2 = "text2";
    public static final String SWITCH1 = "switch1";
    private String text;
    private String text2;
    private boolean switchOnOff;
    public static String session_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        logCheck = (CheckBox)findViewById(R.id.logCheck);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button btn = (Button)findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json charset=utf-8");
                JSONObject body = new JSONObject();
                try {
                    body.put("command", "get_session");
                    body.put("email", username.getText().toString());
                    body.put("password", password.getText().toString());
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
                        if (response.isSuccessful()){
                            final String jsonString = response.body().string();
                            try {
                                JSONObject json = new JSONObject(jsonString);
                                session_id = json.getString("session_id");

                            }
                            catch(JSONException ex) {
                                System.out.println("Error");
                            }

                            final String sid = session_id;
                            if (sid.compareTo("LOGIN FAILED") == 0){
                                login_page.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                            else{
                                login_page.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), dashboard.class);
                                        intent.putExtra("sid", sid);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                });

                    if (logCheck.isChecked())
                        saveData();
                    else
                        clearData();
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////

        loadData();
        updateView();


//////////////////////////////////////////////////////////////////////////////////////////////////
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
   private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString(TEXT, username.getText().toString());
       editor.putString(TEXT2, password.getText().toString());
        editor.putBoolean(SWITCH1, logCheck.isChecked());

        editor.apply();

        //Toast.makeText(this,"Data saved", Toast.LENGTH_SHORT).show();
    }

    private  void clearData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, "");
        editor.putString(TEXT2, "");
        editor.putBoolean(SWITCH1, false);

        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
        text2 = sharedPreferences.getString(TEXT2, "");
        switchOnOff = sharedPreferences.getBoolean(SWITCH1,false);
    }

    public void updateView(){
        username.setText(text);
        password.setText(text2);
        logCheck.setChecked(switchOnOff);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

}

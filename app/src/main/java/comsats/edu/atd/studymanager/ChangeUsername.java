package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeUsername extends AppCompatActivity {
    private ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    private TextInputEditText user_name,user_namehint,password;
    private Button save,cancel;
    private boolean IsError;
    String name,password1,newname,pass,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        Intent intent = getIntent();
        setContentView(R.layout.activity_change_first_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name= intent.getStringExtra("username");
        password1  = intent.getStringExtra("password");
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        password = findViewById(R.id.password);
        user_name = findViewById(R.id.firstname);
        user_namehint = findViewById(R.id.nameholder);
        user_namehint.setText(name);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcontent();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChangeUsername.this,Setting.class);
                startActivity(intent1);
                finish();
            }
        });
    }
    public void checkcontent(){
        newname = user_name.getText().toString().trim();
        pass = password.getText().toString().trim();

        if(newname == null || newname.isEmpty()){
            IsError = true;
            user_name.setError("Field cannot be empty");
            user_name.setFocusable(true);
        }else if(newname.equals(name)){
            IsError = true;
            user_name.setError("Please enter a different name");
            user_name.setFocusable(true);
        }else if(pass.isEmpty() || pass == null){
            IsError = true;
            password.setError("Field cannot be empty");
            password.setFocusable(true);

        }else if(!pass.equals(password1)){
            IsError = true;
            password.setError("Password doesnot match");
            password.setFocusable(true);

        }
        else{
            IsError = false;
        }
        if(!IsError){
            savechanges();

        }

    }
    private void savechanges() {
        dialog = ProgressDialog.show(ChangeUsername.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ChangeUsername.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.UPDATE_USERNAME;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        Log.d("112233","Url Setting");
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                Snackbar.make(findViewById(android.R.id.content),"Username not updated", Snackbar.LENGTH_SHORT)
                                        .show();
                            }else if (status.equals("200")) {
                                Snackbar.make(findViewById(android.R.id.content),"Username Updated Successfully", Snackbar.LENGTH_LONG)
                                        .show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username",newname);
                                editor.apply();
                                waitforsometime();


                            }else if (status.equals("201")) {
                                Snackbar.make(findViewById(android.R.id.content),"User already exist \n,Please try another name", Snackbar.LENGTH_LONG)
                                        .show();



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content),"Opps.! Retry Again", Snackbar.LENGTH_SHORT)

                        .show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("name", newname);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent(ChangeUsername.this,MyProfile.class);
                startActivity(intent);
                finish();

            }
        }, 1500);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                super.onBackPressed();
                break;
        }


        return true;
    }
}

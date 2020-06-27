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
import android.widget.Toast;

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

public class ChangePassword extends AppCompatActivity {
    private ProgressDialog dialog;

    private TextInputEditText currentpassword,newpassword,repassword;
    private Button save,cancel;
    private boolean IsError;
    String name,password1,newname,pass,username;
    String current_password,new_password,re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent intent = getIntent();
        password1  = intent.getStringExtra("password");
       SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        currentpassword = findViewById(R.id.currentpassword);
        newpassword = findViewById(R.id.newpassword);
        repassword = findViewById(R.id.repassword);

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
                Intent intent1 = new Intent(ChangePassword.this,Setting.class);
                startActivity(intent1);
                finish();
            }
        });
    }
    public void checkcontent(){
        current_password = currentpassword.getText().toString().trim();
        new_password = newpassword.getText().toString().trim();
        re_password = repassword.getText().toString().trim();

        if(current_password == null || current_password.isEmpty()){
            IsError = true;
            currentpassword.setError("Field cannot be empty");
            currentpassword.setFocusable(true);

        }else if(!current_password.equals(password1)){
            IsError = true;
            currentpassword.setError("Wrong password");
            currentpassword.setFocusable(true);

        }else if(new_password.length()<8){
            IsError = true;
            newpassword.setError("Minimum 8 characters required");
            newpassword.setFocusable(true);

        }

        else if(new_password == null || new_password.isEmpty()){
            IsError = true;
            newpassword.setError("Field cannot be empty");
            newpassword.setFocusable(true);

        }else if(re_password == null || re_password.isEmpty()){
            IsError = true;
            repassword.setError("Field cannot be empty");
            repassword.setFocusable(true);

        }else if(!new_password.equals(re_password)){
            IsError = true;
            repassword.setError("Password doesnot match");
            repassword.setFocusable(true);
        }
        else{
            IsError = false;
        }
        if(!IsError){
            savechanges();

        }

    }

    private void savechanges() {
        dialog = ProgressDialog.show(ChangePassword.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ChangePassword.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.UPDATE_PASSWORD;
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
                                Snackbar.make(findViewById(android.R.id.content),"Password not updated", Snackbar.LENGTH_SHORT)
                                        .show();
                            }else if (status.equals("200")) {
                                Snackbar.make(findViewById(android.R.id.content),"Password Updated Successfully", Snackbar.LENGTH_LONG)
                                        .show();
                                waitforsometime();


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
                params.put("password", new_password);

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

                Intent intent = new Intent(ChangePassword.this,MyProfile.class);
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

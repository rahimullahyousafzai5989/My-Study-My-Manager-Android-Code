package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    private ImageView userimage;
    private TextView username,date,firstname,lastname,email,profilesummary;
    private String username1,recievername,sendername,firstname1,lastname1,email1,profilesummary1,date1,userimage1,profilepic;
    private Button sendrequest,requestsent,acceptrequest;
    private String user_name;
    private ArrayList<UserInfoDataModel> userInfoDataModels;
    private JSONObject jsonObject;
    private String isaccepted;
    private Button friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoDataModels = new ArrayList<>();
        setContentView(R.layout.activity_user_detail);
        userimage = findViewById(R.id.userimage);
        username = findViewById(R.id.username);
        date= findViewById(R.id.date);
        firstname= findViewById(R.id.firstname);
        lastname= findViewById(R.id.lastname);
        friends = findViewById(R.id.friends);
       email= findViewById(R.id.email);
        profilesummary = findViewById(R.id.profilesummary);
        sendrequest = findViewById(R.id.sendrequest);
        requestsent = findViewById(R.id.requestsent);
        acceptrequest = findViewById(R.id.acceptrequest);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user_name = sharedPreferences.getString("username", "");
        sendrequest.setVisibility(View.GONE);
        requestsent.setVisibility(View.GONE);
        acceptrequest.setVisibility(View.GONE);
        friends.setVisibility(View.GONE);
        Intent intent = getIntent();
        username1 = intent.getStringExtra("username");
        firstname1 = intent.getStringExtra("firstname");
        lastname1 = intent.getStringExtra("lastname");
        email1 = intent.getStringExtra("email");
        profilesummary1 = intent.getStringExtra("profilesummary");
        date1 = intent.getStringExtra("date");
        username.setText(intent.getStringExtra("username"));
            date.setText(intent.getStringExtra("date"));
            date1 = intent.getStringExtra("date");
            profilepic = intent.getStringExtra("profilepic");
            firstname.setText(intent.getStringExtra("firstname"));
            lastname.setText(intent.getStringExtra("lastname"));
            email.setText(intent.getStringExtra("email"));
            profilesummary.setText(intent.getStringExtra("profilesummary"));

             if(username1.equals(user_name)){
                sendrequest.setVisibility(View.GONE);
                requestsent.setVisibility(View.GONE);
                acceptrequest.setVisibility(View.GONE);
            }else{
                sendrequest.setVisibility(View.GONE);
                requestsent.setVisibility(View.GONE);
                acceptrequest.setVisibility(View.GONE);
                 CheckVolleyRequest();

             }
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailActivity.this,CollectionActivity.class);
                intent.putExtra("username",username1);
                intent.putExtra("friend","yes");
                startActivity(intent);
            }
        });
        Glide.with(UserDetailActivity.this).load(Urls.DOMAIN+"/assets/profilepictures/"+profilepic).

                into(userimage);

        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog(UserDetailActivity.this,"Are you sure to send request.?","").show();
            }
        });
        acceptrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog3(UserDetailActivity.this,"Are you sure to accept request.?","").show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
    private void SendVolleyRequest() {
        dialog = ProgressDialog.show(UserDetailActivity.this, "Please Wait",
                "Sending Request...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(UserDetailActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.SEND_REQUEST;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog2(UserDetailActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(UserDetailActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                sendrequest.setVisibility(View.GONE);
                                requestsent.setVisibility(View.VISIBLE);
                                buildDialog2(UserDetailActivity.this, "Congratulations", "Request sent").show();

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
                buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",user_name);
                params.put("reciever",username1);
                params.put("firstname",firstname1);
                params.put("lastname",lastname1);
                params.put("email",email1);
                params.put("profilesummary",profilesummary1);
                params.put("date",date1);
                params.put("profilepic",profilepic);
                params.put("isaccepted","false");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void CheckVolleyRequest() {
        dialog = ProgressDialog.show(UserDetailActivity.this, "Please Wait",
                "Checking Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(UserDetailActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_REQUEST;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                               // buildDialog2(UserDetailActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                //Toast.makeText(UserDetailActivity.this,"No Record was found",Toast.LENGTH_LONG).show();
                                if(username1.equals(user_name)){
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.GONE);
                                    acceptrequest.setVisibility(View.GONE);
                                    friends.setVisibility(View.GONE);

                                }else if(recievername.equals(user_name)){
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.GONE);
                                    acceptrequest.setVisibility(View.VISIBLE);
                                    friends.setVisibility(View.GONE);

                                }



                            } else if (status.equals("200")) {
                                JSONArray request = reader.getJSONArray("response");
                                for(int i=0;i<request.length();i++){
                                    jsonObject = request.getJSONObject(i);
                                    isaccepted = jsonObject.getString("isaccepted");
                                }
                                if(isaccepted.equals("true")){
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.GONE);
                                    acceptrequest.setVisibility(View.GONE);
                                    friends.setVisibility(View.VISIBLE);

                                }else{
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.VISIBLE);
                                    acceptrequest.setVisibility(View.GONE);
                                    friends.setVisibility(View.GONE);
                                }

                                //buildDialog2(UserDetailActivity.this, "Congratulations", "Request sent").show();

                            }
                            else if (status.equals("201")) {
                                JSONArray request = reader.getJSONArray("response");
                                for(int i=0;i<request.length();i++){
                                    jsonObject = request.getJSONObject(i);
                                    isaccepted = jsonObject.getString("isaccepted");
                                }
                                if(isaccepted.equals("true")){
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.GONE);
                                    acceptrequest.setVisibility(View.GONE);
                                    friends.setVisibility(View.VISIBLE);
                                }
                                else{
                                    sendrequest.setVisibility(View.GONE);
                                    requestsent.setVisibility(View.GONE);
                                    acceptrequest.setVisibility(View.VISIBLE);
                                    friends.setVisibility(View.GONE);

                                }
                                //buildDialog2(UserDetailActivity.this, "Congratulations", "Request sent").show();

                            }
                            else if (status.equals("401")) {
                                sendrequest.setVisibility(View.VISIBLE);
                                requestsent.setVisibility(View.GONE);
                                acceptrequest.setVisibility(View.GONE);
                                //buildDialog2(UserDetailActivity.this, "Congratulations", "Request sent").show();

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
                //buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",user_name);
                params.put("reciever",username1);
                params.put("setparameter","and");
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

    private void AcceptVolleyRequest() {
        dialog = ProgressDialog.show(UserDetailActivity.this, "Please Wait",
                "Checking Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(UserDetailActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.ACCEPT_REQUEST;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog4(UserDetailActivity.this, "Error", "Internal Server Error").show();
                            } else if (status.equals("200")) {

                                buildDialog4(UserDetailActivity.this, "Congratulations", "You are now friends").show();

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
                //buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",user_name);
                params.put("reciever",username1);
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:

                super.onBackPressed();

                break;


        }


        return true;
    }




    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }
    public AlertDialog.Builder buildDialog4(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime2();

        return builder;
    }
    public AlertDialog.Builder buildDialog3(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AcceptVolleyRequest();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendVolleyRequest();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(UserDetailActivity.this,RequestActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    public void waitforsometime2(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(UserDetailActivity.this,FriendActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}

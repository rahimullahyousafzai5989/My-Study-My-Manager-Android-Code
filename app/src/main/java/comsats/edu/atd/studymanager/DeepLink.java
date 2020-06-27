package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeepLink extends AppCompatActivity {
    Toolbar toolbar;
    private TextView toolbartext;
    private String username;
    private ProgressDialog dialog;
    JSONObject jsonObject;
    private static String date2,dayvalue,time;
     public boolean isaction_mode = false;
    String userdate,user_name,firstname,lastname,email;
    String searchuser;
    String profilepic,profilesummary;


    private boolean isFirstVisit = false;
    private boolean isuserloggedin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        searchuser = ""+uri.getQueryParameter("user");

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("username", "")!=null ||!(sharedPreferences.getString("username", "").isEmpty())){
            username = sharedPreferences.getString("username", "");
        }
        if(username != null){
            username = sharedPreferences.getString("username", "");

        }

        isFirstVisit =  sharedPreferences.getBoolean("isFirstVisit",true);

        if(isFirstVisit == false){

            isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
            if(isuserloggedin){
                VolleyRequest1();
            }else{
                Intent intent1 = new Intent(DeepLink.this,LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        }else{
            Intent intent2 = new Intent(DeepLink.this,MainActivity.class);
            startActivity(intent2);
            finish();
        }



    }
    public void VolleyRequest1() {
        dialog = ProgressDialog.show(DeepLink.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(DeepLink.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.SEARCH_USERS;
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
                                buildDialog(DeepLink.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(DeepLink.this,"No Record was found",Toast.LENGTH_LONG).show();

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray collections = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < collections.length(); i++) {
                                    jsonObject = collections.getJSONObject(i);
                                    user_name = jsonObject.getString("username");
                                    userdate = jsonObject.getString("_id");
                                    firstname = jsonObject.getString("firstname");
                                    lastname = jsonObject.getString("lastname");
                                    email = jsonObject.getString("email");
                                    profilepic = jsonObject.getString("profilepic");
                                    profilesummary = jsonObject.getString("profilesummary");
                                    String timestamp = userdate.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    userdate = datebuilding(date);
                                    Intent intent = new Intent(DeepLink.this,UserDetailActivity.class);
                                    intent.putExtra("profilepic",profilepic);
                                    intent.putExtra("username",user_name);
                                    intent.putExtra("firstname",firstname);
                                    intent.putExtra("lastname",lastname);
                                    intent.putExtra("email",email);
                                    intent.putExtra("date",userdate);
                                    intent.putExtra("profilesummary",profilesummary);

                                    startActivity(intent);
                                    finish();
                                }


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
                buildDialog(DeepLink.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", searchuser);
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
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
    public static String datebuilding(long date) {
        //Log.d("JSON","Inside Date Building");
        Date date1 = new Date(date * 1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        date2 = sdf.format(date1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("E");
        dayvalue = sdf1.format(date1);
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
        time = sdf2.format(date1);
        String Complete_Date = dayvalue+"/"+date2;
        return Complete_Date;
    }
}

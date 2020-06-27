package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.CheckBox;
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

public class FriendActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView toolbartext;
    private String username;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    JSONObject jsonObject;
   private static String date2,dayvalue,time;
    ArrayList<UserInfoDataModel> arrayList;
    int counter=0;
    public boolean isaction_mode = false;

    ArrayList<UserInfoDataModel> userInfoDataModels;
    String userdate,user_name,firstname,lastname,email;
    String searchuser;
    String sender,reciever,isaccepted,date,profilepic,profilesummary;
    ArrayList<RequestDataModel> requestDataModels;
    UserAdapahter adaphter;
    FriendAdaphter adaphter2;
    MyRequestAdaphter myadaphter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        arrayList = new ArrayList<>();
        toolbar = findViewById(R.id.mytoolbar);
        mylayout1 = findViewById(R.id.friendlayoutwrapper);
        setSupportActionBar(toolbar);
        requestDataModels = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        FindFriendsVolleyRequest();
          }

    private void FindFriendsVolleyRequest() {
        dialog = ProgressDialog.show(FriendActivity.this, "Please Wait",
                "Checking Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(FriendActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.FIND_FRIENDS;

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
                                buildDialog2(FriendActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("401")){
                                Toast.makeText(FriendActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else {
                                mylayout1.setVisibility(View.GONE);
                                JSONArray request = reader.getJSONArray("response");

                                for (int i = 0; i < request.length(); i++) {
                                    jsonObject = request.getJSONObject(i);
                                    sender = jsonObject.getString("sender");
                                    reciever = jsonObject.getString("reciever");
                                        if(sender.equals(username)){
                                            user_name = reciever;
                                        }else{
                                            user_name = sender;
                                        }
                                    userdate = jsonObject.getString("_id");
                                    firstname = jsonObject.getString("firstname");
                                    lastname = jsonObject.getString("lastname");
                                    email = jsonObject.getString("email");
                                    profilesummary = jsonObject.getString("profilesummary");
                                    profilepic = jsonObject.getString("profilepic");

                                    String timestamp = userdate.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    userdate = datebuilding(date);
                                    UserInfoDataModel dataModel = new UserInfoDataModel(firstname, lastname,user_name,userdate,email,profilepic,profilesummary);
                                    arrayList.add(dataModel);
                                }
                                adaphter2 = new FriendAdaphter(arrayList,FriendActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.friendlist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(FriendActivity.this));
                                recyclerView.setAdapter(adaphter2);

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
                params.put("sender",username);
                params.put("reciever",username);
                params.put("setparameter","or");
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

    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }

    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(FriendActivity.this,FriendActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.searchmenu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:

                    super.onBackPressed();

                break;

            case R.id.nav_search:

                final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if(!(arrayList == null)){
                            arrayList.clear();
                        }
                       // Toast.makeText(FriendActivity.this, ""+query, Toast.LENGTH_SHORT).show();
                        searchView.clearFocus();
                        searchuser = query.trim();
                        VolleyRequest1();
                        return true;

                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        //if(adaphter == null){
                           // Toast.makeText(FriendActivity.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
                       // }else {
                          //  adaphter.getFilter().filter(newText);
                        //}
                        return false;
                    }
                });
                break;


        }


        return true;
    }

    public void VolleyRequest1() {
       dialog = ProgressDialog.show(FriendActivity.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(FriendActivity.this);
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
                                buildDialog(FriendActivity.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(FriendActivity.this,"No Record was found",Toast.LENGTH_LONG).show();
                               mylayout1.setVisibility(View.VISIBLE);

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                               mylayout1.setVisibility(View.GONE);
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
                                   UserInfoDataModel dataModel = new UserInfoDataModel(firstname, lastname,user_name,userdate,email,profilepic,profilesummary);
                                   arrayList.add(dataModel);
                                }
                               adaphter = new UserAdapahter(arrayList,FriendActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.friendlist);
                               recyclerView.setLayoutManager(new LinearLayoutManager(FriendActivity.this));
                               recyclerView.setAdapter(adaphter);

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
                buildDialog(FriendActivity.this, "Oops..!", "Error occured").show();
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
    public void prepareselection(View v,int position){
        if(((CheckBox) v).isChecked()){
            userInfoDataModels.add(arrayList.get(position));
            counter=counter+1;
            updatecounter(counter);

        }else{
            userInfoDataModels.remove(arrayList.get(position));
            counter=counter-1;
            updatecounter(counter);

        }
    }
    public void clearactionmode(){
        isaction_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.pdffilemenu);
        toolbar.setTitle("Study Manager");
        toolbartext.setVisibility(View.GONE);
        isaction_mode = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        userInfoDataModels.clear();
    }
    public void updatecounter(int counter){
        if(counter <= 0) {
            toolbartext.setText("0 items selected");
        }
        else{
            toolbartext.setText(counter+" items selected");

        }

    }

    @Override
    public void onBackPressed() {
        if(isaction_mode){
            clearactionmode();
            adaphter.notifyDataSetChanged();

        }
        else{
            super.onBackPressed();

        }
    }
}

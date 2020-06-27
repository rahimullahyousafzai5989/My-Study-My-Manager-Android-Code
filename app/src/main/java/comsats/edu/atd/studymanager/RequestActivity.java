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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity implements View.OnLongClickListener {
    Toolbar toolbar;
    private ProgressDialog dialog;
    JSONObject jsonObject;
    int counter=0;
    public boolean isaction_mode = false;
    private TextView toolbartext;
    MyRequestAdaphter adaphter;
    private String user_name;
    LinearLayout mylayout;
    String sender,reciever,isaccepted,date,profilepic,profilesummary;
    private static String date2,dayvalue,time;
    private ArrayList<RequestDataModel> requestDataModels,requestDataModelArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestDataModels = new ArrayList<>();
        requestDataModelArrayList =new ArrayList<>();
        setContentView(R.layout.activity_request);
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        toolbar = findViewById(R.id.mytoolbar);
        mylayout = findViewById(R.id.mylayouta);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user_name = sharedPreferences.getString("username", "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CheckVolleyRequest();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pdffilemenu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_delete:
                if(counter == 0){
                    Toast.makeText(RequestActivity.this, "Please select atleast one item", Toast.LENGTH_SHORT).show();
                }else {
                    buildDialog3(RequestActivity.this).show();
                }
                break;
            case android.R.id.home:

                if(!isaction_mode){
                    super.onBackPressed();
                }else{
                    clearactionmode();
                    adaphter.notifyDataSetChanged();
                }
                break;
            case R.id.delete_nav:
                if(adaphter == null){
                    Toast.makeText(RequestActivity.this, "Please add data", Toast.LENGTH_SHORT).show();
                }else {
                    toolbar.getMenu().clear();
                    toolbar.setTitle("");
                    toolbar.inflateMenu(R.menu.delete_items);
                    toolbartext.setVisibility(View.VISIBLE);
                    isaction_mode = true;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    adaphter.notifyDataSetChanged();
                }

                break;
            case R.id.nav_search:

                androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(adaphter == null){
                            Toast.makeText(RequestActivity.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            adaphter.getFilter().filter(newText);
                        }
                        return true;
                    }
                });
                break;


        }


        return true;
    }
    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setMessage("following request/s would be deleted");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyRequestAdaphter myadapahter = adaphter;
                myadapahter.updateAdaphter(requestDataModelArrayList);
                DeleteVolleyRequest(requestDataModelArrayList);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }

    private void CheckVolleyRequest() {
        dialog = ProgressDialog.show(RequestActivity.this, "Please Wait",
                "Checking Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(RequestActivity.this);
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
                                buildDialog2(RequestActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("401")){
                                Toast.makeText(RequestActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else {
                                mylayout.setVisibility(View.GONE);
                                JSONArray request = reader.getJSONArray("response");

                                for (int i = 0; i < request.length(); i++) {
                                    jsonObject = request.getJSONObject(i);
                                    sender = jsonObject.getString("sender");
                                    reciever = jsonObject.getString("reciever");
                                    isaccepted = jsonObject.getString("isaccepted");
                                    date = jsonObject.getString("_id");
                                    profilepic = jsonObject.getString("profilepic");
                                    String timestamp = date.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long mydate = Long.parseLong(String.valueOf(bi));
                                    date = datebuilding(mydate);
                                    RequestDataModel dataModel = new RequestDataModel(sender, reciever,date,isaccepted,profilepic);
                                    requestDataModels.add(dataModel);

                                }
                                adaphter = new MyRequestAdaphter(requestDataModels,RequestActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.requestlist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(RequestActivity.this));
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
                //buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",user_name);
                params.put("reciever",user_name);
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
                Intent intent = new Intent(RequestActivity.this,RequestActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
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
        String Complete_Date = dayvalue+"/"+date2+"/"+time;
        return Complete_Date;
    }
    public void clearactionmode(){
        isaction_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.pdffilemenu);
        toolbar.setTitle("Study Manager");
        toolbartext.setVisibility(View.GONE);
        isaction_mode = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        requestDataModelArrayList.clear();
    }
    private void DeleteVolleyRequest(final ArrayList<RequestDataModel> requestDataModels) {
        dialog = ProgressDialog.show(RequestActivity.this, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(RequestActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_REQUEST;

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
                                buildDialog2(RequestActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(RequestActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                clearactionmode();
                                buildDialog2(RequestActivity.this, "Congratulations", "Request deleted successfully").show();

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
                buildDialog2(RequestActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String data = gson.toJson(requestDataModelArrayList);
                Log.d("1122",""+data);
                Log.d("112233",""+requestDataModelArrayList);
                params.put("data", data);

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
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.delete_items);
        toolbartext.setVisibility(View.VISIBLE);
        isaction_mode = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adaphter.notifyDataSetChanged();
        return true;
    }

    public void prepareselection(View v,int position){
        if(((CheckBox) v).isChecked()){
            requestDataModelArrayList.add(requestDataModels.get(position));
            counter=counter+1;
            updatecounter(counter);

        }else{
            requestDataModelArrayList.remove(requestDataModels.get(position));
            counter=counter-1;
            updatecounter(counter);

        }
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

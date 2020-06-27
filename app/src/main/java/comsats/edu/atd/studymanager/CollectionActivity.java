package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class CollectionActivity extends AppCompatActivity implements View.OnLongClickListener {

    private FloatingActionButton fb;
    private String m_Text;
    private static String date2,dayvalue,time;
    private String username;
    private String collection_name;
    private String collection_date;
    JSONObject jsonObject;
    private int counter=0;
    public boolean isaction_mode;
    public boolean isedit_mode;
    private ArrayList<CollectionDataModel> CollectionnArrayList,collectionDataModelArrayList;
    private LinearLayout mylayout;
    private ProgressDialog dialog;
    private CollectionAdaphter adaphter;
    private TextView toolbartext;
    Toolbar toolbar;
    String friend=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mylayout = findViewById(R.id.collectionwrapper);
        fb = findViewById(R.id.fb_c);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

            Intent intent = getIntent();
            if(intent.getStringExtra("friend")!=null){
                username = intent.getStringExtra("username");
                friend = intent.getStringExtra("friend");
                //Toast.makeText(this, ""+username, Toast.LENGTH_SHORT).show();
            }

        CollectionnArrayList = new ArrayList<>();
        collectionDataModelArrayList = new ArrayList<>();
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        if(friend !=null && friend.equals("yes")){
           fb.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        VolleyRequest2(username);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog().show();
            }
        });

    }
    public void VolleyRequest2(final String username) {
        dialog = ProgressDialog.show(CollectionActivity.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(CollectionActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.VIEW_COLLECTION;

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
                                buildDialog(CollectionActivity.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(CollectionActivity.this,"No Record was found",Toast.LENGTH_LONG).show();
                                mylayout.setVisibility(View.VISIBLE);

                            } else if (status.equals("200")) {
                                mylayout.setVisibility(View.GONE);
                                JSONArray collections = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < collections.length(); i++) {
                                    jsonObject = collections.getJSONObject(i);
                                    collection_name = jsonObject.getString("collectionname");
                                    collection_date = jsonObject.getString("_id");
                                    String timestamp = collection_date.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    collection_date = datebuilding(date);
                                    CollectionDataModel dataModel = new CollectionDataModel(collection_name, collection_date);
                                    CollectionnArrayList.add(dataModel);

                                }
                                adaphter = new CollectionAdaphter(CollectionActivity.this,CollectionnArrayList,friend);
                                RecyclerView recyclerView =findViewById(R.id.collectionlist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(CollectionActivity.this));
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
                buildDialog(CollectionActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
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

    public AlertDialog.Builder buildDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionActivity.this);
        builder.setTitle("Enter Collection Name");
        builder.setMessage("(e.g Data warehousing)");
// Set up the input
        final EditText input = new EditText(CollectionActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString().trim();
                if(m_Text.equals(null) || m_Text.isEmpty() || m_Text == ""){
                    Toast.makeText(CollectionActivity.this,"Please Enter a Collection name",Toast.LENGTH_LONG).show();
                }else{
                    buildDialog2().show();
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;

    }


    public AlertDialog.Builder buildDialog2(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();

        return builder;
    }
    public AlertDialog.Builder buildDialog2() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionActivity.this);
        builder.setTitle("Are you sure.?");
        builder.setMessage(m_Text + " collection will be created");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                VolleyRequest1(m_Text, username);


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        return builder;
    }
    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setMessage("following collection/s would be deleted");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CollectionAdaphter myadapahter = adaphter;
                myadapahter.updateAdaphter(collectionDataModelArrayList);
                DeleteVolleyRequest(collectionDataModelArrayList);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(friend !=null && friend.equals("yes")){
            menuInflater.inflate(R.menu.searchmenu,menu);

        }else{
            menuInflater.inflate(R.menu.pdffilemenu,menu);

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_delete:
                if(counter == 0){
                    Toast.makeText(CollectionActivity.this, "Please select atleast one item", Toast.LENGTH_SHORT).show();
                }else {
                    buildDialog3(CollectionActivity.this).show();
                }
                break;
            case android.R.id.home:
                if(!isaction_mode){
                    Intent intent = new Intent(CollectionActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    clearactionmode();
                    adaphter.notifyDataSetChanged();

                }
                break;
            case R.id.delete_nav:
                    if(adaphter == null){
                        Toast.makeText(CollectionActivity.this, "Please add data", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CollectionActivity.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            adaphter.getFilter().filter(newText);
                        }
                        return true;
                    }
                });
                break;

            case R.id.edit_nav:
                if(adaphter == null){
                    Toast.makeText(CollectionActivity.this, "Please add data", Toast.LENGTH_SHORT).show();
                }else {
                     isedit_mode = true;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    adaphter.notifyDataSetChanged();
                }

                break;

        }


        return true;
    }

    private void DeleteVolleyRequest(final ArrayList<CollectionDataModel> collectionDataModels) {
        dialog = ProgressDialog.show(CollectionActivity.this, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(CollectionActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_COLLECTION;

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
                                buildDialog2(CollectionActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(CollectionActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                clearactionmode();
                                buildDialog2(CollectionActivity.this, "Congratulations", "Collection deleted successfully").show();

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
                buildDialog(CollectionActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String data = gson.toJson(collectionDataModelArrayList);
                Log.d("1122",""+data);
                Log.d("112233",""+collectionDataModelArrayList);

                params.put("data", data);
                params.put("username",username);
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

    public void clearactionmode(){
        isaction_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.pdffilemenu);
        toolbar.setTitle("Study Manager");
        toolbartext.setVisibility(View.GONE);
        isaction_mode = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        collectionDataModelArrayList.clear();
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
            collectionDataModelArrayList.add(CollectionnArrayList.get(position));
            counter=counter+1;
            updatecounter(counter);

        }else{
            collectionDataModelArrayList.remove(CollectionnArrayList.get(position));
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
        else if(isedit_mode){
            isedit_mode = false;
            adaphter.notifyDataSetChanged();
        }

        else{
            Intent intent = new Intent(CollectionActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }

    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(CollectionActivity.this,CollectionActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    public void VolleyRequest1(final String m_Text, final String username) {
        // Instantiate the RequestQueue.
        dialog = ProgressDialog.show(CollectionActivity.this, "Please Wait",
                "Saving Data...");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(CollectionActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.ADD_COLLECTION;

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
                                buildDialog(CollectionActivity.this, "500", "Internal Server Error").show();
                            }

                            else if (status.equals("200")) {
                                buildDialogdatachanged(CollectionActivity.this, "Collection saved", "Collection is created").show();

                            }else if(status.equals("409")){
                                buildDialogdatachanged(CollectionActivity.this, "Collection already exists", "please try again with a different name").show();

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
                buildDialog(CollectionActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("collecname", m_Text);

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
                Intent intent = new Intent(CollectionActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();

            }
        });

        return builder;
    }


    public AlertDialog.Builder buildDialogdatachanged(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CollectionActivity.this,CollectionActivity.class);
                startActivity(intent);
                finish();
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
        String Complete_Date = dayvalue+"/"+date2+"/"+time;
        return Complete_Date;
    }

}

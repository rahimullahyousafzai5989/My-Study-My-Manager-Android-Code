package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewVideos extends AppCompatActivity implements View.OnLongClickListener {

    private FloatingActionButton fb;
    private String collectionname,username;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    private JSONObject jsonObject;
    private String file_name,file_title;
    private VideoAdaphter adaphter;
    Toolbar toolbar;
    public boolean isaction_mode = false;
    public boolean isedit_mode = false;
    int counter;
    private TextView toolbartext;
    private String friend;
    private ArrayList<VideoContentModel> contentModels,contentModelslist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_videos);
        fb = findViewById(R.id.fb_3);
        Intent intent = getIntent();
        if(friend !=null && friend.equals("yes")){
            fb.setVisibility(View.GONE);
        }
        if(intent.getStringExtra("friend") != null){
            friend = intent.getStringExtra("friend");
        }
        mylayout1 = findViewById(R.id.mylayout4);
        contentModels = new ArrayList<>();
        contentModelslist = new ArrayList<>();
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collectionname = intent.getStringExtra("collectionname");
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        VolleyRequest2(username,collectionname);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewVideos.this,EnterFileContents.class);
                intent.putExtra("collectionname",collectionname);
                intent.putExtra("identifier","video");
                startActivity(intent);
                finish();
            }
        });

    }
    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setMessage("following file/s would be deleted");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                VideoAdaphter myadapahter = adaphter;
                myadapahter.updateAdaphter(contentModelslist);
                DeleteVolleyRequest(contentModelslist);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(friend !=null && friend.equals("yes")){
            menuInflater.inflate(R.menu.searchmenu,menu);

        }else{
            menuInflater.inflate(R.menu.pdffilemenu,menu);

        } return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_delete:
                if(counter == 0){
                    Toast.makeText(ViewVideos.this, "Please select atleast one item", Toast.LENGTH_SHORT).show();
                }else {

                    buildDialog3(ViewVideos.this).show();
                }
                break;
            case android.R.id.home:
                if(adaphter ==null){
                    super.onBackPressed();
                }else{
                    clearactionmode();
                    adaphter.notifyDataSetChanged();
                }
                break;
            case R.id.delete_nav:
                    if(adaphter == null){
                        Toast.makeText(ViewVideos.this, "Please add data", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ViewVideos.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            adaphter.getFilter().filter(newText);
                        }return true;
                    }
                });
                break;

            case R.id.edit_nav:
                if(adaphter == null){
                    Toast.makeText(ViewVideos.this, "Please add data", Toast.LENGTH_SHORT).show();
                }else {
                    isedit_mode = true;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    adaphter.notifyDataSetChanged();
                }

                break;

        }


        return true;
    }

    private void DeleteVolleyRequest(final ArrayList<VideoContentModel> contentModelslist) {
        dialog = ProgressDialog.show(ViewVideos.this, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ViewVideos.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_VIDEOS;

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
                                buildDialog2(ViewVideos.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(ViewVideos.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                clearactionmode();
                                buildDialog2(ViewVideos.this, "Congratulations", "File deleted successfully").show();

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
                buildDialog(ViewVideos.this, "Oops..!", "Error occured","").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String data = gson.toJson(contentModelslist);
                Log.d("1122",""+data);
                Log.d("112233",""+contentModelslist);

                params.put("data", data);
                params.put("username",username);
                params.put("collectionname",collectionname);

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

    public void prepareselection(View v,int position){
        if(((CheckBox) v).isChecked()){
            contentModelslist.add(contentModels.get(position));
            // Toast.makeText(this, ""+contentModelslist.size(), Toast.LENGTH_SHORT).show();
            counter=counter+1;
            updatecounter(counter);

        }else{
            contentModelslist.remove(contentModels.get(position));
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
    public void clearactionmode(){
        isaction_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.pdffilemenu);
        toolbar.setTitle("Study Manager");
        toolbartext.setVisibility(View.GONE);
        isaction_mode = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        contentModelslist.clear();
    }


    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status.equals("200")){
                    Intent intent = new Intent(ViewVideos.this,SelectPhoto.class);
                    intent.putExtra("collectionname",collectionname);
                    startActivity(intent);
                    finish();
                }

            }
        });

        return builder;
    }
    public void VolleyRequest2(final String username,final String collectionname) {
        dialog = ProgressDialog.show(ViewVideos.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ViewVideos.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.VIEW_VIDEOS;

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
                                buildDialog(ViewVideos.this, "500", "Internal Server Error","").show();
                            }else if(status.equals("404")){
                                Toast.makeText(ViewVideos.this,"No Record was found",Toast.LENGTH_LONG).show();
                                mylayout1.setVisibility(View.VISIBLE);

                            } else if (status.equals("200")) {
                                mylayout1.setVisibility(View.GONE);
                                JSONArray photodetails = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < photodetails.length(); i++) {
                                    jsonObject = photodetails.getJSONObject(i);
                                    file_name = jsonObject.getString("filename");
                                    file_title = jsonObject.getString("fileTitle");
                                    Log.d("2345",""+file_name);

                                    VideoContentModel dataModel = new VideoContentModel(file_name,file_title);
                                    contentModels.add(dataModel);

                                }
                                RecyclerView recyclerView = findViewById(R.id.videolist);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewVideos.this,2);
                                recyclerView.setLayoutManager(gridLayoutManager);
                                adaphter = new VideoAdaphter(ViewVideos.this,contentModels);
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
                buildDialog(ViewVideos.this, "Oops..!", "Error occured","").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("collectionname",collectionname);
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

                Intent intent = new Intent(ViewVideos.this,ViewVideos.class);
                intent.putExtra("collectionname",collectionname);
                startActivity(intent);
                finish();
            }
        }, 1000);
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
}

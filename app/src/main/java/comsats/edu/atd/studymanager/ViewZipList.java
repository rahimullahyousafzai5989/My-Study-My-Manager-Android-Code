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

public class ViewZipList extends AppCompatActivity implements View.OnLongClickListener {
    private String filePath;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    private FloatingActionButton fb;
    JSONObject jsonObject;
    String collectionname;
    public boolean isaction_mode = false;
    public boolean isedit_mode = false;
    private ZipFileAdaphter adaphter;
    private ArrayList<ZipFileContentModelList> zipFileContentModel,zipFileContentModelLists;
    Toolbar toolbar;
    private TextView toolbartext;
    int counter = 0;
    private String m_Text;
    private static String date2,dayvalue,time;
    private String username,friend;
    Intent pintent;

    String file_name,file_data,file_date,filetitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_zip_list);
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

        zipFileContentModelLists = new ArrayList<>();
        zipFileContentModel = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mylayout1 = findViewById(R.id.mylayouta);
        fb = findViewById(R.id.fb_a);
        pintent= getIntent();
        if(pintent.getStringExtra("friend") != null){
            friend = pintent.getStringExtra("friend");
        }
        username = sharedPreferences.getString("username", "");
        collectionname =pintent.getStringExtra("collectionname");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        VolleyRequest2(username,collectionname);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewZipList.this,EnterFileContents.class);
                intent.putExtra("collectionname",collectionname);
                intent.putExtra("identifier","zip");
                startActivity(intent);
                finish();
            }
        });
        if(friend !=null && friend.equals("yes")){
            fb.setVisibility(View.GONE);
        }
    }
    public void VolleyRequest2(final String username,final String collectionname) {
        dialog = ProgressDialog.show(ViewZipList.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ViewZipList.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.VIEW_ZIPFILES;

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
                                buildDialog(ViewZipList.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(ViewZipList.this,"No Record was found",Toast.LENGTH_LONG).show();
                                mylayout1.setVisibility(View.VISIBLE);

                            } else if (status.equals("200")) {
                                mylayout1.setVisibility(View.GONE);
                                JSONArray collections = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < collections.length(); i++) {
                                    jsonObject = collections.getJSONObject(i);
                                    file_name = jsonObject.getString("fileTitle");
                                    file_date = jsonObject.getString("_id");
                                    filetitle = jsonObject.getString("filename");
                                    String timestamp = file_date.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    file_date = datebuilding(date);
                                    ZipFileContentModelList dataModel = new ZipFileContentModelList(file_name, file_data,file_date,filetitle);
                                    zipFileContentModelLists.add(dataModel);

                                }
                                adaphter = new ZipFileAdaphter(ViewZipList.this,zipFileContentModelLists);
                                RecyclerView recyclerView = findViewById(R.id.zipfilelist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ViewZipList.this));
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
                buildDialog(ViewZipList.this, "Oops..!", "Error occured").show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(friend !=null && friend.equals("yes")){
            menuInflater.inflate(R.menu.searchmenu,menu);

        }else{
            menuInflater.inflate(R.menu.pdffilemenu,menu);

        }return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_delete:
                if(counter == 0){
                    Toast.makeText(ViewZipList.this, "Please select atleast one item", Toast.LENGTH_SHORT).show();
                }else {
                    buildDialog3(ViewZipList.this).show();
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
                        Toast.makeText(ViewZipList.this, "Please add data", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ViewZipList.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            adaphter.getFilter().filter(newText);
                        }
                        return true;
                    }
                });
                break;

            case R.id.edit_nav:
                if(adaphter == null){
                    Toast.makeText(ViewZipList.this, "Please add data", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ViewZipList.this, "Please Select a file", Toast.LENGTH_SHORT).show();
                    isedit_mode = true;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    adaphter.notifyDataSetChanged();
                }

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
    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setMessage("following file/s would be deleted");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ZipFileAdaphter myadapahter = adaphter;
                myadapahter.updateAdaphter(zipFileContentModel);
                DeleteVolleyRequest(zipFileContentModel);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }


    private void DeleteVolleyRequest(final ArrayList<ZipFileContentModelList> pdfselectionlist) {
        dialog = ProgressDialog.show(ViewZipList.this, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(ViewZipList.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_ZIPFILES;

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
                                buildDialog2(ViewZipList.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(ViewZipList.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                clearactionmode();
                                buildDialog2(ViewZipList.this, "Congratulations", "File deleted successfully").show();

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
                buildDialog(ViewZipList.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String data = gson.toJson(pdfselectionlist);
                Log.d("1122",""+data);
                Log.d("112233",""+pdfselectionlist);

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

    public void clearactionmode(){
        isaction_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.pdffilemenu);
        toolbar.setTitle("Study Manager");
        toolbartext.setVisibility(View.GONE);
        isaction_mode = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        zipFileContentModel.clear();
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
            zipFileContentModel.add(zipFileContentModelLists.get(position));
            counter=counter+1;
            updatecounter(counter);

        }else{
            zipFileContentModel.remove(zipFileContentModelLists.get(position));
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

        }else if(isedit_mode){
            isedit_mode = false;
            adaphter.notifyDataSetChanged();
        }
        else{
            super.onBackPressed();

        }
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(ViewZipList.this,ViewZipList.class);
                intent.putExtra("collectionname",collectionname);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}

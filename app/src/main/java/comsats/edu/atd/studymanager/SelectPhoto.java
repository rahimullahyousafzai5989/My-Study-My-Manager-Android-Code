package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
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
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectPhoto extends AppCompatActivity implements View.OnLongClickListener {

    FloatingActionButton btn;
    int success = 0;
    int fail = 0;
    private ProgressDialog dialog;
    private PhotoAdaphter adaphter;
    private ArrayList<PhotoContentModel> contentModels,contentModelslist;
    Toolbar toolbar;
    public boolean isaction_mode = false;
    int counter;
    private TextView toolbartext;

    private LinearLayout mylayout1;
    ArrayList<String> filePaths;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private String filePath,mimetype,name,bucketname;
    JSONObject jsonObject;
    String file_name;

    private FrameLayout frame_layout1;
    private TextView textView;
   int size = 0;
   private String username,friend;
   private static String collectionname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePaths = new ArrayList<>();
        setContentView(R.layout.activity_select_photo);
        Intent intent = getIntent();
        mylayout1 = findViewById(R.id.mylayout3);
        contentModels = new ArrayList<>();
        contentModelslist = new ArrayList<>();
        collectionname = intent.getStringExtra("collectionname");
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        toolbartext = findViewById(R.id.toolbartext);
        toolbartext.setVisibility(View.GONE);
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(intent.getStringExtra("friend") != null){
            friend = intent.getStringExtra("friend");
        }
        VolleyRequest2(username,collectionname);
        btn = findViewById(R.id.btn_select_pics);
        if(friend !=null && friend.equals("yes")){
            btn.setVisibility(View.GONE);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkpermisiion();
            }
        });


    }
    public void checkpermisiion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(SelectPhoto.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(SelectPhoto.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            }
            else{
                UploadPics();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(requestCode == 100 && (grantResults[i] == PackageManager.PERMISSION_GRANTED)){

                UploadPics();
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

                }
            }
        }

    }
    public void UploadPics(){
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(false)
                .setShowImages(true)
                .setMaxSelection(-1)
                .setSkipZeroSizeFiles(true)
                .setIgnoreNoMedia(true)
                .build());
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1
                && resultCode == RESULT_OK
                && data != null){
                mediaFiles.clear();
                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                size = mediaFiles.size();
                for(int i = 0;i<mediaFiles.size();i++){
                MediaFile file = mediaFiles.get(i);
                filePath = file.getPath();

                    uploadNow(i+1);
                }
            }

        }

    private void uploadNow(final int counter){
        Log.d("112233",""+filePath);
        File fileToUpload = new File(filePath);
        Log.d("112233",""+fileToUpload);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        username =  sharedPreferences.getString("username",null);
        String mycollectionname= collectionname;
        final ProgressDialog progressDialog = new ProgressDialog(SelectPhoto.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Ion.with(SelectPhoto.this)
                .load("POST", Urls.UPLOAD_PHOTO)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {

                        double progress = (100.0 * uploaded) / total;

                        progressDialog.setMessage("Uploading "+counter+" of "+size+" files " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "image/jpeg", fileToUpload)
                .setMultipartParameter("username",username)
                .setMultipartParameter("collectionname",mycollectionname)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        progressDialog.dismiss();
                        Log.i("312333", result + "  " + e + "");
                        if (result != null) {
                            try {
                                JSONObject mainObject = new JSONObject(result);
                                String status = mainObject.getString("status");
                                if (status.equals("500")) {
                                    fail++;
                                }

                                else if (status.equals("200")) {
                                    success++;


                                }
                                else if(status.equals("409")){
                                    buildDialog(SelectPhoto.this, "File cannnot be saved", "File with same name already exists","200").show();

                                }

                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                            if(success == size){
                                buildDialog(SelectPhoto.this, "", "Files Uploaded Successfully","200").show();

                            }else if(fail==size){
                                buildDialog(SelectPhoto.this, "500", "Internal Server Error","500").show();

                            }
                        } else {
                            buildDialog(SelectPhoto.this, "404", "Record Not Saved","404").show();
                        }
                    }
                });
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status.equals("200")){
                    Intent intent = new Intent(SelectPhoto.this,SelectPhoto.class);
                    intent.putExtra("collectionname",collectionname);
                    startActivity(intent);
                    finish();
                }

            }
        });

        return builder;
    }
    public void VolleyRequest2(final String username,final String collectionname) {
        dialog = ProgressDialog.show(SelectPhoto.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(SelectPhoto.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.VIEW_PHOTOS;

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
                                buildDialog(SelectPhoto.this, "500", "Internal Server Error","").show();
                            }else if(status.equals("404")){
                                Toast.makeText(SelectPhoto.this,"No Record was found",Toast.LENGTH_LONG).show();
                                mylayout1.setVisibility(View.VISIBLE);

                            } else if (status.equals("200")) {
                                mylayout1.setVisibility(View.GONE);
                                JSONArray photodetails = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < photodetails.length(); i++) {
                                    jsonObject = photodetails.getJSONObject(i);
                                    file_name = jsonObject.getString("filename");
                                    Log.d("2345",""+file_name);

                                   PhotoContentModel dataModel = new PhotoContentModel(file_name);
                                    contentModels.add(dataModel);

                                }
                                RecyclerView recyclerView = findViewById(R.id.photolist);
                                adaphter = new PhotoAdaphter(SelectPhoto.this,contentModels);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(SelectPhoto.this,2);
                                recyclerView.setLayoutManager(gridLayoutManager);

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
                buildDialog(SelectPhoto.this, "Oops..!", "Error occured","").show();
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
                PhotoAdaphter myadapahter = adaphter;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(friend !=null && friend.equals("yes")){
            menuInflater.inflate(R.menu.searchmenu,menu);

        }else{
            menuInflater.inflate(R.menu.pdffilemenu,menu);

        }  return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_delete:
                if(counter == 0){
                    Toast.makeText(SelectPhoto.this, "Please select atleast one item", Toast.LENGTH_SHORT).show();
                }else {

                    buildDialog3(SelectPhoto.this).show();
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
                        Toast.makeText(SelectPhoto.this, "Please add data", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SelectPhoto.this, "Please add data to apply search", Toast.LENGTH_SHORT).show();
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



    private void DeleteVolleyRequest(final ArrayList<PhotoContentModel> contentModelslist) {
        dialog = ProgressDialog.show(SelectPhoto.this, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(SelectPhoto.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_PHOTOS;

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
                                buildDialog2(SelectPhoto.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(SelectPhoto.this,"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                clearactionmode();
                                buildDialog2(SelectPhoto.this, "Congratulations", "File deleted successfully").show();

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
                buildDialog(SelectPhoto.this, "Oops..!", "Error occured","").show();
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
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent(SelectPhoto.this,SelectPhoto.class);
                intent.putExtra("collectionname",collectionname);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}

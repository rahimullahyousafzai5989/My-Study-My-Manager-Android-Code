package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyProfile extends AppCompatActivity{
  private String username;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    JSONObject jsonObject;
    private static String date2,dayvalue,time;
    ArrayList<UserInfoDataModel> arrayList;
    public boolean isaction_mode = false;

    ImageView useriamge,changeprofilepic,editprofilepic;
    TextView uname,fname,lname,useremail,summary,datejoined;
    String userdate,user_name,firstname,lastname,email;
    String searchuser;
    String profilepic,profilesummary,password;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    int size = 0;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        useriamge = findViewById(R.id.userimage);
        changeprofilepic = findViewById(R.id.editprofilepic);
        editprofilepic = findViewById(R.id.editprofilepic);
        editprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            checkpermisiion();
            }
        });
        uname = findViewById(R.id.username);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        useremail = findViewById(R.id.email);
        summary = findViewById(R.id.profilesummary);
        datejoined = findViewById(R.id.datejoined);
        mylayout1 = findViewById(R.id.profilewrapper);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        VolleyRequest();
    }
    public void checkpermisiion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MyProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MyProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
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
                .setMaxSelection(1)
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
                buildDialog3(MyProfile.this).show();

            }
        }

    }

    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               uploadNow();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
    private void uploadNow() {
        final ProgressDialog progressDialog = new ProgressDialog(MyProfile.this);
        progressDialog.setMessage("Uploading file");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final File file = new File(filePath);
        Ion.with(MyProfile.this)
                .load("POST", Urls.UPDATE_PROFILE_PIC)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        double progress = (100.0 * uploaded) / total;
                        progressDialog.setMessage("Uploading profile picture.. " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "application/pdf", file)
                .setMultipartParameter("username",username)

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
                                    buildDialog(MyProfile.this, "500", "Internal Server Error","500").show();

                                }

                                else if (status.equals("200")) {
                                    buildDialog(MyProfile.this,"Congratulations","profile pic updated","200").show();
                                    waitforsometime();
                                }
                                else if(status.equals("409")){

                                    buildDialog(MyProfile.this, "Account not created", "User with same name already exists","409").show();

                                }
                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                        } else {
                            buildDialog(MyProfile.this, "404", "Record Not Saved","404").show();
                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profilemenu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:

                super.onBackPressed();

                break;

            case R.id.nav_settings:
                Intent intent = new Intent(MyProfile.this,Setting.class);
                intent.putExtra("firstname",firstname);
                intent.putExtra("lastname",lastname);
                intent.putExtra("username",username);
                intent.putExtra("email",email);
                intent.putExtra("profilesummary",profilesummary);
                intent.putExtra("password",password);
                startActivity(intent);



        }


        return true;
    }

    public void VolleyRequest() {
        dialog = ProgressDialog.show(MyProfile.this, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(MyProfile.this);
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
                                buildDialog(MyProfile.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(MyProfile.this,"No Record was found",Toast.LENGTH_LONG).show();
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
                                    password = jsonObject.getString("password");
                                    profilepic = jsonObject.getString("profilepic");
                                    profilesummary = jsonObject.getString("profilesummary");
                                    String timestamp = userdate.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    userdate = datebuilding(date);

                                }
                                Glide.with(MyProfile.this).load(Urls.DOMAIN+"/assets/profilepictures/"+profilepic).
                                        circleCrop().
                                        into(useriamge);
                                uname.setText(user_name);
                                fname.setText(firstname);
                                lname.setText(lastname);
                                useremail.setText(email);
                                summary.setText(profilesummary);
                                datejoined.setText(userdate);


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
                buildDialog(MyProfile.this, "Oops..!", "Error occured").show();
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
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();

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
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(MyProfile.this,MyProfile.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        return builder;
    }

}

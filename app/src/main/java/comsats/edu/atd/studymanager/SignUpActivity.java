package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText firstname,lastname,phonenum,email,password,repassword,profilesummary,profilepicture;
    private String fname,lname,phonenumber,email1,pass,repass,profilesum,profilepic;
    private Button signup;
    private  ProgressDialog dialog;
    private String URLline = "";
    ArrayList<String> filePaths;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    int size = 0;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //finding ids (Edit Text)
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phonenum = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        profilesummary = findViewById(R.id.profilesummary);
        profilepicture = findViewById(R.id.profilepic);

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermisiion();
            }
        });


        //Getting Text
        signup = findViewById(R.id.btn_register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                   signup_user();

                }
        });
    }
    public void checkpermisiion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
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
                profilepicture.setText(filePath);

            }
        }

    }
     public void signup_user(){
        boolean isError = false;
        email1=email.getText().toString().trim();
        fname = firstname.getText().toString().trim();
        lname = lastname.getText().toString().trim();
        pass = password.getText().toString().trim();
        phonenumber = phonenum.getText().toString();
        repass = repassword.getText().toString().trim();
        profilesum = profilesummary.getText().toString().trim();
        profilepic = profilepicture.getText().toString().trim();
        profilepicture.setText(profilepic);
        if(email1.isEmpty()){
            email.setError("Field cannot be empty");
            isError = true;
        }


        else if(!(Patterns.EMAIL_ADDRESS.matcher(email1).matches())){
            email.setError("Invalid Email Address");
            isError = true;
        }else{
            email.setError(null);
            isError = false;
        }


        if(fname.isEmpty()){
            firstname.setError("Field cannot be empty");
            isError = true;
        }else if(fname ==null){
            firstname.setError("Field cannot be empty");
            isError = true;
        }else if(fname == ""){
            firstname.setError("Field cannot be empty");
            isError = true;
        }

        else{
            firstname.setError(null);
            isError = false;
        }

        if(lname.isEmpty()){
            lastname.setError("Field cannot be empty");
            isError = true;
        }
        else if(lname ==null){
            lastname.setError("Field cannot be empty");
            isError = true;
        }else if(lname == ""){
            lastname.setError("Field cannot be empty");
            isError = true;
        }
        else{
            lastname.setError(null);
            isError = false;
        }


        if(pass.isEmpty()){
            password.setError("Field cannot be empty");
            isError = true;
        }
        else if(pass ==null){
            password.setError("Field cannot be empty");
            isError = true;
        }else if(pass == ""){
            password.setError("Field cannot be empty");
            isError = true;
        }

        else if(pass.length()<8){
            password.setError("Minimum 8 characters required");
            isError = true;
        }else{
            password.setError(null);
            isError = false;
        }

        if(phonenumber.isEmpty()){
            phonenum.setError("Field cannot be empty");
            isError = true;

        }
        else if( phonenumber ==null){
            phonenum.setError("Field cannot be empty");
            isError = true;
        }else if( phonenumber == ""){
            phonenum.setError("Field cannot be empty");
            isError = true;
        }
        else if( phonenumber.length()<11){
            phonenum.setError("Minimum 11 characters required");
            isError = true;
        }else{
            phonenum.setError(null);
            isError = false;
        }

        if(repass.isEmpty()){
            repassword.setError("Field cannot be empty");
            isError = true;
        }
        else if(repass ==null){
            repassword.setError("Field cannot be empty");
            isError = true;
        }else if(repass == ""){
            repassword.setError("Field cannot be empty");
            isError = true;
        }
        else if(pass.length()<8){
            repassword.setError("Minimum 8 characters required");
            isError = true;
        }else if(!(pass.equals(repass))){
            repassword.setError("Password doesn't match");
            isError = true;
        }else{
            repassword.setError(null);
            isError = false;
        }  if(profilesum.isEmpty()){
             profilesummary.setError("Please give more summary");
             isError = true;
         }
         else if(profilesum.length()<50){
             profilesummary.setError("Please give more summary");
             isError = true;
         }
         if(profilepic.isEmpty()){
             profilepicture.setError("Field Cannot be Empty");
             isError = true;
         }else if(profilepic ==null){
             profilepicture.setError("Field Cannot be Empty");
             isError = true;
         }

        if (!isError) {
            dialog = ProgressDialog.show(SignUpActivity.this, "Please Wait",
                    "Saving Data...");
            dialog.show();
            uploadNow();
        }
    }
    private void uploadNow() {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Uploading file");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final File file = new File(filePath);
        Ion.with(SignUpActivity.this)
                .load("POST", Urls.SIGNUP_ACCOUNT)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        double progress = (100.0 * uploaded) / total;
                        progressDialog.setMessage("Creating account.. " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "application/pdf", file)
                    .setMultipartParameter("firstname", fname)
                    .setMultipartParameter("lastname", lname)
                    .setMultipartParameter("Phone", phonenumber)
                    .setMultipartParameter("email", email1)
                    .setMultipartParameter("password", pass)
                   .setMultipartParameter("profilesummary", profilesum)

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
                                    buildDialog(SignUpActivity.this, "500", "Internal Server Error","500").show();

                                }

                                else if (status.equals("200")) {
                                    buildDialog(SignUpActivity.this,"Congratulations","username/password are sent to your email","200").show();
                                    waitforsometime();
                                }
                                else if(status.equals("409")){

                                    buildDialog(SignUpActivity.this, "Account not created", "User with same name already exists","409").show();

                                }
                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                        } else {
                            buildDialog(SignUpActivity.this, "404", "Record Not Saved","404").show();
                        }
                    }
                });

    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
               startActivity(intent);
               finish();

            }
        }, 2000);
    }

    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        return builder;
    }

}

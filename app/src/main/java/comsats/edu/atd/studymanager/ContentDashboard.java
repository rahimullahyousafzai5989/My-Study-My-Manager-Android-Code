package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.security.AccessController.getContext;


public class ContentDashboard extends AppCompatActivity {
    private String filePath;
    private ProgressDialog dialog;
    Intent myintent;
    String identifier;
    String friend=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_dashboard);
         LinearLayout linearLayout = findViewById(R.id.parentlayout1);
        LinearLayout linearLayout2 = findViewById(R.id.parentlayout2);
        LinearLayout linearLayout3 = findViewById(R.id.parentlayout3);
        LinearLayout linearLayout4 = findViewById(R.id.parentlayout4);
        LinearLayout linearLayout5 = findViewById(R.id.parentlayout5);
        LinearLayout linearLayout6 = findViewById(R.id.parentlayout6);


        myintent = getIntent();
        if(myintent.getStringExtra("friend") != null){
            friend = myintent.getStringExtra("friend");
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier ="pdf";
                checkpermission();

            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier ="doc";
                checkpermission();
            }
        });
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier = "pics";
                checkpermission();

            }
        });
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier="video";
                checkpermission();
            }
        });
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier="audio";
                checkpermission();
            }
        });
        linearLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifier="zip";
                checkpermission();
            }
        });

    }
    public void checkpermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(ContentDashboard.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ContentDashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }
            else {
                if(identifier.equals("pdf")){
                    Intent intent = new Intent(ContentDashboard.this,ViewPdfList.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    }
                    startActivity(intent);
                }else if(identifier.equals("doc")){
                    Intent intent = new Intent(ContentDashboard.this,ViewDocList.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    } startActivity(intent);
                }
                else if(identifier.equals("pics")){
                    Intent intent = new Intent(ContentDashboard.this,SelectPhoto.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    } startActivity(intent);
                }
                else if(identifier.equals("video")){
                    Intent intent = new Intent(ContentDashboard.this,ViewVideos.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    } startActivity(intent);
                }
                else if(identifier.equals("audio")){
                    Intent intent = new Intent(ContentDashboard.this,ViewAudioList.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    }  startActivity(intent);
                }
                else if(identifier.equals("zip")){
                    Intent intent = new Intent(ContentDashboard.this,ViewZipList.class);
                    String collectionname = myintent.getStringExtra("collectionname");
                    intent.putExtra("collectionname",collectionname);
                    if(friend !=null && friend.equals("yes")){
                        intent.putExtra("friend",friend);
                    } startActivity(intent);
                }
            }

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){


        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

            }
        }
    }
}

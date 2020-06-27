package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class EnterFileContents extends AppCompatActivity {
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    EditText filetitle;
    TextView textView;
    Button pickfile,upload;
    private static String filePath=null;
    ArrayList<String> filePaths;
    private boolean isError=false;
    private String FILE,mfiletitle,mfiledescription;
    private String username;
    private static String identifier;
            public static String  mycollection;
    private String tumbnail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_file_contents);

        Intent intent = getIntent();
        identifier = intent.getStringExtra("identifier");
        mycollection = intent.getStringExtra("collectionname");
        filetitle = findViewById(R.id.filetitle);
        textView = findViewById(R.id.viewfilename);
        textView.setVisibility(View.GONE);
        upload = findViewById(R.id.btn_upload);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfiletitle = filetitle.getText().toString().trim();

                if(mfiletitle.isEmpty() || mfiletitle.equals("")){
                    filetitle.setError("Field Cannot be empty");
                    isError = true;
                }else{
                    isError=false;
                    if(!isError){
                        if(identifier.equals("pdf")){
                            UploadFile("pdf");
                        }else if(identifier.equals("doc")){
                            UploadFile("doc");
                        }
                        else if(identifier.equals("pics")){
                           Intent intent1 = new Intent(EnterFileContents.this,SelectPhoto.class);
                           startActivity(intent1);
                        }
                        else if(identifier.equals("video")){
                            UploadFile("video");
                        }
                        else if(identifier.equals("zip")){
                            UploadFile("zip");
                        }
                        else if(identifier.equals("audio")){
                            UploadFile("audio");
                        }


                }

                }
                }

        });
    }


    public void UploadFile(String datatype){
        Intent intent = new Intent(this, com.jaiselrahman.filepicker.activity.FilePickerActivity.class);

        String type="";
        if(datatype.equals("pdf")){
            type = "pdf";
            intent.putExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(false)
                    .setShowImages(false)
                    .setShowFiles(true)
                    .setSuffixes("pdf")
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());


        }else if(datatype.equals("doc")){

            intent.putExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(false)
                    .setShowImages(false)
                    .setShowFiles(true)
                    .setSuffixes("docx","doc","csv","html","ppt","pptx")

                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());
        }
        else if(datatype.equals("video")){
            intent.putExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(true)
                    .setShowImages(false)
                    .setShowFiles(false)

                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());
        }
        else if(datatype.equals("audio")){
            intent.putExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(false)
                    .setShowImages(false)
                    .setShowFiles(false)
                    .setShowAudios(true)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());
        }
        else if(datatype.equals("zip")){
            intent.putExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(false)
                    .setShowImages(false)
                    .setShowFiles(true)
                    .setSuffixes("zip","rar")
                    .setShowAudios(false)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());
        }
        else if(datatype.equals("audio")){
           intent = new Intent(this, com.jaiselrahman.filepicker.activity.FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowVideos(false)
                    .setShowImages(false)
                    .setShowAudios(true)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .setIgnoreNoMedia(true)
                    .build());

        }
        startActivityForResult(intent,1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode ==1
                && resultCode == RESULT_OK
                && data != null){
            mediaFiles.clear();
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(com.jaiselrahman.filepicker.activity.FilePickerActivity.MEDIA_FILES));
            for(int i = 0;i<mediaFiles.size();i++){
                MediaFile file = mediaFiles.get(i);
                filePath = file.getPath();

                textView.setVisibility(View.VISIBLE);
                textView.setText(filePath);
                if(filePath!=null && filePath.equals("")){
                    Toast.makeText(EnterFileContents.this,"Please Choose a File",Toast.LENGTH_LONG).show();
                }else {
                    if(identifier.equals("pdf")){
                        UploadNow("pdf");

                    }else if(identifier.equals("doc")){
                        UploadNow("doc");
                    }else if(identifier.equals("zip")){
                        UploadNow("zip");
                    }
                    else if(identifier.equals("video")){
                        tumbnail = String.valueOf(file.getThumbnail());
                        Log.d("2211",""+tumbnail);
                        UploadNow("video");
                    }else if(identifier.equals("audio")){
                        UploadNow("audio");
                    }

                }
            }
        }

    }

    public void UploadNow(String datatype)
    {

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        username =  sharedPreferences.getString("username",null);
       String url="";
        if(datatype.equals("pdf")){
            url = Urls.UPLOAD_PDF;
            UploadMyFile(url);
        }else if(datatype.equals("doc")){
            url = Urls.UPLOAD_DOC;
            UploadMyFile(url);
        }else if(datatype.equals("zip")){
            url = Urls.UPLOAD_ZIP;
            UploadMyFile(url);
        }else if(datatype.equals("video")){
            UploadVideo();
            return;
        }else if(datatype.equals("audio")){
            uploadaudio();

        }

    }
    public void UploadMyFile(String url){
        final File fileToUpload = new File(filePath);

        final ProgressDialog progressDialog = new ProgressDialog(EnterFileContents.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Ion.with(EnterFileContents.this)
                .load("POST", url)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        double progress = (100.0 * uploaded) / total;
                        progressDialog.setMessage("Uploading file " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "application/pdf", fileToUpload)
                .setMultipartParameter("fileTitle",mfiletitle)
                .setMultipartParameter("username",username)
                .setMultipartParameter("collectionname",mycollection)
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
                                    buildDialog(EnterFileContents.this, "500", "Internal Server Error","500").show();

                                }

                                else if (status.equals("200")) {
                                    buildDialog(EnterFileContents.this, "Congratulations", "File uploaded successfully.!","200").show();
                                    waitforsometime();

                                }
                                else if(status.equals("409")){
                                    buildDialog(EnterFileContents.this, "File cannnot be saved", "File with same name already exists","409").show();


                                }
                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                        } else {
                            buildDialog(EnterFileContents.this, "404", "Record Not Saved","404").show();
                        }
                    }
                });



    }

        private void UploadVideo() {
            final ProgressDialog progressDialog = new ProgressDialog(EnterFileContents.this);
            progressDialog.setMessage("Uploading file");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final File file = new File(filePath);
            Ion.with(EnterFileContents.this)
                    .load("POST", Urls.UPLOAD_VIDEO)
                    .setLogging("1312wWOD", Log.INFO)
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            double progress = (100.0 * uploaded) / total;
                            progressDialog.setMessage("Uploading file " + ((int) progress) + " %");
                        }
                    })
                    .setMultipartFile("file", "application/pdf", file)
                    .setMultipartParameter("fileTitle",mfiletitle)
                    .setMultipartParameter("username",username)
                    .setMultipartParameter("collectionname",mycollection)
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
                                        buildDialog(EnterFileContents.this, "500", "Internal Server Error","500").show();

                                    }

                                    else if (status.equals("200")) {
                                      buildDialog(EnterFileContents.this,"Congratulations","File is uploaded successfully","200").show();
                                        waitforsometime();
                                    }
                                    else if(status.equals("409")){

                                        buildDialog(EnterFileContents.this, "File cannnot be saved", "File with same name already exists","409").show();

                                    }
                                } catch (Exception ex) {
                                    Log.i("312333", ex + "");
                                }
                            } else {
                                buildDialog(EnterFileContents.this, "404", "Record Not Saved","404").show();
                            }
                        }
                    });

    }
    private void uploadaudio() {
        final ProgressDialog progressDialog = new ProgressDialog(EnterFileContents.this);
        progressDialog.setMessage("Uploading file");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final File file = new File(filePath);
        Ion.with(EnterFileContents.this)
                .load("POST", Urls.UPLOAD_AUDIO)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        double progress = (100.0 * uploaded) / total;
                        progressDialog.setMessage("Uploading file " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "application/pdf", file)
                .setMultipartParameter("fileTitle",mfiletitle)
                .setMultipartParameter("username",username)
                .setMultipartParameter("collectionname",mycollection)
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
                                    buildDialog(EnterFileContents.this, "500", "Internal Server Error","500").show();

                                }

                                else if (status.equals("200")) {
                                    buildDialog(EnterFileContents.this,"Congratulations","File is uploaded successfully","200").show();
                                    waitforsometime();
                                }
                                else if(status.equals("409")){

                                    buildDialog(EnterFileContents.this, "File cannnot be saved", "File with same name already exists","409").show();

                                }
                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                        } else {
                            buildDialog(EnterFileContents.this, "404", "Record Not Saved","404").show();
                        }
                    }
                });

    }

    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        if(status.equals("409") || status.equals("500")){
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        }
        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent=new Intent();
                if(identifier.equals("video")){
                    intent  = new Intent(EnterFileContents.this,ViewVideos.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                }else if(identifier.equals("pdf")){
                    intent  = new Intent(EnterFileContents.this,ViewPdfList.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                }else if(identifier.equals("doc")){
                    intent  = new Intent(EnterFileContents.this,ViewDocList.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                }else if(identifier.equals("audio")){
                    intent  = new Intent(EnterFileContents.this,ViewAudioList.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                }
                else if(identifier.equals("pics")){
                    intent  = new Intent(EnterFileContents.this,SelectPhoto.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                } else if(identifier.equals("zip")){
                    intent  = new Intent(EnterFileContents.this,ViewZipList.class);
                    intent.putExtra("collectionname",mycollection);
                    startActivity(intent);
                    finish();

                }

            }
        }, 1000);
    }
}

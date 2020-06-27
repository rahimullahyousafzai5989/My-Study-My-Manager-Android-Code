package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import java.io.File;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import android.os.Bundle;


public class DisplayPdf extends AppCompatActivity {
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pdf);
       Intent intent = getIntent();
       String filetype = intent.getStringExtra("filetype");
        String filename = intent.getStringExtra("filename");
        if(filetype.equals("pdf")){
            url =  Urls.DOMAIN+"/assets/PDfFiles/"+filename;
        }else{
           url = Urls.DOMAIN+"/assets/DocFiles/"+filename;
        }
       ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();
        url = url.replace(" ","%20");
       final Uri fileLink = Uri.parse(url);
       DocumentActivity.openDocument(this, fileLink, config);

       finish();
    }
   }

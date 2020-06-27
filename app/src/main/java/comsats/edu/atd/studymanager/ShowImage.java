package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class ShowImage extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageView = findViewById(R.id.myimageview);
        Intent intent = getIntent();
        String filename = intent.getStringExtra("filename");
        Log.d("FILENAME",""+filename);
        String url;
        url  = Urls.DOMAIN+"/assets/photos/"+filename;
        url = url.replace(" ","%20");
        Glide.with(ShowImage.this).load(url).into(imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(ShowImage.this));
    }
}

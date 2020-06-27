package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.khizar1556.mkvideoplayer.MKPlayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class ShowVideo extends AppCompatActivity {
    private MKPlayer player;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        Intent intent = getIntent();
        String identifier =intent.getStringExtra("identifier");
        final String filename = intent.getStringExtra("filename");

        if(identifier.equals("audio")){
            url  = Urls.DOMAIN+"/assets/audios/"+filename;
            url = url.replace(" ","%20");
        }else{
            url = Urls.DOMAIN+"/assets/Videos/"+filename;
            url = url.replace(" ","%20");

        }

        player=new MKPlayer(this);

        player.onComplete(new Runnable() {
            @Override
            public void run() {
                //callback when video is finish
                Toast.makeText(getApplicationContext(), "Media completed",Toast.LENGTH_SHORT).show();
            }
        }).onInfo(new MKPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //do something when buffering start
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //do something when buffering end
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //download speed
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //do something when video rendering
                        break;
                }
            }
        }).onError(new MKPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                Toast.makeText(getApplicationContext(), "video play error",Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayerCallbacks(new MKPlayer.playerCallbacks() {
            @Override
            public void onNextClick() {
                Toast.makeText(ShowVideo.this, "Select a media from collection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreviousClick() {
                Toast.makeText(ShowVideo.this, "Select a media from collection", Toast.LENGTH_SHORT).show();

            }
        });
        player.play(url);
        player.setTitle(filename);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    }


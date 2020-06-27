package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.multidex.MultiDex;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Button getstarted;
    private Boolean isFirstVisit;
    private WifiManager wifiManager;
    private String username=null;
    private JSONObject jsonObject;
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNELID="channel1";
    public static final String CHANNELNAME="My Study My Manager";
    private NotificationManager manager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
        notificationManager = NotificationManagerCompat.from(this);
        buildchannels();
       IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("username", "")!=null ||!(sharedPreferences.getString("username", "").isEmpty())){
            username = sharedPreferences.getString("username", "");
        }
        if(username != null){
            username = sharedPreferences.getString("username", "");

        }

        isFirstVisit =  sharedPreferences.getBoolean("isFirstVisit",true);

        if(isFirstVisit == false){

            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }


        getstarted = findViewById(R.id.btn_getstarted);
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                setvisit("buttonclick");
                finish();
            }
        });
        Boolean isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
        if(isuserloggedin){
            RecieverVolleyRequest();
            SenderVolleyRequest();
        }

    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data/Wifi to access.");

       waitforsometime();

        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                finish();
            }
        }, 1000);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setvisit(String check){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstVisit",false);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_collection:
                Toast.makeText(this, "Collection Item", Toast.LENGTH_SHORT).show();
            break;
        }


        return true;
    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private void RecieverVolleyRequest() {
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.RECIEVER_REQUEST;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                // buildDialog2(UserDetailActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("401")){
                                //Toast.makeText(UserDetailActivity.this,"No Record was found",Toast.LENGTH_LONG).show();



                            } else if (status.equals("200")) {
                                JSONArray request = reader.getJSONArray("response");
                                for (int i = 0; i < request.length(); i++) {
                                   jsonObject = request.getJSONObject(i);
                                   String sendername = jsonObject.getString("sender");
                                   Intent resultIntent = new Intent(MainActivity.this,RequestActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                    Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNELID)
                                            .setSmallIcon(R.drawable.ic_group_black_24dp)
                                            .setContentTitle("Request Recieved")
                                            .setContentText(sendername+" has requested to follow you")
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                            .setAutoCancel(true)
                                            .setContentIntent(pendingIntent)
                                            .build()
                                            ;
                                    notificationManager.notify(i, notification);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                //buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",username);
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
    private void SenderVolleyRequest() {
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.FRIEND_REQUEST;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                // buildDialog2(UserDetailActivity.this, "Error", "Internal Server Error").show();
                            }else if(status.equals("401")){
                                //Toast.makeText(UserDetailActivity.this,"No Record was found",Toast.LENGTH_LONG).show();



                            } else if (status.equals("200")) {
                                JSONArray request = reader.getJSONArray("response");
                                for (int i = 0; i < request.length(); i++) {
                                    jsonObject = request.getJSONObject(i);
                                    String sendername = jsonObject.getString("sender");
                                    if(sendername.equals(username)){

                                    }else{
                                        Intent resultIntent = new Intent(MainActivity.this,FriendActivity.class);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNELID)
                                                .setSmallIcon(R.drawable.ic_group_black_24dp)
                                                .setContentTitle("Request Accepted")
                                                .setContentText(sendername+" has accepted your request")
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                .setAutoCancel(true)
                                                .setContentIntent(pendingIntent)
                                                .build()
                                                ;
                                        notificationManager.notify(i, notification);
                                    }

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                //buildDialog2(UserDetailActivity.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender",username);
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
    private void buildchannels() {
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           NotificationChannel channel = new NotificationChannel(CHANNELID,CHANNELNAME, NotificationManager.IMPORTANCE_HIGH);
           channel.enableLights(true);
           channel.enableVibration(true);
           channel.getAudioAttributes();
           channel.setLightColor(R.color.colorPrimary);

           channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
           getManager().createNotificationChannel(channel);

       }


    }
    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }
}

package comsats.edu.atd.studymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, android.app.TimePickerDialog.OnTimeSetListener {
    private Button btn;
    private TextInputEditText title,date;
    int orghour,orgdate,orgmint,orgmonth;
    String mydate;
    Spinner spinner;
    EditText subject;
    String subjecttitle,reminder_title;
    boolean is_Error;
    String time,time1;
    String cat;
    private FrameLayout container;
     String collectionname;
    private  ProgressDialog dialog;
    private String username;
    String medium="pm";
    int reqcode ;
    String status;
    String status1, minuteholder,hourHolder;
    int realmonth,realdate,year,realhour;
    ArrayList<NotificationDetails> details;
    private NotificationHelperClass notificationHelperClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random r = new Random();
        reqcode=r.nextInt(45 - 28) + 28;
        setContentView(R.layout.activity_add_reminder);
        if(NotificationDataModel.getDetails()!=null){
            details = NotificationDataModel.getDetails();
        }else{
            details = new ArrayList<>();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        container = findViewById(R.id.container);
        container.setVisibility(View.GONE);
        spinner = findViewById(R.id.spinner1);
        subject = findViewById(R.id.subject);
        btn = findViewById(R.id.btn_setreminder);
        title = findViewById(R.id.title);
        date = findViewById(R.id.deadline);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat = spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        notificationHelperClass = new NotificationHelperClass(this);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Dialog();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //setupchannel("MyStudyMyManager","You Have an assigment comming up..!!!");
               checkfilecontet();
            }
        });

    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        realmonth = month+1;
        orgmonth = month;
        orgdate = dayOfMonth;

        String monthcontainer;
         realdate = dayOfMonth;
        String realdatecontainer;
        if(realmonth>9){
            monthcontainer = ""+realmonth;
        }else{
            monthcontainer = "0"+realmonth;

        }
        if(realdate>9){
            realdatecontainer=""+dayOfMonth;


        }else{
            realdatecontainer = "0"+dayOfMonth;
        }
        mydate=year+"-"+monthcontainer+"-"+realdatecontainer;

        if(mydate!=null){
            showTimePickerDialog();
        }




    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        realhour= hourOfDay;
        orghour = hourOfDay;
        orgmint = minute;
        hourHolder=""+hourOfDay;
        minuteholder=""+minute;

        if(hourOfDay>=0 && hourOfDay<=11){
            medium = "am";

        }

        else if(hourOfDay>12){
            realhour=hourOfDay-12;
            if(realhour>=10){
                hourHolder = ""+realhour;

            }else{
                hourHolder = "0"+realhour;

            }

        }else if(realhour == 0){
            realhour = 12;
            hourHolder = ""+realhour;

        }
        if( minute<10){
            minuteholder = "0"+minute;

        }
        time1 = String.valueOf(realhour)+":"+minuteholder;
        time=hourHolder+":"+minuteholder;
        dialog = ProgressDialog.show(AddReminder.this, "Please Wait",
                "");
        dialog.show();
        VolleyDownloadRequest();


    }

    private void checkfilecontet() {
        subjecttitle = subject.getText().toString().trim();
        reminder_title = title.getText().toString().trim();

       if(reminder_title==null ||reminder_title.isEmpty()){
            is_Error=true;
            title.setError("Field Cannot Be Empty");
            title.requestFocus();
        } else if(subjecttitle==null || subjecttitle.isEmpty()){
           is_Error = true;
           subject.setError("Field Cannot Be Empty");
           subject.requestFocus();
       }
       else if(time==null){
            is_Error=true;
            Toast.makeText(notificationHelperClass, "Please Select Time", Toast.LENGTH_SHORT).show();
        }else{
            is_Error = false;
        }
        if(!is_Error){
            dialog = ProgressDialog.show(AddReminder.this, "Please Wait",
                    "Setting Reminder...");
            dialog.show();
           VolleyRequest();
        }
    }

    public void VolleyRequest(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(AddReminder.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.UPLOAD_REMINDER;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();

                        try {
                            JSONObject reader;
                            reader = new JSONObject(response);

                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog(AddReminder.this, "500", "Internal Server Error").show();
                            }else if(status.equals("200")){
                                buildDialog(AddReminder.this, "", "Reminder Set Sucessfully").show();
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, orgmonth);
                                c.set(Calendar.DAY_OF_MONTH, orgdate);
                                String subjecttitle = reader.getString("subjecttitle");
                                String category = reader.getString("category");

                                c.set(Calendar.HOUR_OF_DAY, orghour);
                                c.set(Calendar.MINUTE, orgmint);
                                c.set(Calendar.SECOND, 0);

                                 AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(AddReminder.this,AlertReciever.class);
                                intent.putExtra("subjecttitle",subjecttitle);
                                intent.putExtra("category",category);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddReminder.this, reqcode, intent, 0);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                                waitforsometime();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                error.printStackTrace();
                buildDialog(AddReminder.this,"Oops..!","Data not saved").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",username);
                params.put("remindertitle", reminder_title);
                params.put("subjecttitle", subjecttitle);
                params.put("category", cat);
                params.put("date", mydate);
                params.put("time",time);
                params.put("medium",medium);
                params.put("code",String.valueOf(reqcode));

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


        return builder;
    }

    private void Show_Dialog(){
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)


        );
        dialog.show();
    }


    private void VolleyDownloadRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(AddReminder.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_TIME;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();

                        try {
                            JSONObject reader;
                            reader = new JSONObject(response);

                           status1 = reader.getString("status");
                            if (status1.equals("400")) {
                                buildDialog(AddReminder.this, "Alert", "please choose a different date").show();
                            }else if(status1.equals("401")){
                                buildDialog(AddReminder.this, "Alert", "please choose a different time").show();

                            }else{
                               date.setText(mydate+"  "+time+"-"+medium);
                               }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                error.printStackTrace();
                buildDialog(AddReminder.this,"Oops..!","Data not saved").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date", mydate);
                params.put("time",time1);
                params.put("medium",medium);
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

    private void showTimePickerDialog() {
        DialogFragment dialog = new TimePickerDialog();
            dialog.show(getSupportFragmentManager(),"file picker");


    }


    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                container.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new ReminderFragment ()).commit();

            }
        }, 1000);
    }
}

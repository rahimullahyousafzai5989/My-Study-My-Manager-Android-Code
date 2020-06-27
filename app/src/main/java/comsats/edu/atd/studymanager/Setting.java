package comsats.edu.atd.studymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    LinearLayout firstname1,lastname1,email1,profilesummary1,password1,username1;
    String password,user_name,firstname,lastname,email,profilesummary;
    TextView uname,fname,lname,useremail,summary,pass;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firstname1 = findViewById(R.id.firstname);
        lastname1 = findViewById(R.id.lastname);
        email1 = findViewById(R.id.email);
        profilesummary1 = findViewById(R.id.summary);
        password1 = findViewById(R.id.password);
        username1 = findViewById(R.id.username);
        firstname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangeFirstName.class);
                intent.putExtra("firstname",firstname);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });

        lastname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangeLastName.class);
                intent.putExtra("lastname",lastname);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });

        email1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangeEmailAddress.class);
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });

        username1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangeUsername.class);
                intent.putExtra("username",user_name);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });

        password1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangePassword.class);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });
        profilesummary1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Setting.this,ChangeProfileSummary.class);
                intent.putExtra("summary",profilesummary);
                intent.putExtra("password",password);
                startActivity(intent);
            }
        });

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        uname = findViewById(R.id.uname);
        useremail = findViewById(R.id.uemail);
        summary = findViewById(R.id.summ);
        pass = findViewById(R.id.pass);



        Intent intent = getIntent();
        if(intent !=null){
            user_name = intent.getStringExtra("username");
            firstname = intent.getStringExtra("firstname");
            lastname= intent.getStringExtra("lastname");
            email= intent.getStringExtra("email");
            profilesummary= intent.getStringExtra("profilesummary");
            password = intent.getStringExtra("password");
        }

        fname.setText(firstname);
        lname.setText(lastname);
        uname.setText(user_name);
        useremail.setText(email);
        summary.setText(profilesummary);
        pass.setText("********");



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                super.onBackPressed();
                break;
        }


        return true;
    }
}

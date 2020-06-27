package comsats.edu.atd.studymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReminderFragment extends Fragment {
    private View view;
    FloatingActionButton fb;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    JSONObject jsonObject;

    String subjecttitle,reminder_title;
    String cat;
    private String username;
    String medium;
    String time1,date1;
    String file_date;
    private static String date2,dayvalue,time,code;
    private ArrayList<ReminderContentModel> reminderContentModels,reminderContentModelArrayList;
    private TextView toolbartext;
    Toolbar toolbar;
    public boolean isaction_mode = false;
    private ReminderAdaphter adaphter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_reminder,container,false);
        Intent intent = new Intent(getActivity(),ReminderActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
        return view;
    }

}

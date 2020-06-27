package comsats.edu.atd.studymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {
    private TextView textView;
    private LinearLayout collectionbtn;
    private JSONObject jsonObject;
    private String username;
    private TextView collection, file, quiz, assignment;
    private String collec_length, file_length, quiz_length, assignment_length;
    private GifImageView loader;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        collectionbtn = view.findViewById(R.id.collectionbtn);
        loader = view.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        collection = view.findViewById(R.id.collec_length);
        file = view.findViewById(R.id.file_length);
        quiz =view.findViewById(R.id.quiz_length);
        assignment = view.findViewById(R.id.assignment_length);
        VolleyRequest();
        collectionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        return view;
    }

    public void VolleyRequest() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        loader.setVisibility(View.VISIBLE);
        String url = Urls.GET_COUNT;
        Log.d("112233", "Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233", "Url Setting");
                        try {

                            JSONObject reader = new JSONObject(response);


                            collec_length = reader.getString("collectionlength");
                            quiz_length = reader.getString("quizlength");
                            assignment_length = reader.getString("assignmentlength");
                            file_length = reader.getString("filelength");
                            collection.setText(collec_length);
                            file.setText(file_length);
                            quiz.setText(quiz_length);
                            assignment.setText(assignment_length);
                            loader.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                Snackbar.make(view,"Cannot refresh dashboard..", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
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

    @Override
    public void onResume() {
        super.onResume();
        VolleyRequest();
    }

}

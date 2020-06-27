package comsats.edu.atd.studymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FriendAdaphter extends RecyclerView.Adapter<FriendAdaphter.viewHolder> implements Filterable {
    String recievername, sendername;
    ArrayList<UserInfoDataModel> arrayList;
    ArrayList<UserInfoDataModel> arrayListFull;
    Context context;
    String user_name,user_name1;
    private String search_query;
    private FriendActivity friendActivity;
    private static String date2,dayvalue,time;
    private ProgressDialog dialog;
    private JSONObject jsonObject;
    String userdate,firstname,lastname,email,profilepic,profilesummary;

    public FriendAdaphter(ArrayList<UserInfoDataModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user_name1 = sharedPreferences.getString("username", "");
        this.friendActivity = (FriendActivity) context;
        this.arrayListFull = new ArrayList<>(arrayList);
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_layout,parent,false);
        return new FriendAdaphter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        final String username = arrayList.get(position).getUsername();
        final String email = arrayList.get(position).getEmail();
        final String date = arrayList.get(position).getDate();
        final String firstname = arrayList.get(position).getFirstname();
        final String lastname = arrayList.get(position).getLastname();
        final String profilepic = arrayList.get(position).getProfilepic();
        final String profilesummary = arrayList.get(position).getProfilesummary();
        holder.username.setText(username);
        holder.email.setText(email);
        holder.date.setText(date);
        Glide.with(context).load(Urls.DOMAIN+"/assets/profilepictures/"+profilepic).

                into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_query = arrayList.get(position).getUsername();
                VolleyRequest1();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView username,email,date;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.userimage);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.useremail);
            date = itemView.findViewById(R.id.userdate);
        }

        @Override
        public void onClick(View v) {
            friendActivity.prepareselection(v,getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return myfilter;
    }

    private Filter myfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserInfoDataModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(UserInfoDataModel item:arrayListFull){
                    if(item.getUsername().toLowerCase().contains(FilterPattern)){
                        filterlist.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
    public void VolleyRequest1() {
        dialog = ProgressDialog.show(context, "Please Wait",
                "Loading Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(context);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.SEARCH_USERS;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        Log.d("112233","Url Setting");
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog(context, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){


                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray collections = reader.getJSONArray("response");

                                Log.i("Collection Activity", String.valueOf(reader));
                                for (int i = 0; i < collections.length(); i++) {
                                    jsonObject = collections.getJSONObject(i);
                                    user_name = jsonObject.getString("username");
                                    userdate = jsonObject.getString("_id");
                                    firstname = jsonObject.getString("firstname");
                                    lastname = jsonObject.getString("lastname");
                                    email = jsonObject.getString("email");
                                    profilepic = jsonObject.getString("profilepic");
                                    profilesummary =  jsonObject.getString("profilesummary");
                                    String timestamp = userdate.substring(0,8);
                                    BigInteger bi = new BigInteger(timestamp,16);
                                    long date = Long.parseLong(String.valueOf(bi));
                                    userdate = datebuilding(date);
                                    Intent intent = new Intent(context,UserDetailActivity.class);
                                    intent.putExtra("profilepic",profilepic);
                                    intent.putExtra("username",user_name);
                                    intent.putExtra("firstname",firstname);
                                    intent.putExtra("lastname",lastname);
                                    intent.putExtra("email",email);
                                    intent.putExtra("date",userdate);
                                    intent.putExtra("profilesummary",profilesummary);

                                    context.startActivity(intent);
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                buildDialog(context, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", search_query);
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
    public static String datebuilding(long date) {
        //Log.d("JSON","Inside Date Building");
        Date date1 = new Date(date * 1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        date2 = sdf.format(date1);
        SimpleDateFormat sdf1 = new SimpleDateFormat("E");
        dayvalue = sdf1.format(date1);
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
        time = sdf2.format(date1);
        String Complete_Date = dayvalue+"/"+date2;
        return Complete_Date;
    }

    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
    public void updateAdaphter(ArrayList<RequestDataModel> arrayList){
        for (RequestDataModel item:arrayList
        ) {
            this.arrayList.remove(item);
        }
        notifyDataSetChanged();
    }
}

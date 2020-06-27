package comsats.edu.atd.studymanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderAdaphter extends RecyclerView.Adapter<ReminderAdaphter.viewHolder> implements Filterable {
    ArrayList<ReminderContentModel> arrayList;
    ArrayList<ReminderContentModel> arrayListFull;
    Context context;
    private ProgressDialog dialog;
    String file_date;
    String code;
    ReminderActivity reminderActivity;
    public ReminderAdaphter(Context context,ArrayList<ReminderContentModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.reminderActivity = (ReminderActivity)context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.reminder_layout,parent,false);
        return new ReminderAdaphter.viewHolder(view,reminderActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        final String remindername = arrayList.get(position).getRemindername();
        final String subjectname = arrayList.get(position).getSubjectname();
        final String cat  = arrayList.get(position).getCat();
        final String date  = arrayList.get(position).getDate();
        final String time  = arrayList.get(position).getTime();
        final String file_date  = arrayList.get(position).getFile_date();
        final String medium = arrayList.get(position).getMedium();
         final String id  = arrayList.get(position).getId();
         code = arrayList.get(position).getCode();
        final String dealine = date+" "+time+" "+medium;

        holder.remindername.setText(subjectname+" "+remindername);
        //holder.file_date.setText(file_date);
        holder.deadline.setText(dealine);
        holder.itemView.setOnLongClickListener(reminderActivity);
        if(!reminderActivity.isaction_mode){
            holder.checkBox.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);

        }
    }
    @Override
    public Filter getFilter() {
        return myfilter;
    }

    private Filter myfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ReminderContentModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(ReminderContentModel item:arrayListFull){
                    if(item.getRemindername().toLowerCase().contains(FilterPattern)){
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


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView remindername,subjectname, cat,deadline, file_date;
        CheckBox checkBox;

        public viewHolder(@NonNull View itemView,ReminderActivity reminderActivity) {
            super(itemView);
            context  = itemView.getContext();
            remindername = itemView.findViewById(R.id.remindername);
            deadline = itemView.findViewById(R.id.deadline);
            checkBox = itemView.findViewById(R.id.deleteitems);
            checkBox.setOnClickListener(this);
            file_date = itemView.findViewById(R.id.filedate);
           // delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void onClick(View v) {
            reminderActivity.prepareselection(v,getAdapterPosition());        }
    }

    public void VolleyRequest2(final String id) {
        dialog = ProgressDialog.show(context, "Please Wait",
                "Deleting Data...");
        dialog.show();

        final RequestQueue queue = Volley.newRequestQueue(context);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.DELETE_REMINDER;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog(context, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){

                            } else if (status.equals("200")) {
                                buildDialog(context, "200", "Reminder deleted").show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Log.d("Reminder Adaphater", error.toString());
                error.printStackTrace();
                buildDialog(context, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
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

    public AlertDialog.Builder buildDialog2(Context c, String header, String message, final String id,
                                            final int position,final String code) {

        final int code1 = Integer.parseInt(code);
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              try{
                  VolleyRequest2(id);
                  arrayList.remove(position);
                  notifyItemRemoved(position);
                  AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                  Intent intent = new Intent(context, AlertReciever.class);
                  PendingIntent pendingIntent = PendingIntent.getBroadcast(context, code1, intent, 0);

                  alarmManager.cancel(pendingIntent);
              }catch(IndexOutOfBoundsException e){
                  arrayList.clear();

                  notifyItemRemoved(position);
              }
            }
        });


        return builder;
    }

    public void updateAdaphter(ArrayList<ReminderContentModel> arrayList){
        for (ReminderContentModel item:arrayList
        ) {
            this.arrayList.remove(item);
        }
        notifyDataSetChanged();
    }
}

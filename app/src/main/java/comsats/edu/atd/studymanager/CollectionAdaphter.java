package comsats.edu.atd.studymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorLong;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollectionAdaphter extends RecyclerView.Adapter<CollectionAdaphter.viewHolder> implements Filterable {
    ArrayList<CollectionDataModel> arrayList;
    Context context;
    ArrayList<CollectionDataModel> arrayListFull;
    CollectionActivity collectionActivity;
    private String friend;
    private String m_Text;
    private String username;
    private ProgressDialog dialog;
     String collectionname;
     String collectiondate;
    String cname;
    public CollectionAdaphter(Context context,ArrayList<CollectionDataModel> arrayList,String friend) {
        this.arrayList = arrayList;
        this.arrayListFull= new ArrayList<>(arrayList);
        this.context = context;
        this.collectionActivity = (CollectionActivity)context;
        this.friend = friend;
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.collection_item_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        collectionname = arrayList.get(position).getCollectionname();
        collectiondate = arrayList.get(position).getCollectiondate();

        holder.collectionname.setText(collectionname);
        holder.collectiondate.setText(collectiondate);
        if(!collectionActivity.isaction_mode){
            holder.parentlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context,"Item Clicked",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context,ContentDashboard.class);
                    intent.putExtra("collectionname",collectionname);
                    intent.putExtra("friend",friend);
                    context.startActivity(intent);
                }
            });
        }   else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


        if(!collectionActivity.isedit_mode){
            holder.parentlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context,"Item Clicked",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context,ContentDashboard.class);
                    intent.putExtra("collectionname", arrayList.get(position).getCollectionname());
                    intent.putExtra("friend",friend);
                    context.startActivity(intent);
                }
            });
        }   else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cname = arrayList.get(position).getCollectionname();
                    buildDialog(cname).show();
                }
            });
        }
        holder.itemView.setOnLongClickListener(collectionActivity);
        if(!collectionActivity.isaction_mode){
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
            ArrayList<CollectionDataModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(CollectionDataModel item:arrayListFull){
                    if(item.getCollectionname().toLowerCase().contains(FilterPattern)){
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
        TextView collectionname,collectiondate;
        LinearLayout parentlayout;
        CheckBox checkBox;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            context  = itemView.getContext();
            collectiondate = itemView.findViewById(R.id.collectiondate);
            collectionname = itemView.findViewById(R.id.collectionname);
            parentlayout = itemView.findViewById(R.id.parentlayout);
            checkBox = itemView.findViewById(R.id.deleteitems);
            checkBox.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            collectionActivity.prepareselection(v,getAdapterPosition());
        }
    }
    public void updateAdaphter(ArrayList<CollectionDataModel> arrayList){
        for (CollectionDataModel item:arrayList
        ) {
            this.arrayList.remove(item);
        }
        notifyDataSetChanged();
    }
    public AlertDialog.Builder buildDialog(final String cname) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Collection Name");
        builder.setMessage("(e.g Data warehousing)");
// Set up the input
        final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(input);
        input.setText(cname);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString().trim();
                if(m_Text.equals(null) || m_Text.isEmpty() || m_Text == ""){
                    Toast.makeText(context,"Please Enter a Collection name",Toast.LENGTH_LONG).show();
                }else if(m_Text.equals(cname)){
                    Toast.makeText(context,"Please choose a different name",Toast.LENGTH_LONG).show();

                }else{
                    VolleyRequest1(m_Text, username);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;

    }
    public void VolleyRequest1(final String m_Text, final String username) {
        // Instantiate the RequestQueue.
        dialog = ProgressDialog.show(context, "Please Wait",
                "Saving Data...");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.UPDATE_COLLECTION;

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
                            }

                            else if (status.equals("200")) {
                                buildDialogdatachanged(context, "Congratulation", "Collection is renamed").show();

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
                params.put("username", username);
                params.put("collecname", m_Text);
                params.put("oldname",cname);

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

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context,DashboardActivity.class);
                context.startActivity(intent);


            }
        });

        return builder;
    }


    public AlertDialog.Builder buildDialogdatachanged(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context,CollectionActivity.class);
                context.startActivity(intent);
                collectionActivity.finish();

            }
        });

        return builder;
    }

}

package comsats.edu.atd.studymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class DocFileAdaphter extends RecyclerView.Adapter<DocFileAdaphter.viewHolder> implements Filterable {

    private ArrayList<DocFileDataModel> arrayList;
    ArrayList<DocFileDataModel> arrayListFull;
    Context context;
    private ProgressDialog dialog;
    private String cname,m_Text,username;
    private ViewDocList viewDocList;
    public DocFileAdaphter(Context context,ArrayList<DocFileDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.viewDocList = (ViewDocList) context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.doclist_layout,parent,false);
        return new DocFileAdaphter.viewHolder(view,viewDocList);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        final String filename = arrayList.get(position).getFilename();
        final String file_date = arrayList.get(position).getFile_date();
        final String file_title  = arrayList.get(position).getFiletitle();
        holder.checkBox.setVisibility(View.INVISIBLE);

        holder.filename.setText(filename);
        holder.filedate.setText(file_date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewDocList.isaction_mode&& !viewDocList.isedit_mode){
                    //Toast.makeText(context,""+file_title,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, DisplayPdf.class);
                    intent.putExtra("filetype", "doc");
                    intent.putExtra("filename", file_title);
                    context.startActivity(intent);
                } else if(!viewDocList.isaction_mode && viewDocList.isedit_mode){

                    cname = arrayList.get(position).getFilename();
                    buildDialog(cname).show();
                }
                else{
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
                }
        });




        holder.itemView.setOnLongClickListener(viewDocList);
        if(!viewDocList.isaction_mode){
            holder.checkBox.setVisibility(View.GONE);
        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return myfilter;
    }
    private Filter myfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DocFileDataModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(DocFileDataModel item:arrayListFull){
                    if(item.getFilename().toLowerCase().contains(FilterPattern)){
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

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView filename,filedate;
        LinearLayout parentlayout;
        CheckBox checkBox;
        private ViewDocList viewDocList1;

        public viewHolder(@NonNull View itemView,ViewDocList viewDocList) {
            super(itemView);
            filedate = itemView.findViewById(R.id.filedate);
            filename = itemView.findViewById(R.id.filename);
            parentlayout = itemView.findViewById(R.id.parentlayout);
            checkBox = itemView.findViewById(R.id.deleteitems);

            checkBox.setOnClickListener(this);
            this.viewDocList1= viewDocList;

        }
        @Override
        public void onClick(View v) {
            viewDocList.prepareselection(v,getAdapterPosition());
        }
    }
    public void updateAdaphter(ArrayList<DocFileDataModel> arrayList){
        for (DocFileDataModel item:arrayList
        ) {
            this.arrayList.remove(item);
        }
        notifyDataSetChanged();
    }
    public AlertDialog.Builder buildDialog(final String cname) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter File Name");
        builder.setMessage("(e.g Lec 1)");
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
        String url = Urls.UPDATE_DOCUMENT;

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
                                buildDialogdatachanged(context, "Congratulation", "File is renamed").show();

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
                params.put("filename", m_Text);
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
                viewDocList.finish();

            }
        });

        return builder;
    }


}

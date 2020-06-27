package comsats.edu.atd.studymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class UserAdapahter extends RecyclerView.Adapter<UserAdapahter.viewHolder> {

    ArrayList<UserInfoDataModel> arrayList;
    Context context;

    public UserAdapahter(ArrayList<UserInfoDataModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_layout,parent,false);
        return new UserAdapahter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

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
                Intent intent = new Intent(context,UserDetailActivity.class);
                intent.putExtra("profilepic",profilepic);
                intent.putExtra("username",username);
                intent.putExtra("firstname",firstname);
                intent.putExtra("lastname",lastname);
                intent.putExtra("email",email);
                intent.putExtra("date",date);
                intent.putExtra("profilesummary",profilesummary);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
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

    }
}

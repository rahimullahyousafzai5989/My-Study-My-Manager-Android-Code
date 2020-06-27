package comsats.edu.atd.studymanager;

import android.content.Context;
import android.content.Intent;
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

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoAdaphter extends RecyclerView.Adapter<PhotoAdaphter.viewHolder> implements Filterable {
        private ArrayList<PhotoContentModel> arrayList;
        Context context;
        private ArrayList<PhotoContentModel> arrayListFull;

    private SelectPhoto selectPhoto;

    public PhotoAdaphter(Context context, ArrayList<PhotoContentModel> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
        this.selectPhoto = (SelectPhoto) context;
        this.arrayListFull = new ArrayList<>(arrayList);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.photogridlayout,parent,false);
        return new PhotoAdaphter.viewHolder(view,selectPhoto);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final String filename = arrayList.get(position).getImage_name();

        final String url = Urls.DOMAIN+"/assets/photos/"+filename;
                url.replace(" ","%20");
        Glide.with(context).load(url).

                 into(holder.imageView);

        if(!selectPhoto.isaction_mode) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ShowImage.class);
                    intent.putExtra("filename",filename);
                    context.startActivity(intent);
                }
            });
        }else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        holder.itemView.setOnLongClickListener(selectPhoto);
        if(!selectPhoto.isaction_mode){
            holder.checkBox.setVisibility(View.GONE);
            holder.wrapper.setVisibility(View.GONE);

        }else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.wrapper.setVisibility(View.VISIBLE);
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
            ArrayList<PhotoContentModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(PhotoContentModel item:arrayListFull){
                    if(item.getImage_name().toLowerCase().contains(FilterPattern)){
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
        ImageView imageView;
        LinearLayout wrapper;
        CheckBox checkBox;
        SelectPhoto selectPhoto1;
        public viewHolder(@NonNull View itemView,SelectPhoto selectPhoto) {
            super(itemView);
            context  = itemView.getContext();
             imageView = itemView.findViewById(R.id.myimage);
            wrapper = itemView.findViewById(R.id.wrapper);
            checkBox = itemView.findViewById(R.id.deleteitems);
            checkBox.setOnClickListener(this);
            this.selectPhoto1= selectPhoto;
        }
        @Override
        public void onClick(View v) {
            selectPhoto.prepareselection(v,getAdapterPosition());
        }
    }
    public void updateAdaphter(ArrayList<PhotoContentModel> arrayList){
        for (PhotoContentModel item:arrayList
        ) {
            this.arrayList.remove(item);
        }
        notifyDataSetChanged();
    }
}

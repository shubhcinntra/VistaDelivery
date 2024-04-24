package com.cinntra.vistadelivery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cinntra.vistadelivery.R;
import com.cinntra.vistadelivery.globals.Globals;

import java.util.ArrayList;
import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter <ImageViewAdapter.ContactViewHolder>{

    Context context;
    ArrayList<Uri> UriList ;
    String flag = "";
    public ImageViewAdapter(Context context, List<Uri> UriList, String autoFetch) {
        this.context = context;
        this.UriList= (ArrayList<Uri>) UriList;
        this.flag = autoFetch;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.imageadapterscree,parent,false);
        return new ContactViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.icon)
                .error(R.drawable.ic_invoice);

        String ImageUrl = Globals.ImageURl + UriList.get(position).toString();

        Glide.with(context).load(UriList.get(position).toString()).apply(options).into(holder.loadimage);
       /* if (flag == "AutoFetch"){
            Glide.with(context).load(ImageUrl).apply(options).into(holder.loadimage);
        }else {

        }*/


        holder.cross.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                UriList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return UriList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {


        ImageView loadimage,cross;
        public ContactViewHolder(@NonNull View itemView) {

            super(itemView);
            loadimage = itemView.findViewById(R.id.loadimage);
            cross= itemView.findViewById(R.id.cross);


        }
    }
}

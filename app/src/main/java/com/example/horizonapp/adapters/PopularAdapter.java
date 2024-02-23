package com.example.horizonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.horizonapp.R;
import com.example.horizonapp.domain.PopularDomain;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    private ArrayList<PopularDomain.Domain> items;
    private Context context;

    public PopularAdapter(ArrayList<PopularDomain.Domain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pup_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularDomain.Domain currentItem = items.get(position);

        // Set data to views
        holder.titleTxt.setText(currentItem.getDomainTitle());
        holder.locTxt.setText(currentItem.getDomainLocation());

        int drawableResourceId = context.getResources().getIdentifier(currentItem.getDomainPicUrl(), "drawable", context.getPackageName());

        Glide.with(context)
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt;
        TextView locTxt;
        ImageView favoriteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            locTxt = itemView.findViewById(R.id.locTxt);
            favoriteIcon = itemView.findViewById(R.id.imageView5);

            favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteIcon.setSelected(!favoriteIcon.isSelected());
                }
            });
        }
    }
}
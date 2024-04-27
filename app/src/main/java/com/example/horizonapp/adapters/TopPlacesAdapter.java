package com.example.horizonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.horizonapp.R;
import com.example.horizonapp.domain.TopPlaceDomain;

import java.util.ArrayList;
import java.util.List;

public class TopPlacesAdapter extends RecyclerView.Adapter<TopPlacesAdapter.ViewHolder> {

    private ArrayList<TopPlaceDomain> items;
    private Context context;
    private OnItemClickListener mListener;

    public TopPlacesAdapter(FragmentActivity activity, List<TopPlaceDomain> topPlacesList) {
    }

    public interface OnItemClickListener {
        void onItemClick(TopPlaceDomain item);
    }

    public TopPlacesAdapter(ArrayList<TopPlaceDomain> items, OnItemClickListener listener) {
        this.items = items;
        this.mListener = listener;
    }

    public TopPlacesAdapter(Context context, List<TopPlaceDomain> topPlacesList) {
        this.context = context;
        this.items = new ArrayList<>(topPlacesList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_top_places_viewholder, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopPlaceDomain currentItem = items.get(position);

        // Set data to views
        holder.titleTxt.setText(currentItem.getTitle());
        holder.locTxt.setText(currentItem.getLocation());

        int drawableResourceId = context.getResources().getIdentifier(currentItem.getPicUrl(), "drawable", context.getPackageName());

        Glide.with(context)
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.pic);

        // Set OnClickListener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(currentItem);
            }
        });
    }

    public TopPlacesAdapter(Context context, List<TopPlaceDomain> topPlacesList, OnItemClickListener listener) {
        this.context = context;
        this.items = new ArrayList<>(topPlacesList);
        this.mListener = listener;
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

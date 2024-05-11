package com.example.horizonapp.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.horizonapp.R;
import com.example.horizonapp.databinding.ActivityDetailBinding;
import com.example.horizonapp.domain.TopPlaceDomain;

public final class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private TopPlaceDomain object;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TopPlaceDomain topPlace = (TopPlaceDomain) getIntent().getSerializableExtra("top_place_item");

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBundles();



        binding.imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                if (isFavorite) {
                    binding.imageView5.setImageResource(R.drawable.baseline_favorite_24_red);
                } else {
                    binding.imageView5.setImageResource(R.drawable.baseline_favorite_24);
                }
            }
        });
    }

    private void getBundles() {
        object = (TopPlaceDomain) getIntent().getSerializableExtra("top_place_item");

        int drawableResourceId = this.getResources().getIdentifier(object.getPicUrl(), "drawable", this.getPackageName());

        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.PlacePic);

        binding.titleTxt.setText(object.getTitle());
        binding.LocationTxt.setText(object.getLocation());
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle save button click
            }
        });
    }
}

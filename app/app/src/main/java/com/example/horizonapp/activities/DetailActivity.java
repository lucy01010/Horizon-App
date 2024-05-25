package com.example.horizonapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.horizonapp.R;
import com.example.horizonapp.databinding.ActivityDetailBinding;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public final class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private boolean isFavorite = false;
    private TopPlaceDomain topPlace;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<String> savedPlaces;

    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        savedPlaces = new ArrayList<>();

        topPlace = (TopPlaceDomain) getIntent().getSerializableExtra("top_place_item");

        getBundles();

        // Load saved places for current user
        if (currentUser != null) {
            loadSavedPlaces();
        }

        View.OnClickListener favoriteClickListener = v -> {
            isFavorite = !isFavorite;
            updateFavoriteStatus();
            saveCurrentPlace();
        };

        binding.imageView5.setOnClickListener(favoriteClickListener);
        binding.saveBtn.setOnClickListener(favoriteClickListener);

        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void getBundles() {
        String picUrl = topPlace.getPicUrl();
        if (picUrl != null && (picUrl.startsWith("http://") || picUrl.startsWith("https://"))) {
            // Load image from URL
            Glide.with(this)
                    .load(picUrl)
                    .into(binding.PlacePic);
        } else {
            // Load image from drawable resources
            int drawableResourceId = this.getResources().getIdentifier(picUrl, "drawable", this.getPackageName());
            Glide.with(this)
                    .load(drawableResourceId)
                    .into(binding.PlacePic);
        }

        binding.titleTxt.setText(topPlace.getTitle());
        binding.LocationTxt.setText(topPlace.getLocation());
    }

    private void loadSavedPlaces() {
        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("saved")) {
                savedPlaces = (List<String>) documentSnapshot.get("saved");
                // Check if the current place is already saved
                if (savedPlaces != null && savedPlaces.contains(topPlace.getTitle())) {
                    isFavorite = true;
                    updateFavoriteStatus();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user data", e);
        });
    }

    private void updateFavoriteStatus() {
        if (isFavorite) {
            binding.imageView5.setImageResource(R.drawable.baseline_favorite_24_red);
            binding.saveBtn.setVisibility(View.GONE);
        } else {
            binding.imageView5.setImageResource(R.drawable.baseline_favorite_24);
            binding.saveBtn.setVisibility(View.VISIBLE);
        }
    }

    private void saveCurrentPlace() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (isFavorite && !savedPlaces.contains(topPlace.getTitle())) {
                savedPlaces.add(topPlace.getTitle());
            } else {
                savedPlaces.remove(topPlace.getTitle());
            }

            db.collection("users").document(userId)
                    .update("saved", savedPlaces)
                    .addOnSuccessListener(aVoid -> {
                        if (isFavorite) {
                            Toast.makeText(DetailActivity.this, "Place saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailActivity.this, "Place removed successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DetailActivity.this, "Failed to save place", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}

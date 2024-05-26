package com.example.horizonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.R;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavedPostsActivity extends AppCompatActivity implements TopPlacesAdapter.OnItemClickListener {

    private static final String TAG = "SavedPostsActivity";

    private RecyclerView savedPlacesRecyclerView;
    private TopPlacesAdapter topPlacesAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> savedPlaces = new ArrayList<>();
    private ArrayList<TopPlaceDomain> listOfSavedPlaces = new ArrayList<>();

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        executorService = Executors.newFixedThreadPool(3);  // Create a thread pool with 3 threads
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set up the Saved Places RecyclerView
        savedPlacesRecyclerView = findViewById(R.id.savedPlacesRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        savedPlacesRecyclerView.setLayoutManager(layoutManager);

        // Fetch saved places
        fetchSavedPlaces();
    }

    private void fetchSavedPlaces() {
        Log.d(TAG, "fetchSavedPlaces called");
        if (currentUser != null) {
            executorService.execute(() -> {
                db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists() && document.contains("saved")) {
                            savedPlaces = (List<String>) document.get("saved");
                            if (savedPlaces == null) {
                                savedPlaces = new ArrayList<>();
                            }
                        }
                        Log.d(TAG, "Saved places fetched: " + savedPlaces.size());
                        runOnUiThread(this::fetchSavedPlacesDetails);
                    } else {
                        Log.d(TAG, "Error getting saved places from Firestore", task.getException());
                        runOnUiThread(() ->
                                Toast.makeText(SavedPostsActivity.this, "Error getting data from Firestore", Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            });
        }
    }

    private void fetchSavedPlacesDetails() {
        Log.d(TAG, "fetchSavedPlacesDetails called");
        if (!savedPlaces.isEmpty()) {
            executorService.execute(() -> {
                db.collection("top_places")
                        .whereIn("title", savedPlaces)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                listOfSavedPlaces.clear();
                                for (DocumentSnapshot document : task.getResult()) {
                                    String title = document.getString("title");
                                    String picUrl = document.getString("picUrl");
                                    String location = document.getString("location");
                                    TopPlaceDomain place = new TopPlaceDomain(title, picUrl, location);
                                    place.setFavorite(true);  // Set as favorite since it's saved
                                    listOfSavedPlaces.add(place);
                                }
                                Log.d(TAG, "Saved places details fetched: " + listOfSavedPlaces.size());
                                runOnUiThread(() -> {
                                    topPlacesAdapter = new TopPlacesAdapter(SavedPostsActivity.this, listOfSavedPlaces, this);
                                    savedPlacesRecyclerView.setAdapter(topPlacesAdapter);
                                    topPlacesAdapter.notifyDataSetChanged();
                                    Log.d(TAG, "Saved places adapter set and notified");
                                });
                            } else {
                                Log.d(TAG, "Error getting saved places details from Firestore", task.getException());
                                runOnUiThread(() ->
                                        Toast.makeText(SavedPostsActivity.this, "Error getting data from Firestore", Toast.LENGTH_SHORT).show()
                                );
                            }
                        });
            });
        } else {
            Log.d(TAG, "No saved places found");
            runOnUiThread(() -> {
                topPlacesAdapter = new TopPlacesAdapter(SavedPostsActivity.this, listOfSavedPlaces, this);
                savedPlacesRecyclerView.setAdapter(topPlacesAdapter);
                topPlacesAdapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onItemClick(TopPlaceDomain item) {
        Intent intent = new Intent(SavedPostsActivity.this, com.example.horizonapp.activities.DetailActivity.class);
        intent.putExtra("top_place_item", item);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(TopPlaceDomain item) {
        if (item.isFavorite()) {
            savedPlaces.remove(item.getTitle());
            item.setFavorite(false);
        } else {
            savedPlaces.add(item.getTitle());
            item.setFavorite(true);
        }
        updateSavedPlaces();
    }

    private void updateSavedPlaces() {
        Log.d(TAG, "updateSavedPlaces called");
        if (currentUser != null) {
            executorService.execute(() -> {
                db.collection("users").document(currentUser.getUid())
                        .update("saved", savedPlaces)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Saved places updated in Firestore");
                            } else {
                                Log.d(TAG, "Error updating saved places in Firestore", task.getException());
                            }
                        });
            });
        }
    }
}

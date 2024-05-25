package com.example.horizonapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.R;
import com.example.horizonapp.activities.DetailActivity;
import com.example.horizonapp.activities.SavedPostsActivity;
import com.example.horizonapp.adapters.PopularAdapter;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.PopularDomain;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements TopPlacesAdapter.OnItemClickListener {

    private static final String TAG = "HomeFragment";

    private RecyclerView placesRecyclerView;
    private RecyclerView topPlacesRecyclerView;
    private PopularAdapter popularAdapter;
    private TopPlacesAdapter topPlacesAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> savedPlaces = new ArrayList<>();
    private ArrayList<PopularDomain.Domain> listOfPlaces = new ArrayList<>();
    private ArrayList<TopPlaceDomain> listOfTopPlaces = new ArrayList<>();

    private ExecutorService executorService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newFixedThreadPool(3);  // Create a thread pool with 3 threads
        Log.d(TAG, "onCreate called");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView called");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set up the Popular Places RecyclerView
        placesRecyclerView = root.findViewById(R.id.placesRecyclerView);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        placesRecyclerView.setLayoutManager(popularLayoutManager);

        // Set up the Top Places RecyclerView
        topPlacesRecyclerView = root.findViewById(R.id.topPlacesRecyclerView);
        LinearLayoutManager topPlacesLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        topPlacesRecyclerView.setLayoutManager(topPlacesLayoutManager);

        // Fetch data
        fetchTopPlaces();
        fetchPlaces();

        root.findViewById(R.id.savedButton).setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), SavedPostsActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void fetchTopPlaces() {
        Log.d(TAG, "fetchTopPlaces called");
        executorService.execute(() -> {
            db.collection("top_places")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listOfTopPlaces.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                String title = document.getString("title");
                                String picUrl = document.getString("picUrl");
                                String location = document.getString("location");
                                listOfTopPlaces.add(new TopPlaceDomain(title, picUrl, location));
                            }
                            Log.d(TAG, "Top places fetched: " + listOfTopPlaces.size());
                            requireActivity().runOnUiThread(() -> {
                                topPlacesAdapter = new TopPlacesAdapter(requireActivity(), listOfTopPlaces, this);
                                topPlacesRecyclerView.setAdapter(topPlacesAdapter);
                                fetchSavedPlaces(); // Fetch saved places after top places are loaded
                                Log.d(TAG, "Top places adapter set and notified");
                            });
                        } else {
                            Log.d(TAG, "Error getting top places from Firestore", task.getException());
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    private void fetchPlaces() {
        executorService.execute(() -> {
            db.collection("products")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listOfPlaces.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String location = document.getString("category");
                                String picUrl = document.getString("imageAlpha");
                                listOfPlaces.add(new PopularDomain.Domain(name, picUrl, location));
                            }
                            Log.d(TAG, "Popular places fetched: " + listOfPlaces.size());
                            requireActivity().runOnUiThread(() -> {
                                popularAdapter = new PopularAdapter(listOfPlaces);
                                placesRecyclerView.setAdapter(popularAdapter);
                                popularAdapter.notifyDataSetChanged();
                            });
                        } else {
                            Log.d(TAG, "Error getting places from Firestore", task.getException());
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
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
                        requireActivity().runOnUiThread(this::updateFavoriteStatus);
                    } else {
                        Log.d(TAG, "Error getting saved places from Firestore", task.getException());
                        requireActivity().runOnUiThread(this::updateFavoriteStatus);
                    }
                });
            });
        } else {
            requireActivity().runOnUiThread(this::updateFavoriteStatus);
        }
    }

    private void updateFavoriteStatus() {
        Log.d(TAG, "updateFavoriteStatus called");
        for (TopPlaceDomain place : listOfTopPlaces) {
            place.setFavorite(savedPlaces.contains(place.getTitle()));
        }
        topPlacesAdapter.notifyDataSetChanged();
        Log.d(TAG, "Top places favorite status updated and adapter notified");
    }

    @Override
    public void onItemClick(TopPlaceDomain item) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
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

    @Override
    public void onResume() {
        super.onResume();
        fetchTopPlaces();
    }
}

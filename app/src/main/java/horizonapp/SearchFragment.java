package com.example.horizonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.activities.DetailActivity;
import com.example.horizonapp.adapters.HomeHorAdapter;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.example.horizonapp.models.HomeHorModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements TopPlacesAdapter.OnItemClickListener {

    private RecyclerView homeHorizontalRec;
    private List<HomeHorModel> homeHorModelList;
    private HomeHorAdapter homeHorAdapter;

    private TopPlacesAdapter topPlacesAdapter;
    private List<TopPlaceDomain> topPlacesModelList;
    private RecyclerView topPlacesRecyclerView;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> savedPlaces;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        homeHorizontalRec = root.findViewById(R.id.home_hor_rec);
        homeHorModelList = new ArrayList<>();
        homeHorModelList.add(new HomeHorModel(R.drawable.museum, "Museum"));
        homeHorModelList.add(new HomeHorModel(R.drawable.fortress, "Fortress"));
        homeHorModelList.add(new HomeHorModel(R.drawable.church, "Church"));
        homeHorModelList.add(new HomeHorModel(R.drawable.monastery, "Monastery"));

        homeHorAdapter = new HomeHorAdapter(getActivity(), homeHorModelList);
        homeHorizontalRec.setAdapter(homeHorAdapter);
        homeHorizontalRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);

        topPlacesRecyclerView = root.findViewById(R.id.topPlacesRecyclerView);
        topPlacesModelList = new ArrayList<>();
        topPlacesAdapter = new TopPlacesAdapter(requireContext(), topPlacesModelList, this);
        topPlacesRecyclerView.setAdapter(topPlacesAdapter);
        topPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        topPlacesRecyclerView.setHasFixedSize(true);

        homeHorizontalRec.setNestedScrollingEnabled(false);

        getSavedPlacesAndLoadTopPlaces();

        return root;
    }

    private void getSavedPlacesAndLoadTopPlaces() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists() && document.contains("saved")) {
                        savedPlaces = (List<String>) document.get("saved");
                        if (savedPlaces == null) {
                            savedPlaces = new ArrayList<>();
                        }
                    } else {
                        savedPlaces = new ArrayList<>();
                    }
                    loadTopPlaces();
                } else {
                    savedPlaces = new ArrayList<>();
                    loadTopPlaces();
                }
            });
        } else {
            savedPlaces = new ArrayList<>();
            loadTopPlaces();
        }
    }

    private void loadTopPlaces() {
        topPlacesModelList.clear();
        topPlacesModelList.add(new TopPlaceDomain("Museum", "drawable/museum", "Museum Location"));
        topPlacesModelList.add(new TopPlaceDomain("Fortress", "drawable/fortress", "Fortress Location"));
        topPlacesModelList.add(new TopPlaceDomain("Church", "drawable/church", "Church Location"));
        topPlacesModelList.add(new TopPlaceDomain("Monastery", "drawable/monastery", "Monastery Location"));

        for (TopPlaceDomain place : topPlacesModelList) {
            if (savedPlaces.contains(place.getTitle())) {
                place.setFavorite(true);
            }
        }

        topPlacesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(TopPlaceDomain item) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
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
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .update("saved", savedPlaces);
        }
    }
}

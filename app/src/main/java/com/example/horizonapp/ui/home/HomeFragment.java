package com.example.horizonapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.R;
import com.example.horizonapp.activities.DetailActivity;
import com.example.horizonapp.adapters.PopularAdapter;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.PopularDomain;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements TopPlacesAdapter.OnItemClickListener {

    private static final String TAG = "HomeFragment";

    private TextView category1;
    private TextView category2;
    private TextView category3;
    private TextView category4;

    private RecyclerView placesRecyclerView;
    private RecyclerView topPlacesRecyclerView;
    private PopularAdapter popularAdapter;
    private FirebaseFirestore db;
    ArrayList<PopularDomain.Domain> listOfPlaces = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        getPlaces();
        category1 = root.findViewById(R.id.category1);

        // Set up the Popular Places RecyclerView
        placesRecyclerView = root.findViewById(R.id.placesRecyclerView);
        popularAdapter = new PopularAdapter(listOfPlaces);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        placesRecyclerView.setLayoutManager(popularLayoutManager);
        placesRecyclerView.setAdapter(popularAdapter);

        // Set up the Top Places RecyclerView
        topPlacesRecyclerView = root.findViewById(R.id.topPlacesRecyclerView);
        ArrayList<TopPlaceDomain> listOfTopPlaces = getTopPlaces();
        TopPlacesAdapter topPlacesAdapter = new TopPlacesAdapter(listOfTopPlaces, this);
        LinearLayoutManager topPlacesLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        topPlacesRecyclerView.setLayoutManager(topPlacesLayoutManager);
        topPlacesRecyclerView.setAdapter(topPlacesAdapter);

        return root;
    }

    private void getPlaces() {
        listOfPlaces.clear();

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Iterate through each document in the collection
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Log.i(TAG, "Document data: " + document.getData());
                                // Parse the document data and add it to the ArrayList
                                String name = document.getString("name");
                                String location = document.getString("category");
                                String picUrl = document.getString("imageAlpha");
                                listOfPlaces.add(new PopularDomain.Domain(name, picUrl, location));
                            }
                            // Notify the adapter if needed
                            popularAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents: query snapshot is null");
                            Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ArrayList<TopPlaceDomain> getTopPlaces() {
        ArrayList<TopPlaceDomain> topPlaces = new ArrayList<>();
        topPlaces.add(new TopPlaceDomain("Cascade", "cascade", "Yerevan"));
        topPlaces.add(new TopPlaceDomain("Military Museum", "military_museum", "Yerevan"));
        topPlaces.add(new TopPlaceDomain("Cascade", "cascade", "Yerevan"));
        topPlaces.add(new TopPlaceDomain("Military Museum", "military_museum", "Yerevan"));
        return topPlaces;
    }

    @Override
    public void onItemClick(TopPlaceDomain item) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("top_place_item", item);
        startActivity(intent);
    }
    
}
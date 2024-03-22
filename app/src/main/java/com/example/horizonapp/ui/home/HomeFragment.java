package com.example.horizonapp.ui.home;

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
import com.example.horizonapp.adapters.PopularAdapter;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.PopularDomain;
import com.example.horizonapp.domain.TopPlaceDomain;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView category1;
    private TextView category2;
    private TextView category3;
    private TextView category4;

    private RecyclerView placesRecyclerView;
    private RecyclerView topPlacesRecyclerView;
    private TopPlacesAdapter topPlacesAdapter;
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

        //-------------------------------------------------
        //-------------------------------------------------

        // Set up the Top Places RecyclerView
        topPlacesRecyclerView = root.findViewById(R.id.topPlacesRecyclerView);
        ArrayList<TopPlaceDomain> listOfTopPlaces = getTopPlaces();
        topPlacesAdapter = new TopPlacesAdapter(listOfTopPlaces);

        LinearLayoutManager topPlacesLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        topPlacesRecyclerView.setLayoutManager(topPlacesLayoutManager);
        topPlacesRecyclerView.setAdapter(topPlacesAdapter);

        //-------------------------------------------------

        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle category 1 click
            }
        });




        return root;
    }

    private void getPlaces() {
        // Clear the list before fetching new data
        listOfPlaces.clear();

        // Get the Firestore collection reference
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
                                String location = document.getString("description");
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

    private void saveProductToFirestore(String name, String description, String imageAlpha) {
        // Create a new document with a generated ID
        db.collection("products")
                .add(getProductMap(name, description, imageAlpha))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Product added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding product", e));
    }

    private Map<String, Object> getProductMap(String name, String location, String imageAlpha) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("location", location);
        product.put("imageAlpha", imageAlpha);
        return product;
    }
}

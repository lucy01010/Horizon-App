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

public class HomeFragment extends Fragment {

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
        category2 = root.findViewById(R.id.category2);
        category3 = root.findViewById(R.id.category3);
        category4 = root.findViewById(R.id.category4);

        // Set up the Popular Places RecyclerView
        placesRecyclerView = root.findViewById(R.id.placesRecyclerView);

        popularAdapter = new PopularAdapter(listOfPlaces);

        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        placesRecyclerView.setLayoutManager(popularLayoutManager);
        placesRecyclerView.setAdapter(popularAdapter);

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

        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle category 2 click
            }
        });

        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle category 3 click
            }
        });

        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle category 4 click
            }
        });

        return root;
    }

    private void getPlaces() {
        // Initialize an empty ArrayList to store places

        // Get the Firestore collection reference
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Iterate through each document in the collection
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                
                                Log.i("TAG", "Document data: " + document.getData());
                                // Parse the document data and add it to the ArrayList
                                String name = document.getString("name");
                                String location = document.getString("description");
                                listOfPlaces.add(new PopularDomain.Domain(name, "https://www.google.com/imgres?imgurl=https%3A%2F%2Ffacilitiesbyadf.com%2Fwp-content%2Fuploads%2F2022%2F03%2Fadf-2022-full-colour.png&tbnid=OMh5aCDiNNNUkM&vet=12ahUKEwj5wY3mj4CFAxVn0bsIHdlQB6UQMygAegQIARBT..i&imgrefurl=https%3A%2F%2Ffacilitiesbyadf.com%2F&docid=UclKdEGa5AS_IM&w=2362&h=1123&q=adf&ved=2ahUKEwj5wY3mj4CFAxVn0bsIHdlQB6UQMygAegQIARBT", location));
                            }
                            // Notify the adapter if needed
                            popularAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("TAG", "Error getting documents: query snapshot is null");
                            Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
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
}
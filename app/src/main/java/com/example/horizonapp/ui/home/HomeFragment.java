package com.example.horizonapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.R;
import com.example.horizonapp.adapters.PopularAdapter;
import com.example.horizonapp.adapters.TopPlacesAdapter;
import com.example.horizonapp.domain.PopularDomain;
import com.example.horizonapp.domain.TopPlaceDomain;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        category1 = root.findViewById(R.id.category1);
        category2 = root.findViewById(R.id.category2);
        category3 = root.findViewById(R.id.category3);
        category4 = root.findViewById(R.id.category4);

        // Set up the Popular Places RecyclerView
        placesRecyclerView = root.findViewById(R.id.placesRecyclerView);
        ArrayList<PopularDomain.Domain> listOfPlaces = getPlaces();
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

    private ArrayList<PopularDomain.Domain> getPlaces() {
        ArrayList<PopularDomain.Domain> places = new ArrayList<>();
        // Add places to the list
        places.add(new PopularDomain.Domain("History Museum", "historymuseum", "Yerevan"));
        places.add(new PopularDomain.Domain("Military Museum", "military_museum", "Yerevan"));
        places.add(new PopularDomain.Domain("Cascade", "cascade", "Yerevan"));
        places.add(new PopularDomain.Domain("Garni Temple", "garni", "Kotayk Province"));
        return places;
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

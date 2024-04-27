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

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements TopPlacesAdapter.OnItemClickListener {

    RecyclerView homeHorizontalRec;
    List<HomeHorModel> homeHorModelList;
    HomeHorAdapter homeHorAdapter;

    TopPlacesAdapter topPlacesAdapter;
    List<TopPlaceDomain> topPlacesModelList;
    RecyclerView topPlacesRecyclerView;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search,container,false);

        homeHorizontalRec = root.findViewById(R.id.home_hor_rec);

        homeHorModelList = new ArrayList<>();
        homeHorModelList.add(new HomeHorModel(R.drawable.museum,"Museum"));
        homeHorModelList.add(new HomeHorModel(R.drawable.fortress,"Fortress"));
        homeHorModelList.add(new HomeHorModel(R.drawable.church,"Church"));
        homeHorModelList.add(new HomeHorModel(R.drawable.monastery,"Monastery"));

        homeHorAdapter = new HomeHorAdapter(getActivity(),homeHorModelList);
        homeHorizontalRec.setAdapter(homeHorAdapter);
        homeHorizontalRec.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);



        topPlacesRecyclerView = root.findViewById(R.id.topPlacesRecyclerView);

        topPlacesModelList = new ArrayList<>();
        topPlacesModelList.add(new TopPlaceDomain("Museum", "drawable/museum", "Museum Location"));
        topPlacesModelList.add(new TopPlaceDomain("Fortress", "drawable/fortress", "Fortress Location"));
        topPlacesModelList.add(new TopPlaceDomain("Church", "drawable/church", "Church Location"));
        topPlacesModelList.add(new TopPlaceDomain("Monastery", "drawable/monastery", "Monastery Location"));

        topPlacesAdapter = new TopPlacesAdapter(requireContext(), topPlacesModelList,  this);
        topPlacesRecyclerView.setAdapter(topPlacesAdapter);
        topPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        topPlacesRecyclerView.setHasFixedSize(true);

        homeHorizontalRec.setNestedScrollingEnabled(false);

        return root;


    }


    @Override
    public void onItemClick(TopPlaceDomain item) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("top_place_item", item);
        startActivity(intent);
    }
}
package com.example.horizonapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizonapp.R;
import com.example.horizonapp.adapters.HomeHorAdapter;
import com.example.horizonapp.models.HomeHorModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView homeHorizontalRec;
    List<HomeHorModel> homeHorModelList;
    HomeHorAdapter homeHorAdapter;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container,false);

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
        return root;


    }

}
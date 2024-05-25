package com.example.horizonapp;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bott_nav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager
                ().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).
                getNavController();


        BottomNavigationView bottomNav = findViewById(R.id.nvMain);
        NavigationUI.setupWithNavController(bottomNav, navController);

    }
}
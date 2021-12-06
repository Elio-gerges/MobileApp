package com.mobileappcourse.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.CategoryFragment;
import fragments.FavoriteFragment;
import fragments.HomeFragment;
import fragments.VideoFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    // Creating a strategy to start the correct fragment
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        // Each item clicked initiate the correspondent fragment
                        case R.id.nav_home:
                            // Initialized a HomeFragment from singleton mode to send context for
                            //  API fetching.
                            selectedFragment = HomeFragment.newInstance(getApplicationContext());
                            break;
                        case R.id.nav_favorite:
                            selectedFragment = FavoriteFragment.newInstance(getApplicationContext());
                            break;
                        case R.id.nav_video:
                            selectedFragment = new VideoFragment();
                            break;
                        case R.id.nav_category:
                            selectedFragment = CategoryFragment.newInstance(getApplicationContext());
                            break;
                    }

                    //Committing the fragment
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        // Setting the bottom navigator to be the home
        // This will allow to auto start the home fragment
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}
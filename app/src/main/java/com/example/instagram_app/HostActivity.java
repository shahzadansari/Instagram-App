package com.example.instagram_app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "HostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initBottomNavigation();

        if (savedInstanceState == null) {
            openHomeFragment();
        }
    }

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.page_home:
                    openHomeFragment();
                    break;
                case R.id.page_profile:
                    openProfileFragment();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void openProfileFragment() {
        navController.navigate(R.id.profileFragment);
    }

    private void openHomeFragment() {
        navController.navigate(R.id.homeFragment);
    }

    private void openHostFragment() {
        navController.navigate(R.id.hostFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }
}
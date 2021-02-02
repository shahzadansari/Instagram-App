package com.example.instagram_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostActivity extends AppCompatActivity {

    private static final String TAG = "HostActivity";
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(TAG, "onCreate: called" + destination.getDisplayName());
            if (destination.getId() == R.id.homeFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }

            if (destination.getId() == R.id.editProfileFragment ||
                    destination.getId() == R.id.shareFragment ||
                    destination.getId() == R.id.galleryFragment ||
                    destination.getId() == R.id.photoFragment ||
                    destination.getId() == R.id.editPhotoFragment ||
                    destination.getId() == R.id.viewPostFragment ||
                    destination.getId() == R.id.viewCommentsFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            openHomeFragment();
        }
    }

    private void openHomeFragment() {
        navController.navigate(R.id.homeFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }
}
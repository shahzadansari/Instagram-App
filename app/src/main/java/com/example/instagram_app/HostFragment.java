package com.example.instagram_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
//        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

//        initBottomNavigation();
//        if (savedInstanceState == null) {
//            openHomeFragment();
//        }
    }

//    private void initBottomNavigation() {
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            int id = item.getItemId();
//            switch (id) {
//                case R.id.page_home:
//                    openHomeFragment();
//                    break;
//                case R.id.page_profile:
//                    openProfileFragment();
//                    break;
//                default:
//                    break;
//            }
//            return true;
//        });
//    }
//
//    private void openProfileFragment() {
//        navController.navigate(R.id.action_hostFragment_to_profileFragment);
//    }
//
//    private void openHomeFragment() {
//        navController.navigate(R.id.action_hostFragment_to_homeFragment);
//    }
}
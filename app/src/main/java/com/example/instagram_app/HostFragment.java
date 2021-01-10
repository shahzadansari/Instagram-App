package com.example.instagram_app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "HomeFragment";

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
        View rootView = inflater.inflate(R.layout.fragment_host, container, false);
        bottomNavigationView = rootView.findViewById(R.id.bottom_navigation);
        initBottomNavigation();

        return rootView;
    }

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.page_feed:
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container_home, new HomeFragment())
                            .setReorderingAllowed(true)
                            .commit();
                    break;
                case R.id.page_profile:
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container_home, new ProfileFragment())
                            .setReorderingAllowed(true)
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }
}
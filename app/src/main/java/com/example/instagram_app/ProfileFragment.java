package com.example.instagram_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private TextView textViewEditProfile;
    private NavController navController;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        textViewEditProfile = view.findViewById(R.id.text_view_edit_profile);
        textViewEditProfile.setOnClickListener(v -> openAccountSettingsFragment());
    }

    private void openAccountSettingsFragment() {
        navController.navigate(R.id.action_profileFragment_to_accountSettingsFragment);
    }
}
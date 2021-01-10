package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private TextView textViewEditProfile;

    public ProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        textViewEditProfile = rootView.findViewById(R.id.text_view_edit_profile);
        textViewEditProfile.setOnClickListener(v -> openAccountSettingsActivity());

        return rootView;
    }

    private void openAccountSettingsActivity() {
        Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
        startActivity(intent);
    }
}
package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.instagram_app.R;

public class GalleryFragment extends Fragment {

    private ImageView imageViewClose;
    private TextView textViewNext;
    private Spinner spinner;
    private GridView gridView;

    private NavController navController;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        imageViewClose = rootView.findViewById(R.id.image_view_close);
        textViewNext = rootView.findViewById(R.id.text_view_next);
        spinner = rootView.findViewById(R.id.spinnerDirectory);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewClose.setOnClickListener(v -> {
            navController.navigateUp();
        });
    }
}
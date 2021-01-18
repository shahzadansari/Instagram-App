package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;

public class EditPhotoFragment extends Fragment {
    private static final String TAG = "EditPhotoFragment";
    private String imagePath;
    private ImageView imageView;

    public EditPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = EditPhotoFragmentArgs.fromBundle(getArguments()).getImagePath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        imageView = rootView.findViewById(R.id.image_view);

        Glide.with(getActivity())
                .load(imagePath)
                .into(imageView);

        return rootView;
    }
}
package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.R;

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";
    private String caption;
    private String authorUsername;
    private String authorProfilePhotoUrl;

    public ViewCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caption = ViewCommentsFragmentArgs.fromBundle(getArguments()).getCaption();
        authorUsername = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorUsername();
        authorProfilePhotoUrl = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorProfilePhotoUrl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_comments, container, false);
    }
}
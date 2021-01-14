package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.FragmentsAdapter;
import com.google.android.material.tabs.TabLayout;

public class ShareFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        tabLayout = rootView.findViewById(R.id.tabs);
        viewPager = rootView.findViewById(R.id.viewpager);

        initViewPager();

        return rootView;
    }

    private void initViewPager() {
        viewPager.setAdapter(new FragmentsAdapter(getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }
}
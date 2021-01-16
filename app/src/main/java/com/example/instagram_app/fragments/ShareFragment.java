package com.example.instagram_app.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.FragmentsAdapter;
import com.example.instagram_app.utils.Permissions;
import com.google.android.material.tabs.TabLayout;

public class ShareFragment extends Fragment {

    private static final String TAG = "ShareFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            Log.d(TAG, "onCreate: checkPermissionsArray: called");
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
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

    public boolean checkPermissionsArray(String[] permissions) {

        for (String permission : permissions) {
            if (!checkPermissions(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission) {

        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public void verifyPermissions(String[] permissions) {

        ActivityCompat.requestPermissions(
                getActivity(),
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
}
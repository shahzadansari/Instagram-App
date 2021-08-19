package com.example.instagram_app.ui.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.R;
import com.example.instagram_app.ui.fragments.ShareFragmentArgs;
import com.example.instagram_app.utils.Permissions;
import com.google.android.material.tabs.TabLayout;

public class ShareFragment extends Fragment {

    private static final String TAG = "ShareFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private final int REQUEST_CODE_PROFILE_PHOTO = 1;
    private TabLayout tabLayout;
    private int requestCode;

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

        requestCode = ShareFragmentArgs.fromBundle(getArguments()).getRequestCode();

        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            Log.d(TAG, "onCreate: fragment called for profile photo request");
        } else {
            Log.d(TAG, "onCreate: fragment called for share photo request");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        tabLayout = rootView.findViewById(R.id.tabs);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        initTabs();
        openGalleryFragment();

        return rootView;
    }

    private void openGalleryFragment() {
        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new GalleryFragment(REQUEST_CODE_PROFILE_PHOTO))
                    .setReorderingAllowed(true)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new GalleryFragment())
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    private void openPhotoFragment() {
        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PhotoFragment(REQUEST_CODE_PROFILE_PHOTO))
                    .setReorderingAllowed(true)
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PhotoFragment())
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Gallery"));
        tabLayout.addTab(tabLayout.newTab().setText("Photo"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    openPhotoFragment();
                } else {
                    openGalleryFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        int permissionRequest = ActivityCompat
                .checkSelfPermission(requireActivity(), permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    public void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
}
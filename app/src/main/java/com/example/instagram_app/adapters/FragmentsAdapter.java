package com.example.instagram_app.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.instagram_app.fragments.GalleryFragment;
import com.example.instagram_app.fragments.PhotoFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new GalleryFragment();
        } else {
            return new PhotoFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Gallery";
        } else {
            return "Photo";
        }
    }
}


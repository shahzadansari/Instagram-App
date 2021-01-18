package com.example.instagram_app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridImageAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> mUrls;

    public GridImageAdapter(Context context, ArrayList<String> urls) {
        this.mContext = context;
        this.mUrls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        Glide.with(mContext)
                .load(mUrls.get(position))
                .into(imageView);
        return imageView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}

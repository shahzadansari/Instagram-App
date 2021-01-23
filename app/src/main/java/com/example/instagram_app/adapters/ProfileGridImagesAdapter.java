package com.example.instagram_app.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;

import java.util.ArrayList;

public class ProfileGridImagesAdapter extends BaseAdapter {

    private static final String TAG = "ProfileGridImagesAdapte";
    private final Context mContext;
    private final ArrayList<Photo> photos;

    public ProfileGridImagesAdapter(Context context, ArrayList<Photo> urls) {
        this.mContext = context;
        this.photos = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.layout_grid_images, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view);
        ProgressBar progressBar = convertView.findViewById(R.id.progress_bar_img);

        Glide.with(mContext)
                .load(photos.get(position).getImage_path())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}

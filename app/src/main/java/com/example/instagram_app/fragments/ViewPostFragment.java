package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    private CircleImageView profilePhoto;
    private ImageView imageViewProfilePhoto, postImage, imageViewEllipses,
            imageViewChecked, imageViewUnchecked, imageViewComments;
    private TextView textViewUsername, textViewLikes, textViewCaption,
            textViewCommentsLink, textViewTimePosted;

    private Photo currentPhoto;

    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPhoto = ViewPostFragmentArgs.fromBundle(getArguments()).getPhotoData();
        Log.d(TAG, "onCreate: " + currentPhoto.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_post, container, false);
        imageViewProfilePhoto = rootView.findViewById(R.id.profile_photo);
        imageViewEllipses = rootView.findViewById(R.id.image_view_ellipses);
        postImage = rootView.findViewById(R.id.post_image);
        imageViewChecked = rootView.findViewById(R.id.image_view_heart_checked);
        imageViewUnchecked = rootView.findViewById(R.id.image_view_heart_unchecked);
        imageViewComments = rootView.findViewById(R.id.image_view_comments);
        textViewUsername = rootView.findViewById(R.id.text_view_username);
        textViewLikes = rootView.findViewById(R.id.text_view_likes);
        textViewCaption = rootView.findViewById(R.id.text_view_image_caption);
        textViewCommentsLink = rootView.findViewById(R.id.text_view_image_comments_link);
        textViewTimePosted = rootView.findViewById(R.id.text_view_time_post_created);

        updateUI();
        return rootView;
    }

    private void updateUI() {
        textViewCaption.setText(currentPhoto.getCaption());
        textViewTimePosted.setText(currentPhoto.getDate_created());
        Glide.with(this)
                .load(currentPhoto.getImage_path())
                .into(postImage);
    }
}
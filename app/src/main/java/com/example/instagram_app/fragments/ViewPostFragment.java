package com.example.instagram_app.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    private CircleImageView profilePhoto;
    private ImageView postImage, imageViewEllipses,
            imageViewUnchecked, imageViewComments;
    private TextView textViewUsername, textViewLikes, textViewCaption,
            textViewCommentsLink, textViewTimePosted;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Photo currentPhoto;

    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPhoto = ViewPostFragmentArgs.fromBundle(getArguments()).getPhotoData();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_post, container, false);
        profilePhoto = rootView.findViewById(R.id.profile_photo);
        imageViewEllipses = rootView.findViewById(R.id.image_view_ellipses);
        postImage = rootView.findViewById(R.id.post_image);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAuthorData(snapshot);

                getLikes(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageViewUnchecked.setOnClickListener(v -> {
            ImageView imageView = (ImageView) v;
            Drawable drawable = imageView.getDrawable();

            if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.ic_heart_unchecked).getConstantState())) {

                imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_checked));
                Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();

            } else {

                imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_unchecked));
                Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLikes(DataSnapshot snapshot) {

        List<String> userIds = new ArrayList<>();
        for (DataSnapshot dataValues : snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("likes")
                .getChildren()) {
            String user_id = (String) dataValues.getValue();
            userIds.add(user_id);
        }

        for (String user_id : userIds) {
            Log.d(TAG, "getLikes: " + user_id);
        }
    }

    private void updateUI() {
        textViewCaption.setText(currentPhoto.getCaption());

        String dateToTimeFormat = Utils.DateToTimeFormat(currentPhoto.getDate_created());
        textViewTimePosted.setText(dateToTimeFormat);

        Glide.with(this)
                .load(currentPhoto.getImage_path())
                .into(postImage);
    }

    public void getAuthorData(DataSnapshot snapshot) {
        UserAccountSettings userAccountSettings = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(currentPhoto.getUser_id()) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        String authorUsername = userAccountSettings.getUsername();
        String authorProfilePhotoUrl = userAccountSettings.getProfile_photo();

        textViewUsername.setText(authorUsername);
        Glide.with(this)
                .load(authorProfilePhotoUrl)
                .into(profilePhoto);
    }
}
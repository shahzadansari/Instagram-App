package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    private CircleImageView profilePhoto;
    private ImageView postImage, imageViewEllipses,
            imageViewLike, imageViewComments;
    private TextView textViewUsername, textViewLikes, textViewCaption,
            textViewCommentsLink, textViewTimePosted;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private UserAccountSettings userAccountSettings;
    private Photo currentPhoto;
    private Boolean isLiked = false;

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
        imageViewLike = rootView.findViewById(R.id.image_view_heart_unchecked);
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

        imageViewLike.setOnClickListener(v -> {

            if (isLiked) {

                imageViewLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_unchecked));
                myRef.child("photos")
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();
                isLiked = false;

            } else {

                imageViewLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_checked));
                myRef.child("photos")
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mAuth.getUid());
                isLiked = true;

            }
        });
    }

    private void getLikes(DataSnapshot snapshot) {
        StringBuilder mUsers = new StringBuilder();

        long totalLikes = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("likes")
                .getChildrenCount();

        Log.d(TAG, "getLikes: totalLikes: " + totalLikes);

        if (totalLikes > 0) {
            for (DataSnapshot userIdsSnapshot : snapshot
                    .child("photos")
                    .child(currentPhoto.getPhoto_id())
                    .child("likes")
                    .getChildren()) {

                String user_id = (String) userIdsSnapshot.getValue();

                User user = snapshot
                        .child("users") // users node
                        .child(user_id) // user_id
                        .getValue(User.class); // data from that node

                mUsers.append(user.getUsername());
                mUsers.append(",");
                String likesString = createLikesString(mUsers);
                textViewLikes.setText(likesString);
                isLiked = mUsers.toString().contains(userAccountSettings.getUsername());

                if (isLiked) {
                    imageViewLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_checked));
                } else {
                    imageViewLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_unchecked));
                }
            }
        } else {
            String likesString = createLikesString(null);
            textViewLikes.setText(likesString);
        }
    }


    private String createLikesString(StringBuilder usersStringBuilder) {
        if (usersStringBuilder == null)
            return "No likes yet";

        String likesString;

        String[] splitUsers = usersStringBuilder.toString().split(",");
        int length = splitUsers.length;

        if (length == 1) {
            likesString = "Liked by " + splitUsers[0];
        } else if (length == 2) {
            likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];
        } else if (length == 3) {
            likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + splitUsers[2];
        } else if (length >= 4) {
            likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + (length - 2) + " others.";
        } else {
            likesString = "No likes yet";
        }

        return likesString;
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
        userAccountSettings = snapshot
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
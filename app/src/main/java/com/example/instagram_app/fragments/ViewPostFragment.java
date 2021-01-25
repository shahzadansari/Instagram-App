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
import com.example.instagram_app.model.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    private CircleImageView profilePhoto;
    private ImageView postImage, imageViewEllipses,
            imageViewChecked, imageViewUnchecked, imageViewComments;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAuthorData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI() {
        String timestampDiff = getTimestampDifference();
        if (!timestampDiff.equals("0")) {
            textViewTimePosted.setText(timestampDiff + " days ago");
        } else {
            textViewTimePosted.setText("Today");
        }

        textViewCaption.setText(currentPhoto.getCaption());

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

    private String getTimestampDifference() {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = currentPhoto.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }
}
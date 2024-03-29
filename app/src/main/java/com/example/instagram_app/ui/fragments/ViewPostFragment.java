package com.example.instagram_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.api.NotificationAPI;
import com.example.instagram_app.api.ServiceGenerator;
import com.example.instagram_app.model.NotificationData;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.model.PushNotification;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";
    private CircleImageView profilePhoto;
    private ImageView postImage, imageViewEllipses,
            imageViewLike, imageViewComments;
    private TextView textViewUsername, textViewLikes, textViewCaption,
            textViewCommentsLink, textViewTimePosted;

    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private UserAccountSettings authorData;
    private UserAccountSettings currentUserData;
    private Photo currentPhoto;
    private Boolean isLiked = false;

    private ValueEventListener valueEventListener;

    private String receiverFCMToken;
    private String currentUsername;

    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPhoto = ViewPostFragmentArgs
                .fromBundle(getArguments()).getPhotoData();

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        updateUI();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        myRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getAuthorData(snapshot);
                getCurrentUserData(snapshot);
                getLikes(snapshot);
                getComments(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getLikes(snapshot);
                getComments(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        textViewCommentsLink.setOnClickListener(v -> {
            NavDirections navDirections = ViewPostFragmentDirections
                    .actionViewPostFragmentToViewCommentsFragment(currentPhoto)
                    .setAuthorUsername(authorData.getUsername())
                    .setAuthorProfilePhotoUrl(authorData.getProfile_photo());
            navController.navigate(navDirections);
        });

        imageViewComments.setOnClickListener(v -> {
            NavDirections navDirections = ViewPostFragmentDirections
                    .actionViewPostFragmentToViewCommentsFragment(currentPhoto)
                    .setAuthorUsername(authorData.getUsername())
                    .setAuthorProfilePhotoUrl(authorData.getProfile_photo());
            navController.navigate(navDirections);
        });

        imageViewLike.setOnClickListener(v -> {

            if (isLiked) {

                imageViewLike.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_heart_unchecked));
                myRef.child("photos")
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .removeValue();

                myRef.child("user_photos")
                        .child(currentPhoto.getUser_id())
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

                myRef.child("user_photos")
                        .child(currentPhoto.getUser_id())
                        .child(currentPhoto.getPhoto_id())
                        .child("likes")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mAuth.getUid());
                isLiked = true;
                sendLikedNotification();
            }
        });
    }

    private void sendLikedNotification() {
        NotificationData notificationData = new NotificationData(
                "New Like!",
                currentUsername + " liked your post: " + currentPhoto.getCaption()
        );

        PushNotification pushNotification = new PushNotification(
                notificationData,
                receiverFCMToken
        );

        NotificationAPI notificationAPI = ServiceGenerator
                .createService(NotificationAPI.class);
        Call call = notificationAPI.postNotification(pushNotification);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d(TAG, "onFailure: error -> " + t.getLocalizedMessage());
            }
        });
    }

    private void getComments(DataSnapshot snapshot) {

        long totalComments = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("comments")
                .getChildrenCount();

        if (totalComments > 1) {
            textViewCommentsLink.setText("View all " + totalComments + " comments");
        } else {
            textViewCommentsLink.setText("View all comments");
        }
    }

    private void getLikes(DataSnapshot snapshot) {

        StringBuilder mUsers = new StringBuilder();

        long totalLikes = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("likes")
                .getChildrenCount();

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

                /** fix "match found" bug e.g. shahzad & shahzadansari06 etc */
                isLiked = mUsers.toString()
                        .contains(currentUserData.getUsername() + "");

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
        authorData = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(currentPhoto.getUser_id()) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        receiverFCMToken = authorData.getFcmToken();

        String authorUsername = authorData.getUsername();
        String authorProfilePhotoUrl = authorData.getProfile_photo();

        textViewUsername.setText(authorUsername);
        Glide.with(this)
                .load(authorProfilePhotoUrl)
                .into(profilePhoto);
    }

    public void getCurrentUserData(DataSnapshot snapshot) {
        currentUserData = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(mAuth.getUid()) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        currentUsername = currentUserData.getUsername();
    }

    @Override
    public void onPause() {
        super.onPause();
        myRef.removeEventListener(valueEventListener);
    }
}
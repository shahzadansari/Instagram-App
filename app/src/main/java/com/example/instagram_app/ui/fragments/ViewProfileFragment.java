package com.example.instagram_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.instagram_app.adapters.ProfileGridImagesAdapter;
import com.example.instagram_app.api.NotificationAPI;
import com.example.instagram_app.api.ServiceGenerator;
import com.example.instagram_app.model.NotificationData;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.model.PushNotification;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.model.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private ProgressBar progressBar;
    private TextView textViewPosts, textViewFollowers, textViewFollowing,
            textViewDisplayName, textViewDescription, textViewFollow, textViewUnfollow;
    private ImageView imageViewProfilePhoto;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private GridView gridView;

    private String userId;

    private String currentUsername;
    private String receiverFCMToken;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        userId = ViewProfileFragmentArgs.fromBundle(getArguments()).getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        rootView.findViewById(R.id.text_view_display_name);
        progressBar = rootView.findViewById(R.id.progress_bar);
        textViewPosts = rootView.findViewById(R.id.text_view_posts_number);
        textViewFollowers = rootView.findViewById(R.id.text_view_followers_number);
        textViewFollowing = rootView.findViewById(R.id.text_view_following_numbers);
        textViewDisplayName = rootView.findViewById(R.id.text_view_display_name);
        textViewDescription = rootView.findViewById(R.id.text_view_description);
        imageViewProfilePhoto = rootView.findViewById(R.id.profile_image);
        gridView = rootView.findViewById(R.id.gridView);
        textViewFollow = rootView.findViewById(R.id.text_view_follow);
        textViewUnfollow = rootView.findViewById(R.id.text_view_unfollow);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        progressBar.setVisibility(View.VISIBLE);

        textViewFollow.setOnClickListener(v -> followUser());
        textViewUnfollow.setOnClickListener(v -> unfollowUser());

        return rootView;
    }

    private void followUser() {
        myRef.child("following")
                .child(mAuth.getCurrentUser().getUid())
                .child(userId)
                .child("user_id")
                .setValue(userId);

        myRef.child("followers")
                .child(userId)
                .child(mAuth.getCurrentUser().getUid())
                .child("user_id")
                .setValue(userId);

        sendFollowedNotification();

        textViewFollow.setVisibility(View.INVISIBLE);
        textViewUnfollow.setVisibility(View.VISIBLE);
    }

    private void sendFollowedNotification() {
        NotificationData notificationData = new NotificationData(
                "New Follower!",
                currentUsername + " started following you!"
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

    private void unfollowUser() {
        myRef.child("following")
                .child(mAuth.getCurrentUser().getUid())
                .child(userId)
                .removeValue();

        myRef.child("followers")
                .child(userId)
                .child(mAuth.getCurrentUser().getUid())
                .child("user_id")
                .removeValue();

        textViewFollow.setVisibility(View.VISIBLE);
        textViewUnfollow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void getUserPhotos(DataSnapshot snapshot) {
        ArrayList<Photo> photoArrayList = new ArrayList<>();

        for (DataSnapshot dataSnapshot : snapshot.child("user_photos")
                .child(userId)
                .getChildren()) {

            Photo photo = new Photo();

            /** You can use try/catch block here. Pt. 88 */

            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
            photo.setCaption(objectMap.get("caption").toString());
            photo.setDate_created(objectMap.get("date_created").toString());
            photo.setImage_path(objectMap.get("image_path").toString());
            photo.setPhoto_id(objectMap.get("photo_id").toString());
            photo.setUser_id(objectMap.get("user_id").toString());
            photo.setTags(objectMap.get("tags").toString());

            photoArrayList.add(photo);
        }

        setupGridView(photoArrayList);
    }

    private void setupGridView(ArrayList<Photo> photoArrayList) {
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ProfileGridImagesAdapter adapter = new ProfileGridImagesAdapter(getActivity(), photoArrayList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Photo photoData = photoArrayList.get(position);
            NavDirections navDirections = ViewProfileFragmentDirections
                    .actionViewProfileFragmentToViewPostFragment(photoData);
            navController.navigate(navDirections);
        });
    }

    private void updateUI(UserSettings userSettings) {
        progressBar.setVisibility(View.INVISIBLE);

        textViewPosts.setText("" + userSettings.getSettings().getPosts());
        textViewFollowers.setText("" + userSettings.getSettings().getFollowers());
        textViewFollowing.setText("" + userSettings.getSettings().getFollowing());

        textViewDisplayName.setText(userSettings.getSettings().getDisplay_name());
        textViewDescription.setText(userSettings.getSettings().getDescription());

        Glide.with(getActivity())
                .load(userSettings.getSettings().getProfile_photo())
                .into(imageViewProfilePhoto);
    }

    private UserSettings retrieveData(DataSnapshot snapshot) {

        User user = snapshot
                .child("users") // users node
                .child(userId) // user_id
                .getValue(User.class); // data from that node

        UserAccountSettings userAccountSettings = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(userId) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        receiverFCMToken = userAccountSettings.getFcmToken();

        return new UserSettings(user, userAccountSettings);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");

        /**
         * Listener for value event changes can be registered for
         * myRef.child("followers" & "following) to update
         * followers and following widgets on runtime
         */

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserSettings userSettings = retrieveData(snapshot);
                updateUI(userSettings);
                getPostsCount(snapshot);
                getFollowers(snapshot);
                getFollowing(snapshot);
                getFollowStatus(snapshot);
                getUserPhotos(snapshot);

                getCurrentUsername(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentUsername(DataSnapshot snapshot) {

        User user = snapshot
                .child("users") // users node
                .child(mAuth.getUid()) // user_id
                .getValue(User.class); // data from that node

        currentUsername = user.getUsername();
        Log.d(TAG, "getCurrentUsername: currentUsername: " + currentUsername);
    }

    public void getFollowStatus(DataSnapshot dataSnapshot) {

        long count = dataSnapshot
                .child("following")
                .child(mAuth.getUid())
                .child(userId)
                .getChildrenCount();

        if (count > 0) {
            Log.d(TAG, "getFollowStatus: following");
            textViewFollow.setVisibility(View.INVISIBLE);
            textViewUnfollow.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "instance initializer: not following");
            textViewUnfollow.setVisibility(View.INVISIBLE);
            textViewFollow.setVisibility(View.VISIBLE);
        }
    }

    public void getPostsCount(DataSnapshot dataSnapshot) {

        long posts = dataSnapshot
                .child("user_photos")
                .child(userId)
                .getChildrenCount();

        if (posts > 0) {
            textViewPosts.setText("" + posts);
        } else {
            textViewPosts.setText("" + 0);
        }
    }

    public void getFollowers(DataSnapshot dataSnapshot) {

        long followers = dataSnapshot
                .child("followers")
                .child(userId)
                .getChildrenCount();

        if (followers > 0) {
            textViewFollowers.setText("" + followers);
        } else {
            textViewFollowers.setText("" + 0);
        }
    }

    public void getFollowing(DataSnapshot dataSnapshot) {

        long following = dataSnapshot
                .child("following")
                .child(userId)
                .getChildrenCount();

        if (following > 0) {
            textViewFollowing.setText("" + following);
        } else {
            textViewFollowing.setText("" + 0);
        }
    }
}
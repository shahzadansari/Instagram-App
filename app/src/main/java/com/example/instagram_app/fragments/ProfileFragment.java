package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.model.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView textViewEditProfileBtn, textViewPosts, textViewFollowers, textViewFollowing,
            textViewDisplayName, textViewDescription;
    private ImageView imageViewProfilePhoto;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        rootView.findViewById(R.id.text_view_display_name);
        progressBar = rootView.findViewById(R.id.progress_bar);
        textViewEditProfileBtn = rootView.findViewById(R.id.text_view_edit_profile);
        textViewPosts = rootView.findViewById(R.id.text_view_posts_number);
        textViewFollowers = rootView.findViewById(R.id.text_view_followers_number);
        textViewFollowing = rootView.findViewById(R.id.text_view_following_numbers);
        textViewDisplayName = rootView.findViewById(R.id.text_view_display_name);
        textViewDescription = rootView.findViewById(R.id.text_view_description);
        imageViewProfilePhoto = rootView.findViewById(R.id.profile_image);

        progressBar.setVisibility(View.VISIBLE);
        textViewEditProfileBtn.setOnClickListener(v -> openEditProfileFragment());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserSettings userSettings = retrieveData(snapshot);
                updateUI(userSettings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void openEditProfileFragment() {
        navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
    }


    private void updateUI(UserSettings userSettings) {
        progressBar.setVisibility(View.INVISIBLE);

        textViewPosts.setText("" + userSettings.getSettings().getPosts());
        textViewFollowers.setText("" + userSettings.getSettings().getFollowers());
        textViewFollowing.setText("" + userSettings.getSettings().getFollowing());

        textViewDisplayName.setText(userSettings.getSettings().getDisplay_name());
        textViewDescription.setText(userSettings.getSettings().getDescription());

        Glide.with(this)
                .load(userSettings.getSettings().getProfile_photo())
                .into(imageViewProfilePhoto);
    }

    private UserSettings retrieveData(DataSnapshot snapshot) {
        String id = mAuth.getCurrentUser().getUid();

        User user = snapshot
                .child(getString(R.string.db_node_users)) // users node
                .child(id) // user_id
                .getValue(User.class); // data from that node

        UserAccountSettings userAccountSettings = snapshot
                .child(getString(R.string.db_node_user_account_settings)) // user_account_settings node
                .child(id) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        return new UserSettings(user, userAccountSettings);
    }
}
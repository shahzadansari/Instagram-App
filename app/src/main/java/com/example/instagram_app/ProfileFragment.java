package com.example.instagram_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.instagram_app.models.User;
import com.example.instagram_app.models.UserAccountSettings;
import com.example.instagram_app.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView textViewEditProfileBtn, textViewPosts, textViewFollowers, textViewFollowing;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        rootView.findViewById(R.id.text_view_display_name);
        textViewEditProfileBtn = rootView.findViewById(R.id.text_view_edit_profile);
        textViewPosts = rootView.findViewById(R.id.text_view_posts_number);
        textViewFollowers = rootView.findViewById(R.id.text_view_followers_number);
        textViewFollowing = rootView.findViewById(R.id.text_view_following_numbers);
        progressBar = rootView.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        textViewEditProfileBtn.setOnClickListener(v -> openAccountSettingsFragment());

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

    private void openAccountSettingsFragment() {
        navController.navigate(R.id.action_profileFragment_to_accountSettingsFragment);
    }


    private void updateUI(UserSettings userSettings) {
        progressBar.setVisibility(View.INVISIBLE);
        textViewPosts.setText("" + userSettings.getSettings().getPosts());
        textViewFollowers.setText("" + userSettings.getSettings().getFollowers());
        textViewFollowing.setText("" + userSettings.getSettings().getFollowing());
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
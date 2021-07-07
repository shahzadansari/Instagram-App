package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FirebaseAuth mAuth;
    private TextView textViewMessage;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userId;

    private ArrayList<String> followedUserIds = new ArrayList<>();
    private ArrayList<Photo> mAllPhotos = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userId = mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        textViewMessage = rootView.findViewById(R.id.text_view_message);

        displayStatus();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getFollowedUserIds(snapshot);
                getFollowedUserPhotos(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowedUserPhotos(DataSnapshot snapshot) {

        for (String string : followedUserIds) {

            Log.d(TAG, "getFollowedUserPhotos: user: " + string);

            for (DataSnapshot dataSnapshot : snapshot
                    .child(getString(R.string.db_node_user_photos))
                    .child(string)
                    .getChildren()) {

                Photo photo = new Photo();
                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                photo.setCaption(objectMap.get("caption").toString());
                photo.setDate_created(objectMap.get("date_created").toString());
                photo.setImage_path(objectMap.get("image_path").toString());
                photo.setPhoto_id(objectMap.get("photo_id").toString());
                photo.setUser_id(objectMap.get("user_id").toString());
                photo.setTags(objectMap.get("tags").toString());

                mAllPhotos.add(photo);

                Log.d(TAG, "getFollowedUserPhotos: image url: " + photo.getImage_path());
            }
        }
    }

    private void getFollowedUserIds(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot
                .child(getString(R.string.db_node_following))
                .child(userId)
                .getChildren()) {

            followedUserIds.add((String) dataSnapshot
                    .child(getString(R.string.db_field_user_id))
                    .getValue());

            Log.d(TAG, "getFollowedUserIds: " + dataSnapshot
                    .child(getString(R.string.db_field_user_id))
                    .getValue());
        }
    }

    public void displayStatus() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String message;
        if (firebaseUser != null) {
            message = firebaseUser.getEmail() + " is signed in";
        } else {
            message = "User is not signed in";
        }
        textViewMessage.setText(message);
    }
}
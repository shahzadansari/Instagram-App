package com.example.instagram_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.PhotosAdapter;
import com.example.instagram_app.model.Photo;
import com.google.firebase.auth.FirebaseAuth;
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

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userId;

    private ArrayList<String> followedUserIds = new ArrayList<>();
    private ArrayList<Photo> mAllPhotos = new ArrayList<>();

    private RecyclerView recyclerViewPhotos;
    private PhotosAdapter adapter;
    private Context mContext;
    private NavController navController;
    private TextView textViewNoPosts;
    private ProgressBar progressBar;

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

        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewPhotos = rootView.findViewById(R.id.recycler_view_photos);
        textViewNoPosts = rootView.findViewById(R.id.text_view_no_posts);
        progressBar = rootView.findViewById(R.id.progress_bar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        initEmptyRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        progressBar.setVisibility(View.VISIBLE);

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

    private void initEmptyRecyclerView() {
        adapter = new PhotosAdapter(mContext);
        recyclerViewPhotos.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (mContext, LinearLayoutManager.VERTICAL, false);

        recyclerViewPhotos.setLayoutManager(linearLayoutManager);

        adapter.setOnItemClickListener((photo, v,
                                        authorUsername,
                                        authorProfilePhotoUrl) -> {

            if (v.getId() == R.id.image_view_comments ||
                    v.getId() == R.id.text_view_image_comments_link) {

                NavDirections navDirections = HomeFragmentDirections
                        .actionHomeFragmentToViewCommentsFragment(photo)
                        .setAuthorUsername(authorUsername)
                        .setAuthorProfilePhotoUrl(authorProfilePhotoUrl);
                navController.navigate(navDirections);
            }
        });
    }

    private void getFollowedUserPhotos(DataSnapshot snapshot) {

        for (String string : followedUserIds) {

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
            }
        }

        if (!mAllPhotos.isEmpty()) {
            textViewNoPosts.setVisibility(View.GONE);
        } else {
            textViewNoPosts.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
        adapter.submitList(mAllPhotos);
    }

    private void getFollowedUserIds(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot
                .child(getString(R.string.db_node_following))
                .child(userId)
                .getChildren()) {

            followedUserIds.add((String) dataSnapshot
                    .child(getString(R.string.db_field_user_id))
                    .getValue());
        }
    }
}
package com.example.instagram_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.CommentsAdapter;
import com.example.instagram_app.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";
    private String caption;
    private String authorUsername;
    private String authorProfilePhotoUrl;
    private String photoId;

    private RecyclerView recyclerViewComments;
    private CommentsAdapter adapter;
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public ViewCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caption = ViewCommentsFragmentArgs.fromBundle(getArguments()).getCaption();
        authorUsername = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorUsername();
        authorProfilePhotoUrl = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorProfilePhotoUrl();
        photoId = ViewCommentsFragmentArgs.fromBundle(getArguments()).getPhotoId();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_comments, container, false);
        recyclerViewComments = rootView.findViewById(R.id.recycler_view_comments);

        initEmptyRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getComments(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initEmptyRecyclerView() {
        adapter = new CommentsAdapter(mContext);
        recyclerViewComments.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (mContext, LinearLayoutManager.VERTICAL, false);

        recyclerViewComments.setLayoutManager(linearLayoutManager);
    }

    private void getComments(DataSnapshot snapshot) {

        List<Comment> comments = new ArrayList<>();

        long totalComments = snapshot
                .child("photos")
                .child(photoId)
                .child("comments")
                .getChildrenCount();

        if (totalComments > 0) {
            for (DataSnapshot userIdsSnapshot : snapshot
                    .child("photos")
                    .child(photoId)
                    .child("comments")
                    .getChildren()) {

                Comment comment = new Comment();
                Map<String, Object> objectMap = (HashMap<String, Object>) userIdsSnapshot.getValue();
                comment.setComment(objectMap.get("comment").toString());
                comment.setDate_created(objectMap.get("date_created").toString());
                comment.setUser_id(objectMap.get("user_id").toString());
//                comment.setLikes((List<Like>) objectMap.get("likes"));
                comments.add(comment);

                adapter.submitList(comments);

//                UserAccountSettings userAccountSettings = snapshot
//                        .child("user_account_settings") // users node
//                        .child(user_id) // user_id
//                        .getValue(UserAccountSettings.class); // data from that node
//
//                String username = userAccountSettings.getUsername();
//                String profilePhotoUrl = userAccountSettings.getProfile_photo();
            }
        } else {
            Log.d(TAG, "getComments: No comments");
        }

        for(Comment comment: comments){
            Log.d(TAG, "getComments: " + comment.getComment());
        }
    }
}
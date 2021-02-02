package com.example.instagram_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.CommentsAdapter;
import com.example.instagram_app.model.Comment;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.utils.Utils;
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
    private String authorUsername;
    private String authorProfilePhotoUrl;

    private RecyclerView recyclerViewComments;
    private CommentsAdapter adapter;
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Photo currentPhoto;

    private EditText editTextComment;
    private ImageView imageViewCheck;

    public ViewCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authorUsername = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorUsername();
        authorProfilePhotoUrl = ViewCommentsFragmentArgs.fromBundle(getArguments()).getAuthorProfilePhotoUrl();
        currentPhoto = ViewCommentsFragmentArgs.fromBundle(getArguments()).getPhoto();

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
        imageViewCheck = rootView.findViewById(R.id.image_view_check);
        editTextComment = rootView.findViewById(R.id.edit_text_comment);

        imageViewCheck.setOnClickListener(v -> {
            Utils.hideKeyboard(getActivity());
            addComment(editTextComment.getText().toString());
            editTextComment.setText("");
            editTextComment.clearFocus();
        });

        initEmptyRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getComments(snapshot);
                recyclerViewComments.smoothScrollToPosition(adapter.getItemCount());
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

        comments.add(new Comment(currentPhoto.getCaption(),
                currentPhoto.getDate_created(),
                currentPhoto.getUser_id(),
                currentPhoto.getLikes()));

        adapter.submitList(comments);

        long totalComments = snapshot
                .child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("comments")
                .getChildrenCount();

        if (totalComments > 0) {
            for (DataSnapshot userIdsSnapshot : snapshot
                    .child("photos")
                    .child(currentPhoto.getPhoto_id())
                    .child("comments")
                    .getChildren()) {

                Comment comment = new Comment();
                Map<String, Object> objectMap = (HashMap<String, Object>) userIdsSnapshot.getValue();
                comment.setComment(objectMap.get("comment").toString());
                comment.setDate_created(objectMap.get("date_created").toString());
                comment.setUser_id((String) objectMap.get("user_id"));
                comments.add(comment);

                adapter.submitList(comments);
            }
        } else {
            Log.d(TAG, "getComments: No comments");
        }
    }

    public void addComment(String commentText) {
        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setDate_created(Utils.getTimestamp());
        comment.setUser_id(mAuth.getUid());

        String commentId = myRef.push().getKey();

        myRef.child("photos")
                .child(currentPhoto.getPhoto_id())
                .child("comments")
                .child(commentId)
                .setValue(comment);

        myRef.child("user_photos")
                .child(mAuth.getCurrentUser().getUid())
                .child(currentPhoto.getPhoto_id())
                .child("comments")
                .child(commentId)
                .setValue(comment);
    }
}
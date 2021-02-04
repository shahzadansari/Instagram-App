package com.example.instagram_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram_app.R;
import com.example.instagram_app.adapters.SearchUsersAdapter;
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
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText editText;
    private RecyclerView recyclerViewSearchResults;
    private SearchUsersAdapter adapter;

    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private List<User> usersList = new ArrayList<>();
    private List<UserAccountSettings> userAccountSettingsList = new ArrayList<>();
    private List<UserSettings> usersSettingsList = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewSearchResults = rootView.findViewById(R.id.recycler_view_search_results);
        editText = rootView.findViewById(R.id.edit_text_search);

        initEmptyRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getUsersSettings(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initEmptyRecyclerView() {
        adapter = new SearchUsersAdapter(mContext);
        recyclerViewSearchResults.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (mContext, LinearLayoutManager.VERTICAL, false);

        recyclerViewSearchResults.setLayoutManager(linearLayoutManager);
    }

    private void getUsersSettings(DataSnapshot snapshot) {

        for (DataSnapshot dataSnapshot : snapshot.child("users")
                .getChildren()) {
            User user = dataSnapshot.getValue(User.class);
            usersList.add(user);
        }

        for (DataSnapshot dataSnapshot : snapshot.child("user_account_settings")
                .getChildren()) {
            UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
            userAccountSettingsList.add(userAccountSettings);
        }

        for (int i = 0; i < usersList.size(); i++) {
            UserSettings userSettings = new UserSettings(usersList.get(i), userAccountSettingsList.get(i));
            usersSettingsList.add(userSettings);
        }

        adapter.submitList(usersSettingsList);
    }
}
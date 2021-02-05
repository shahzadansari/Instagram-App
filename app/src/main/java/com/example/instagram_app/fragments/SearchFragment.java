package com.example.instagram_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
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

    public static final int USER_TYPE_PERSONAL = 0;
    public static final int USER_TYPE_VISITOR = 1;
    private static final String TAG = "SearchFragment";
    private TextView textViewEmptyState;
    private EditText editText;
    private RecyclerView recyclerViewSearchResults;
    private SearchUsersAdapter adapter;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private NavController navController;
    private List<User> usersList = new ArrayList<>();
    private List<UserAccountSettings> userAccountSettingsList = new ArrayList<>();
    private List<UserSettings> usersSettingsList = new ArrayList<>();
    private int userCode;

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
        textViewEmptyState = rootView.findViewById(R.id.text_view_empty_state);
        editText = rootView.findViewById(R.id.edit_text_search);

        initEmptyRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getUsersSettings(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<UserSettings> filteredList = new ArrayList<>();
        for (UserSettings userSettings : usersSettingsList) {
            if (userSettings.getUser().getUsername().toLowerCase().contains(text.toLowerCase()) ||
                    userSettings.getSettings().getDisplay_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(userSettings);
            }
        }
        adapter.submitList(filteredList);

        if (filteredList.isEmpty()) {
            textViewEmptyState.setVisibility(View.VISIBLE);
        } else {
            textViewEmptyState.setVisibility(View.INVISIBLE);
        }
    }

    private void initEmptyRecyclerView() {
        adapter = new SearchUsersAdapter(mContext);
        recyclerViewSearchResults.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (mContext, LinearLayoutManager.VERTICAL, false);

        recyclerViewSearchResults.setLayoutManager(linearLayoutManager);

        adapter.setOnItemClickListener(position -> {

            String selectedUserId = usersSettingsList.get(position)
                    .getUser()
                    .getUser_id();

            if (selectedUserId.equals(mAuth.getUid())) {
                navController.navigate(R.id.action_searchFragment_to_profileFragment);
            } else {
                NavDirections navDirections = SearchFragmentDirections
                        .actionSearchFragmentToViewProfileFragment()
                        .setUserId(selectedUserId);
                navController.navigate(navDirections);
            }
        });
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
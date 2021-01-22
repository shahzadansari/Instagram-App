package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.R;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpFragment extends Fragment {

    private Button btnSignUp;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private TextView textViewSignIn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        btnSignUp = rootView.findViewById(R.id.button_sign_up);
        editTextEmail = rootView.findViewById(R.id.edit_text_email);
        editTextUsername = rootView.findViewById(R.id.edit_text_username);
        editTextPassword = rootView.findViewById(R.id.edit_text_password);
        textViewSignIn = rootView.findViewById(R.id.text_view_sign_in_account);

        btnSignUp.setOnClickListener(v -> registerUser());
        textViewSignIn.setOnClickListener(v -> openLoginFragment());

        return rootView;
    }

    private void openLoginFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isUsernameAvailable(username, snapshot)) {
                    createNewUser(email, username, password);
                } else {
                    Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void createNewUser(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {

                        addInitialUserData(email, username);
                        Toast.makeText(getContext(), "Sign up successful\nLogin to continue", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> openLoginFragment(), 2000);

                    } else {
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addInitialUserData(String email, String username) {
        String userId = mAuth.getCurrentUser().getUid();
        User user = new User(userId, 1, email, username);
        UserAccountSettings userAccountSettings = new UserAccountSettings
                ("",
                        "",
                        0,
                        0, 0,
                        "",
                        username);

        myRef.child("users")
                .child(userId)
                .setValue(user);

        myRef.child("user_account_settings")
                .child(userId)
                .setValue(userAccountSettings);
    }

    private boolean isUsernameAvailable(String username, DataSnapshot snapshot) {
        User user = new User();

        for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {
            user.setUsername(dataSnapshot.getValue(User.class).getUsername());

            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }
}
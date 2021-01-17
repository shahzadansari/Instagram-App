package com.example.instagram_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.instagram_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView textViewMessage;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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
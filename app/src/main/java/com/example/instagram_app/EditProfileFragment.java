package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class EditProfileFragment extends Fragment {

    private ImageView imageViewProfilePhoto, imageViewBackArrow, imageViewSaveChanges;
    private TextView textViewChangePhoto, textViewSignOut;
    private EditText editTextUsername, editTextDisplayName, editTextDescription,
            editTextEmail, editTextPhoneNumber;

    private NavController navController;
    private FirebaseAuth mAuth;

    public EditProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        imageViewBackArrow = rootView.findViewById(R.id.image_view_back_arrow);
        imageViewSaveChanges = rootView.findViewById(R.id.image_view_save_changes);
        imageViewProfilePhoto = rootView.findViewById(R.id.image_view_profile_photo);
        textViewChangePhoto = rootView.findViewById(R.id.text_view_change_photo);
        textViewSignOut = rootView.findViewById(R.id.text_view_sign_out);
        editTextUsername = rootView.findViewById(R.id.username_edit_text);
        editTextDisplayName = rootView.findViewById(R.id.edit_text_display_name);
        editTextDescription = rootView.findViewById(R.id.edit_text_description);
        editTextEmail = rootView.findViewById(R.id.email_edit_text);
        editTextPhoneNumber = rootView.findViewById(R.id.edit_text_phone_number);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewBackArrow.setOnClickListener(v -> navController.navigateUp());
        textViewSignOut.setOnClickListener(v -> signOut());
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
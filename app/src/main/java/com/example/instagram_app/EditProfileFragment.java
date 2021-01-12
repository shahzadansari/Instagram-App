package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.instagram_app.models.User;
import com.example.instagram_app.models.UserAccountSettings;
import com.example.instagram_app.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment {

    private ImageView imageViewProfilePhoto, imageViewBackArrow, imageViewSaveChanges;
    private TextView textViewChangePhoto;
    private EditText editTextUsername, editTextDisplayName, editTextDescription,
            editTextEmail, editTextPhoneNumber;
    private RelativeLayout relativeLayoutClickable;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public EditProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        imageViewBackArrow = rootView.findViewById(R.id.image_view_back_arrow);
        imageViewSaveChanges = rootView.findViewById(R.id.image_view_save_changes);
        imageViewProfilePhoto = rootView.findViewById(R.id.image_view_profile_photo);
        textViewChangePhoto = rootView.findViewById(R.id.text_view_change_photo);
        editTextUsername = rootView.findViewById(R.id.username_edit_text);
        editTextDisplayName = rootView.findViewById(R.id.edit_text_display_name);
        editTextDescription = rootView.findViewById(R.id.edit_text_description);
        editTextEmail = rootView.findViewById(R.id.email_edit_text);
        editTextPhoneNumber = rootView.findViewById(R.id.edit_text_phone_number);
        relativeLayoutClickable = rootView.findViewById(R.id.relLayout7);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewBackArrow.setOnClickListener(v -> navController.navigateUp());
        relativeLayoutClickable.setOnClickListener(v -> signOut());

        textViewChangePhoto.setOnClickListener(v -> {
            // TODO: Grab a photo
        });

        imageViewSaveChanges.setOnClickListener(v -> saveChanges());

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
    }

    private void saveChanges() {
        updateUsername();
        updateDescription();
        updateDisplayName();
        updatePhoneNumber();
    }

    private void updatePhoneNumber() {
        long phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString());

        myRef.child(getString(R.string.db_node_users))
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_phone_number))
                .setValue(phoneNumber);
    }

    private void updateDisplayName() {
        String displayName = editTextDisplayName.getText().toString();

        myRef.child(getString(R.string.db_node_user_account_settings))
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_display_name))
                .setValue(displayName);
    }

    private void updateDescription() {
        String description = editTextDescription.getText().toString();

        myRef.child(getString(R.string.db_node_user_account_settings))
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_description))
                .setValue(description);
    }

    private void updateUsername() {
        //TODO: check if username already exists (Use Query Firebase Database) Pt. 36

        String username = editTextUsername.getText().toString();

        myRef.child(getString(R.string.db_node_users))
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_username))
                .setValue(username);

        myRef.child(getString(R.string.db_node_user_account_settings))
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_username))
                .setValue(username);
    }

    private void updateUI(UserSettings userSettings) {

        Glide.with(this)
                .load(userSettings.getSettings().getProfile_photo())
                .into(imageViewProfilePhoto);

        editTextUsername.setText(userSettings.getSettings().getUsername());
        editTextDisplayName.setText(userSettings.getSettings().getDisplay_name());
        editTextDescription.setText(userSettings.getSettings().getDescription());
        editTextEmail.setText(userSettings.getUser().getEmail());
        editTextPhoneNumber.setText("" + userSettings.getUser().getPhone_number());
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

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
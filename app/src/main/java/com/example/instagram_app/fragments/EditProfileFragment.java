package com.example.instagram_app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.instagram_app.MainActivity;
import com.example.instagram_app.R;
import com.example.instagram_app.dialogs.ConfirmPasswordDialog;
import com.example.instagram_app.model.User;
import com.example.instagram_app.model.UserAccountSettings;
import com.example.instagram_app.model.UserSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener {

    public static final int REQUEST_CODE = 1;
    private ImageView imageViewProfilePhoto, imageViewBackArrow, imageViewSaveChanges;
    private TextView textViewChangePhoto;
    private EditText editTextUsername, editTextDisplayName, editTextDescription,
            editTextEmail, editTextPhoneNumber;
    private RelativeLayout relativeLayoutClickable;
    private NavController navController;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private UserSettings mUserSettings;
    private String newEmail;
    private ProgressDialog progressDialog;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewBackArrow.setOnClickListener(v -> navController.navigateUp());
        relativeLayoutClickable.setOnClickListener(v -> signOut());

        textViewChangePhoto.setOnClickListener(v -> {
            NavDirections navDirections = EditProfileFragmentDirections
                    .actionEditProfileFragmentToShareFragment4().setRequestCode(REQUEST_CODE);
            navController.navigate(navDirections);
        });

        imageViewSaveChanges.setOnClickListener(v -> saveChanges());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserSettings = retrieveData(snapshot);
                updateUI(mUserSettings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveChanges() {
        showProgressDialog();
        updateUsername();
        updateDescription();
        updateDisplayName();
        updatePhoneNumber();
        updateEmail();
    }

    private void updateEmail() {
        newEmail = editTextEmail.getText().toString();

        if (!newEmail.equals(mUserSettings.getUser().getEmail())) {
            initConfirmPasswordDialog();
        }
    }

    private void initConfirmPasswordDialog() {
        ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
        dialog.show(getFragmentManager(), "EditProfileFragment");
        dialog.setTargetFragment(EditProfileFragment.this, 1);
    }

    @Override
    public void onConfirmPassword(String password) {
        reAuthenticateUser(password);
    }

    private void reAuthenticateUser(String password) {
        String currentEmail = mAuth.getCurrentUser().getEmail();

        AuthCredential credential = EmailAuthProvider
                .getCredential(currentEmail, password);

        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkIfEmailIsAvailable();
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed!\nTry again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfEmailIsAvailable() {
        mAuth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(task -> {
            if (task.getResult().getSignInMethods().size() == 0) {
                updateEmailAddress();
            } else {
                Toast.makeText(getActivity(), "Email address is not available", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }

    private void updateEmailAddress() {
        mAuth.getCurrentUser().updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        myRef.child("users")
                                .child(mAuth.getUid())
                                .child(getString(R.string.db_field_email))
                                .setValue(newEmail)
                                .addOnCompleteListener(task1 -> {
                                    hideProgressDialog();
                                    Toast.makeText(getActivity(), "Email address updated", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void updatePhoneNumber() {
        long phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString());

        myRef.child("users")
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_phone_number))
                .setValue(phoneNumber)
                .addOnCompleteListener(task -> hideProgressDialog());
    }

    private void updateDisplayName() {
        String displayName = editTextDisplayName.getText().toString();

        myRef.child("user_account_settings")
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_display_name))
                .setValue(displayName)
                .addOnCompleteListener(task -> hideProgressDialog());
    }

    private void updateDescription() {
        String description = editTextDescription.getText().toString();

        myRef.child("user_account_settings")
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_description))
                .setValue(description)
                .addOnCompleteListener(task -> hideProgressDialog());
    }

    private void updateUsername() {
        //TODO: check if username already exists (Use Query Firebase Database) Pt. 36

        String username = editTextUsername.getText().toString();

        myRef.child("users")
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_username))
                .setValue(username);

        myRef.child("user_account_settings")
                .child(mAuth.getUid())
                .child(getString(R.string.db_field_username))
                .setValue(username)
                .addOnCompleteListener(task -> hideProgressDialog());
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
                .child("users") // users node
                .child(id) // user_id
                .getValue(User.class); // data from that node

        UserAccountSettings userAccountSettings = snapshot
                .child("user_account_settings") // user_account_settings node
                .child(id) // user_id
                .getValue(UserAccountSettings.class); // data from that node

        return new UserSettings(user, userAccountSettings);
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Uploading..");
        progressDialog.setTitle("Please wait..");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
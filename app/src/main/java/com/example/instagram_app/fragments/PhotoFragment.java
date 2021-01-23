package com.example.instagram_app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.instagram_app.R;
import com.example.instagram_app.utils.FilePaths;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PhotoFragment extends Fragment {

    private static final String TAG = "PhotoFragment";
    private static final int REQUEST_CODE_PROFILE_PHOTO = 1;
    private static final int CAMERA_REQUEST_CODE = 6;

    private Button buttonOpenCamera;
    private String imagePath;
    private NavController navController;
    private int requestCode;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public PhotoFragment(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            Log.d(TAG, "onCreate: photo fragment called for profile photo");
        } else {
            Log.d(TAG, "onCreate: photo fragment called to share post");
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        buttonOpenCamera = rootView.findViewById(R.id.button_open_camera);

        buttonOpenCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imagePath = Utils.savingToStorage(bitmap);

            if (this.requestCode == REQUEST_CODE_PROFILE_PHOTO) {
                updateProfilePhoto(bitmap);
            } else {
                NavDirections navDirections = ShareFragmentDirections
                        .actionShareFragmentToEditPhotoFragment(imagePath);
                navController.navigate(navDirections);
            }
        }
    }

    private void updateProfilePhoto(Bitmap bitmap) {
        FilePaths filePaths = new FilePaths();
        String user_id = mAuth.getCurrentUser().getUid();

        /** e.g, photos/users/user_id/profile_photo */
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

        byte[] bytes = Utils.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                myRef.child("user_account_settings")
                        .child(user_id)
                        .child(getString(R.string.db_field_profile_photo))
                        .setValue(uri.toString())
                        .addOnSuccessListener(aVoid -> {
                            navController.popBackStack();
//                            navController.navigate(R.id.action_shareFragment_to_homeFragment);
                        });
            });
        }).addOnFailureListener(exception -> {
            Log.d(TAG, "uploadNewPhoto: Upload failed");
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
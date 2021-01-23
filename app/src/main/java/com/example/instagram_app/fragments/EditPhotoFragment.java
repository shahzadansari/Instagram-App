package com.example.instagram_app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.model.Photo;
import com.example.instagram_app.utils.FilePaths;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditPhotoFragment extends Fragment {

    private static final String TAG = "EditPhotoFragment";
    private String imagePath;
    private ImageView imageView, imageViewBackArrow;
    private TextView textViewShare;
    private EditText editTextCaption;
    private NavController navController;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private int imageCount;

    public EditPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imagePath = EditPhotoFragmentArgs.fromBundle(getArguments()).getImagePath();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageCount = getImageCount(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView = view.findViewById(R.id.image_view);
        editTextCaption = view.findViewById(R.id.edit_text_caption);
        imageViewBackArrow = view.findViewById(R.id.image_view_back_arrow);
        textViewShare = view.findViewById(R.id.text_view_share);

        imageViewBackArrow.setOnClickListener(v -> navController.navigateUp());

        Glide.with(getActivity())
                .load(imagePath)
                .into(imageView);

        textViewShare.setOnClickListener(v -> {
            String caption = editTextCaption.getText().toString();
            uploadNewPhoto(caption,
                    imageCount,
                    imagePath);
        });
    }

    private void uploadNewPhoto(String caption, int imageCount, String imageUrl) {
        FilePaths filePaths = new FilePaths();
        String user_id = mAuth.getCurrentUser().getUid();

        /** e.g, photos/users/user_id/photo1 */
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (imageCount + 1));

        Bitmap bitmap = Utils.createBitmap(imageUrl);
        byte[] bytes = Utils.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "uploadNewPhoto: Upload successful");

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "uploadNewPhoto: imageUrl: " + uri);

                String newPhotoKey = myRef.child("user_photos")
                        .push()
                        .getKey();

                Photo photo = new Photo();
                photo.setCaption(caption);
                photo.setDate_created(Utils.getTimestamp());
                photo.setImage_path(uri.toString());
                photo.setPhoto_id(newPhotoKey);
                photo.setUser_id(user_id);
                photo.setTags(Utils.getTags(caption));

                /** insert into user_photos */
                myRef.child("user_photos")
                        .child(user_id)
                        .child(newPhotoKey)
                        .setValue(photo);

                /** insert into (all) photos */
                myRef.child("photos")
                        .child(newPhotoKey)
                        .setValue(photo).addOnCompleteListener(task -> {
                    navController.popBackStack(R.id.homeFragment, true);
                });

            });
        }).addOnFailureListener(exception -> {
            Log.d(TAG, "uploadNewPhoto: Upload failed");
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");
        });
    }

    private int getImageCount(DataSnapshot snapshot) {
        int count = 0;

        String userId = mAuth.getCurrentUser().getUid();
        for (DataSnapshot dataSnapshot : snapshot
                .child("user_photos")
                .child(userId)
                .getChildren()) {

            count++;
        }
        return count;
    }
}
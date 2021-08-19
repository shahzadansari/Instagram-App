package com.example.instagram_app.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.instagram_app.R;
import com.example.instagram_app.adapters.GridImageAdapter;
import com.example.instagram_app.ui.fragments.ShareFragmentDirections;
import com.example.instagram_app.utils.FilePaths;
import com.example.instagram_app.utils.FileSearch;
import com.example.instagram_app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int REQUEST_CODE_PROFILE_PHOTO = 1;
    private ImageView imageViewClose;
    private TextView textViewNext;
    private Spinner spinner;
    private GridView gridView;
    private ImageView imageViewSelected;
    private ArrayList<String> directories;
    private NavController navController;

    private String selectedImagePath;
    private int requestCode;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    private NavDirections navDirections;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public GalleryFragment(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            Log.d(TAG, "onCreate: gallery called for profile photo");
        } else {
            Log.d(TAG, "onCreate: gallery called to share post");
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
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        imageViewClose = rootView.findViewById(R.id.image_view_close);
        textViewNext = rootView.findViewById(R.id.text_view_next);
        spinner = rootView.findViewById(R.id.spinnerDirectory);
        gridView = rootView.findViewById(R.id.gridView);
        imageViewSelected = rootView.findViewById(R.id.image_view_selected);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
            textViewNext.setTextSize(16);
            textViewNext.setText("Update Profile Photo");
        } else {
            textViewNext.setTextSize(20);
            textViewNext.setText("Next");
        }

        initSpinner();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewClose.setOnClickListener(v -> navController.navigate(R.id.action_shareFragment_to_homeFragment));

        textViewNext.setOnClickListener(v -> {

            if (requestCode == REQUEST_CODE_PROFILE_PHOTO) {
                updateProfilePhoto(selectedImagePath);
            } else {
                navDirections = ShareFragmentDirections
                        .actionShareFragmentToEditPhotoFragment(selectedImagePath);
                navController.navigate(navDirections);
            }
        });
    }

    public void initSpinner() {
        FilePaths filePaths = new FilePaths();

        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        directories.add(filePaths.CAMERA);
        directories.add(filePaths.WHATSAPP_IMAGES);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (String directoryName : directories) {
            int index = directoryName.lastIndexOf("/");
            String updatedDirectoryName = directoryName.substring(index + 1);
            directoryNames.add(updatedDirectoryName);
        }

        /*********** To remove .thumbnails/.Gallery2 directories ***********/
        directoryNames.remove(0);
        directories.remove(0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageViewSelected.setImageDrawable(null);
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setupGridView(String selectedDirectory) {

        ArrayList<String> imageUrls = FileSearch.getFilePaths(selectedDirectory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), imageUrls);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> setImage(imageUrls.get(position)));

        if (!imageUrls.isEmpty()) {
            setImage(imageUrls.get(0));
        }

        if (imageUrls.isEmpty()) {
            Toast.makeText(getActivity(), "No images found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImage(String imagePath) {
        Glide.with(getActivity())
                .load(imagePath)
                .into(imageViewSelected);

        selectedImagePath = imagePath;
        Log.d(TAG, "setImage: selectedImagePath: " + selectedImagePath);
    }

    private void updateProfilePhoto(String imageUrl) {
        FilePaths filePaths = new FilePaths();
        String user_id = mAuth.getCurrentUser().getUid();

        /** e.g, photos/users/user_id/profile_photo */
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

        Bitmap bitmap = Utils.createBitmap(imageUrl);
        byte[] bytes = Utils.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                myRef.child("user_account_settings")
                        .child(user_id)
                        .child(getString(R.string.db_field_profile_photo))
                        .setValue(uri.toString())
                        .addOnSuccessListener(aVoid -> {
//                            navController.popBackStack();
                            navController.navigate(R.id.action_shareFragment_to_homeFragment);
                        });
            });
        }).addOnFailureListener(exception -> {
            Log.d(TAG, "uploadNewPhoto: Upload failed");
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");
        });
    }
}
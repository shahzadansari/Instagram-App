package com.example.instagram_app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.instagram_app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 1;
    private Button buttonOpenCamera;
    private ImageView imageViewReceivedImage;

    private String imagePath;
    private NavController navController;

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        buttonOpenCamera = rootView.findViewById(R.id.button_open_camera);
        imageViewReceivedImage = rootView.findViewById(R.id.image_view_received);

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
            imagePath = savingToStorage(bitmap);
            imageViewReceivedImage.setImageBitmap(bitmap);

            NavDirections navDirections = ShareFragmentDirections
                    .actionShareFragmentToEditPhotoFragment(imagePath);
            navController.navigate(navDirections);
        }
    }

    // storage/emulated/0/Pictures/Insta Clone
    public String savingToStorage(Bitmap bitmap) {
        String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/Insta Clone";
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();

        String format = new SimpleDateFormat("yyyyMMddHHmmss",
                java.util.Locale.getDefault()).format(new Date());

        File file = new File(dir, format + ".jpeg");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
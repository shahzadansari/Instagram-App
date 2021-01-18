package com.example.instagram_app.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.instagram_app.R;
import com.example.instagram_app.adapters.GridImageAdapter;
import com.example.instagram_app.utils.FilePaths;
import com.example.instagram_app.utils.FileSearch;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private ImageView imageViewClose;
    private TextView textViewNext;
    private Spinner spinner;
    private GridView gridView;
    private ImageView imageViewSelected;
    private ArrayList<String> directories;
    private NavController navController;

    public GalleryFragment() {
        // Required empty public constructor
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

        initSpinner();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        imageViewClose.setOnClickListener(v -> {
            navController.navigateUp();
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

        Log.d(TAG, "initSpinner: directoryNames: " + directoryNames.get(0));
        Log.d(TAG, "initSpinner: directories: " + directories.get(0));

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
    }
}
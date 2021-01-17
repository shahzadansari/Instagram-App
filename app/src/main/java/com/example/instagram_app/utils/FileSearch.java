package com.example.instagram_app.utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();

        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();

        File file = new File(directory);
        File[] listFiles = file.listFiles();

        for (File listfile : listFiles) {
            if (listfile.isFile()) {
                pathArray.add(listfile.getAbsolutePath());
            }
        }
        return pathArray;
    }
}

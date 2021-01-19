package com.example.instagram_app.utils;

import android.os.Environment;

public class FilePaths {

    // storage/emulated/0
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String WHATSAPP_IMAGES = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}

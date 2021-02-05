package com.example.instagram_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagram_app.fragments.LoginFragment;
import com.example.instagram_app.utils.Permissions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            Log.d(TAG, "onCreate: checkPermissionsArray: called");
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            openHostActivity();
        } else {
            openLoginFragment();
        }
    }

    private void openLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    public void openHostActivity() {
        Intent intent = new Intent(this, HostActivity.class);
        startActivity(intent);
        finish();
    }

    public void verifyPermissions(String[] permissions) {

        ActivityCompat.requestPermissions(
                this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    public boolean checkPermissionsArray(String[] permissions) {

        for (String permission : permissions) {
            if (!checkPermissions(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission) {

        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);

        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }
}
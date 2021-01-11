package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: called");
    }

    private void openLoginFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    private void openHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HostFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    public void openHostActivity(){
        Intent intent = new Intent(this, HostActivity.class);
        startActivity(intent);
    }
}
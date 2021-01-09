package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private TextView textViewMessage;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewMessage = findViewById(R.id.text_view_message);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String message;
        if (firebaseUser != null) {
            message = firebaseUser.getEmail() + " is signed in";

            Log.d(TAG, "onCreate: Email: " + firebaseUser.getEmail() + "\n" +
                    "DisplayName: " + firebaseUser.getDisplayName() + "\n" +
                    "isEmailVerified: " + firebaseUser.isEmailVerified());
        } else {
            message = "User is not signed in";
        }
        textViewMessage.setText(message);

        btnLogout = findViewById(R.id.button_log_out);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
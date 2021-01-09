package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private Button btnSignUp;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.button_sign_up);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        textViewSignIn = findViewById(R.id.text_view_sign_in_account);

        btnSignUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
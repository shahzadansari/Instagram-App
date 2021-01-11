package com.example.instagram_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private Button btnLogin;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignUp;
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = rootView.findViewById(R.id.button_login);
        editTextEmail = rootView.findViewById(R.id.edit_text_email);
        editTextPassword = rootView.findViewById(R.id.edit_text_password);
        textViewSignUp = rootView.findViewById(R.id.text_view_sign_up_account);

        btnLogin.setOnClickListener(v -> signInUser());
        textViewSignUp.setOnClickListener(v -> openSignUpFragment());

        return rootView;
    }

    private void openSignUpFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        openHostActivity();
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openHostActivity() {
        Intent intent = new Intent(getActivity(), HostActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
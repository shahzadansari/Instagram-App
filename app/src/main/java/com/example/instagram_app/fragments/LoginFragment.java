package com.example.instagram_app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instagram_app.HostActivity;
import com.example.instagram_app.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private Button btnLogin;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        btnLogin.setOnClickListener(v -> signInUser());
        textViewSignUp.setOnClickListener(v -> openSignUpFragment());

        return rootView;
    }

    private void openSignUpFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    private void signInUser() {
        showProgressDialog();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        hideProgressDialog();
                        openHostActivity();
                    } else {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openHostActivity() {
        Intent intent = new Intent(getActivity(), HostActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Logging User..");
        progressDialog.setTitle("Please wait..");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
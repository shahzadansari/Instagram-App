package com.example.instagram_app.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.instagram_app.R;

public class ConfirmPasswordDialog extends DialogFragment {

    private static final String TAG = "ConfirmPasswordDialog";
    private OnConfirmPasswordListener mOnConfirmPasswordListener;
    private EditText editTextPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_custom, container, false);
        editTextPassword = view.findViewById(R.id.password_edit_text);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnClose = view.findViewById(R.id.btnClose);

        btnConfirm.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString();
            if (!password.equals("")) {
                mOnConfirmPasswordListener.onConfirmPassword(password);
                getDialog().dismiss();
            } else {
                Toast.makeText(getActivity(), "You must enter a password", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> getDialog().dismiss());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    public interface OnConfirmPasswordListener {
        void onConfirmPassword(String password);
    }
}


package com.example.cscb07project.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cscb07project.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {
    /*
    I considered abstracting this class, but then decided not to overcomplicate things.
    This fragment is very different from others, and does not need to
    be able to navigate to any screens other than login, so it does not inherit
    loginview.
     */

    private int buttonColor;

    private EditText email;
    private Button resetButton;
    private Button backToLoginButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.emailEditText);
        resetButton = view.findViewById(R.id.resetPasswordButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        resetButton.setOnClickListener(v -> handleResetClick());
        backToLoginButton.setOnClickListener(v -> navigateToLogin());
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorPrimary, typedValue, true);
        this.buttonColor = typedValue.data;
    }

    private void handleResetClick() {
        String enteredEmail = email.getText().toString().trim();
        if (enteredEmail.isEmpty()) {
            showError("Please enter your email.", false);
            return;
        }

        setLoading(true);
        FirebaseAuth.getInstance().sendPasswordResetEmail(enteredEmail)
                .addOnSuccessListener(something -> {
                    setLoading(false);
                    showError("Password reset email sent. Please check spam.", true);
                    navigateToLogin();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showError(e.getMessage() != null ? e.getMessage() : "Failed to send an email.", false);
                });
    }

    private void showError(String message, boolean isLong) {

        Toast.makeText(getContext(), message, isLong ? Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, LoginFragment.class, null)
                .commit();
    }

    private void setLoading(boolean isLoading) {
        resetButton.setEnabled(!isLoading);
        backToLoginButton.setEnabled(!isLoading);
        resetButton.setBackgroundColor(isLoading? Color.GRAY:this.buttonColor);
    }
}
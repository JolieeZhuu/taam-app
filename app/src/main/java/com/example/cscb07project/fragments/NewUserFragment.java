package com.example.cscb07project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.cscb07project.R;
import com.example.cscb07project.login.LoginView;
import com.example.cscb07project.login.MVPInterface;
import com.example.cscb07project.login.SignUpModel;
import com.example.cscb07project.login.SignUpPresenter;

public class NewUserFragment extends LoginView {
    protected EditText confirmPassword;
    protected EditText usernameField;
    @Override protected MVPInterface.presenter createPresenter() {
        return new SignUpPresenter(this, new SignUpModel());
    }

    @Override protected int getLayoutResId() {return R.layout.fragment_new_user;}
    @Override protected int getEmailId() {return R.id.emailEditText;}
    @Override protected int getPasswordId() {return R.id.passwordEditText;}
    @Override protected int getMainButtonId() {return R.id.signUpButton;}
    @Override protected int getSecondaryButtonId() {return R.id.backToLoginButton;}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // Unique to this fragment, not needed for loginFragment.
        this.confirmPassword = view.findViewById(R.id.confirmEditText);
        this.usernameField = view.findViewById(R.id.usernameTextEdit);

        mainButton.setOnClickListener(v -> {
            // Only check password match here, other formal checks happen in SignUpModel.
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                showError("passwords do not match");
                return;
            }
            p.handleLoginClick(
                    email.getText().toString(),
                    password.getText().toString(),
                    usernameField.getText().toString()
            );
        });
    }

}

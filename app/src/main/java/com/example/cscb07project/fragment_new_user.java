package com.example.cscb07project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.cscb07project.login.LoginView;
import com.example.cscb07project.login.MVPInterface;
import com.example.cscb07project.login.SignUpModel;
import com.example.cscb07project.login.SignUpPresenter;

public class fragment_new_user extends LoginView {
    protected EditText confirmPassword;
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
        this.confirmPassword = view.findViewById(R.id.confirmEditText);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    showError("passwords do not match");
                    return;
                }
                p.handleLoginClick(email.getText().toString(), password.getText().toString());
            }
        });



    }
}

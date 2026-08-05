package com.example.cscb07project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cscb07project.R;
import com.example.cscb07project.login.LoginModel;
import com.example.cscb07project.login.LoginPresenter;
import com.example.cscb07project.login.LoginView;
import com.example.cscb07project.login.MVPInterface;


public class LoginFragment extends LoginView {
    Button forgotPasswordButton;
    @Override protected MVPInterface.presenter createPresenter() {
        return new LoginPresenter(this, new LoginModel());
    }
    @Override protected int getLayoutResId() {return R.layout.fragment_login;}
    @Override protected int getEmailId() {return R.id.emailEditText;}
    @Override protected int getPasswordId() {return R.id.passwordEditText;}
    @Override protected int getMainButtonId() {return R.id.loginButton;}
    @Override protected int getSecondaryButtonId() {return R.id.createAccountButton;}


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // Unique to this fragment, not needed for signUpFragment.
        this.forgotPasswordButton = view.findViewById(R.id.forgotPasswordButton);
        forgotPasswordButton.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, ResetPasswordFragment.class, null)
                    .commit();

        });
    }
}
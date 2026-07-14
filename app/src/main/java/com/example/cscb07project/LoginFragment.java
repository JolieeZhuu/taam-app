package com.example.cscb07project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07project.login.LoginModel;
import com.example.cscb07project.login.LoginPresenter;
import com.example.cscb07project.login.LoginView;
import com.example.cscb07project.login.MVPInterface;


public class LoginFragment extends LoginView {
    @Override protected MVPInterface.presenter createPresenter() {
        return new LoginPresenter(this, new LoginModel());
    }
    @Override protected int getLayoutResId() {return R.layout.fragment_login;}
    @Override protected int getEmailId() {return R.id.emailEditText;}
    @Override protected int getPasswordId() {return R.id.passwordEditText;}
    @Override protected int getMainButtonId() {return R.id.loginButton;}
    @Override protected int getSecondaryButtonId() {return R.id.createAccountButton;}
}
package com.example.cscb07project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07project.R;
import com.example.cscb07project.login.LoginPresenter;
import com.example.cscb07project.login.MVPInterface;


public class LoginFragment extends Fragment implements MVPInterface.view {
    LoginPresenter p;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button createAccountButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.p = new LoginPresenter(this, new com.example.cscb07project.login.LoginModel());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.p = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        this.email = view.findViewById(R.id.emailEditText);
        this.password = view.findViewById(R.id.passwordEditText);
        this.loginButton = view.findViewById(R.id.loginButton);
        this.createAccountButton = view.findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                p.handleLoginClick(email.getText().toString(), password.getText().toString());
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                p.handleSignUpClick();
            }
        });
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment_fake_home.class, null)
                .commit();
    }

    @Override
    public void navigateToAdmin() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment_fake_admin.class, null)
                .commit();

    }

    @Override
    public void navigateToSignUp() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment_new_user.class, null)
                .commit();
    }
}
package com.example.cscb07project.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07project.R;


public abstract class LoginView extends Fragment implements MVPInterface.view{
    protected MVPInterface.presenter p;
    protected EditText email;
    protected EditText password;
    protected Button mainButton;
    protected Button secondaryButton;
    protected abstract MVPInterface.presenter createPresenter();
    protected abstract int getLayoutResId();
    protected abstract int getEmailId();
    protected abstract int getPasswordId();
    protected abstract int getMainButtonId();
    protected abstract int getSecondaryButtonId();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.p = createPresenter();
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
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        this.email = view.findViewById(getEmailId());
        this.password = view.findViewById(getPasswordId());
        this.mainButton = view.findViewById(getMainButtonId());
        this.secondaryButton = view.findViewById(getSecondaryButtonId());

        mainButton.setOnClickListener(view1 -> p.handleLoginClick(email.getText().toString(), password.getText().toString(), ""));

        secondaryButton.setOnClickListener(view2 -> p.handleSignUpClick());
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

    @Override
    public void navigateToLogin() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment_login.class, null)
                .commit();
    }
}

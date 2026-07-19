package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public class SignUpPresenter implements MVPInterface.presenter {
    private MVPInterface.view v;
    private MVPInterface.model m;

    public SignUpPresenter(MVPInterface.view v) {
        this.v = v;
        this.m = new SignUpModel();
    }

    public SignUpPresenter(MVPInterface.view v, MVPInterface.model m) {
        this.v = v;
        this.m = m;
    }

    @Override
    public void handleLoginClick(String email, String password, String username) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            v.showError("fields cannot be empty");
            return;
        }
        m.authenticateUser(email, password, username, new MVPInterface.model.callback() {
            @Override
            public void onSuccess(User user) {
                v.navigateToHome();
            }
            @Override
            public void onError(String message) {
                v.showError(message);
            }
        });
    }

    @Override
    public void handleSignUpClick() {
        v.navigateToLogin();
    }
}
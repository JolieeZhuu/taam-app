package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public class SignUpPresenter implements MVPInterface.presenter {
    /*
    I didn't want to write a new signup presenter class, so
    the same login class was used, however, this leads to
    some confusing method names, mentioned below.
     */
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
        // Defines what the "sign up" button does.
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            v.showError("fields cannot be empty");
            return;
        }
        v.setLoading(true);
        m.authenticateUser(email, password, username, new MVPInterface.model.callback() {
            @Override
            public void onSuccess(User user) {
                v.navigateToHome();
            }
            @Override
            public void onError(String message) {
                v.showError(message);
                v.setLoading(false);
            }
        });
    }

    @Override
    public void handleSignUpClick() {
        // Defines what the "back to log in" button does.
        v.navigateToLogin();
    }
}
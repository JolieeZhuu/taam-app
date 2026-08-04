package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public class SignUpPresenter implements MVPInterface.presenter {
    /*
    Presenter half of the signup. Reuses the shared
    MVPInterface.presenter contract from login rather than defining a
    separate signup only interface, so LoginView can hold a single
    presenter field regardless of which screen it's working for.
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

    /**
     * Defines what the "sign up" button does, validates that no field is
     * empty, shows a loading state, then invokes signup model. On success,
     * navigates to the home screen, on failure, does nothing and shows an error.
     */
    @Override
    public void handleMainButtonClick(String email, String password, String username) {
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

    /**
     * Navigates back to the login screen.
     */
    @Override
    public void handleSecondaryButtonClick() {
        v.navigateToLogin();
    }
}
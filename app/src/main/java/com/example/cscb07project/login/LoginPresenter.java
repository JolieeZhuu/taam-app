package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public class LoginPresenter implements MVPInterface.presenter {
    private MVPInterface.view v;
    private MVPInterface.model m;

    public LoginPresenter(MVPInterface.view v) {
        this.v = v;
        this.m = new LoginModel();
    }

    public LoginPresenter(MVPInterface.view v, MVPInterface.model m) {
        this.v = v;
        this.m = m;
    }

    @Override
    public void handleLoginClick(String email, String password, String username) {
        if (password.isEmpty() || email.isEmpty()) {
            v.showError("fields cannot be empty");
            return;
        }

        m.authenticateUser(email, password, "", new MVPInterface.model.callback() {
            @Override
            public void onSuccess(User user) {
                if (m instanceof MVPInterface.AdminCheckable) {
                    ((MVPInterface.AdminCheckable) m).checkAdmin(user, isAdmin -> {
                        if (isAdmin) v.navigateToAdmin();
                        else v.navigateToHome();
                    });
                } else {
                    v.navigateToHome();
                }
            }

            @Override
            public void onError(String message) {
                v.showError(message);
            }
        });
    }

    @Override
    public void handleSignUpClick() {
        v.navigateToSignUp();
    }
}
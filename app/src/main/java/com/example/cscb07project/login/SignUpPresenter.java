package com.example.cscb07project.login;

public class SignUpPresenter implements MVPInterface.presenter{
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
    public void handleLoginClick(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            v.showError("fields cannot be empty");
            return;
        }

        User user = m.authenticateUser(email, password);
        //TODO: if user is null, invalid email/pw, otherwise, navigate to home.
        if(user != null) v.navigateToHome();
    }

    @Override
    public void handleSignUpClick() {
        v.navigateToLogin();
    }
}

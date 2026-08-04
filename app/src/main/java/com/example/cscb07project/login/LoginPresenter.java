package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public class LoginPresenter implements MVPInterface.presenter {
    /*
    The "in between" class for login view and model. Validates user input,
    controls loading states, and navigates to home/admin/signup using view,
    based on what loginModel gives us.
     */
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

    /**
     * Validates email/pw, then invokes model. On success, routes to
     * admin or home screen depending on AdminCheckable, on failure, does nothing
     * and shows errpr.
     */
    @Override
    public void handleLoginClick(String email, String password, String username) {
        if (password.isEmpty() || email.isEmpty()) {
            v.showError("fields cannot be empty");
            return;
        }
        v.setLoading(true); //block multiple concurrent login attempts, overlapping
        //async callbacks racing to replace our fragment is bad :(
        m.authenticateUser(email, password, "", new MVPInterface.model.callback() {
            @Override
            public void onSuccess(User user) {
                /*
                An artifact of the past: this instanceof check exists because LoginModel did not
                always implement AdminCheckable. Earlier in development the branching logic
                here allowed the app to remain demoable before admin checking was added.
                From pre commit e969688 on prototype/login.
                */
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
                v.setLoading(false);
                v.showError(message);
            }
        });
    }

    @Override
    public void handleSignUpClick() {
        v.navigateToSignUp();
    }
}
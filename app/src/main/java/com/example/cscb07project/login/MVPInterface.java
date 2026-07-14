package com.example.cscb07project.login;

public interface MVPInterface {
    interface view{
        void showError(String message);
        void navigateToHome();
        void navigateToAdmin();
        void navigateToSignUp();
        void navigateToLogin();
    }
    interface model{
        User authenticateUser(String email, String password); //for now a boolean,
        // I don't feel like handling async code atm :(
    }

    interface presenter{
        void handleLoginClick(String email, String password);
        void handleSignUpClick();
    }

}

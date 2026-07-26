package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public interface MVPInterface {
    interface view{
        void showError(String message);
        void navigateToHome();
        void navigateToAdmin();
        void navigateToSignUp();
        void navigateToLogin();
    }

    interface model{
        interface callback {
            void onSuccess(User user);
            void onError(String message);
        }
        void authenticateUser(String email, String password, String username, callback callback);
        /*
            tradeoff: pass nothing into username (not needed for login) in loginmodel so that
            mvpinterface.model can be used for both loginmodel and signupmodel.
            For simplicity reasons, and to reduce code duplicaiton.
         */
    }

    interface AdminCheckable {
        interface AdminCallback { void onResult(boolean isAdmin); }
        void checkAdmin(User user, AdminCallback callback);
    }

    interface presenter{
        void handleLoginClick(String email, String password, String username);
        void handleSignUpClick();
    }
}
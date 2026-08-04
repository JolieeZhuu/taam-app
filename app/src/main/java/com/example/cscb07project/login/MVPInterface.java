package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public interface MVPInterface {
    /*
    defines a shared MVP interface for both login and signup.
    defined for login model/view/presenter and signup counterparts.
     */
    interface view{
        /*
        implemented by login related fragments, prevents needless code duplication between
        login and signup fragments. The presenter calls these methods to navigate between
    screens (fragments) and to update UI/loading state
         */
        void showError(String message);
        void navigateToHome();
        void navigateToAdmin();
        void navigateToSignUp();
        void navigateToLogin();
        void setLoading(boolean isLoading);

    }

    interface model{
        /*
        Handles auth backend (ty Jolie for the fb auth methods)
         */
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
        /*
        Technically optional, but we want to differentiate between admin/user. Used in LoginModel
        only, to handle what home screen users should be directed to.
         */
        interface AdminCallback { void onResult(boolean isAdmin); }
        void checkAdmin(User user, AdminCallback callback);
    }

    interface presenter{
        /*
        Defines what we should do when clicking.
         */
        void handleLoginClick(String email, String password, String username);
        void handleSignUpClick();
    }
}
package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;

public interface MVPInterface {
    /*
    defines a shared MVP interface for both login and signup.
    defined for login model/view/presenter and signup counterparts.
     */
    interface view{
        /*
        Presenter calls these methods to navigate between
        screens (fragments) and to update UI/loading state.
        Note that this is implemented ONCE, then used by
        login and signup fragments.
         */

        /**
         * Displays an error message to the user via toast.
         * @param message the error text to display
         */
        void showError(String message);
        void navigateToHome();
        void navigateToAdmin();
        void navigateToSignUp();
        void navigateToLogin();

        /**
         * Toggles loading state, disabling buttons and updating button color
         * while an async request is in flight.
         * @param isLoading true to enter loading state, false to exit it
         */
        void setLoading(boolean isLoading);

    }

    interface model{
        /*
        Handles auth backend (ty Jolie for the fb auth methods). Note this is used in both signup
        and login.
         */
        interface callback {

            /**
             * Called when authentication succeeds.
             * @param user the authenticated user
             */
            void onSuccess(User user);

            /**
             * Called when authentication fails.
             * @param message a user facing error message
             */
            void onError(String message);
        }

        /**
         * Authenticates a user (login or signup depending on implementation).
         * @param email user's email
         * @param password user's password
         * @param username only used by signUpModel, loginModel ignores this param
         * @param callback with resulting user on success, or an error message on failure
         */
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
        interface AdminCallback {
            //A callback interface for checkAdmin.

            /**
             * Called with result of an admin check.
             * @param isAdmin true if the checked user is an admin
             */
            void onResult(boolean isAdmin);
        }

        /**
         * Checks whether the given user is an admin.
         * @param user the user to check
         * @param callback receives the result. Implementations should default to
         *                 false if the check itself fails
         */
        void checkAdmin(User user, AdminCallback callback);
    }

    interface presenter{
        /*
        Defines what we should do when clicking. Note this is used in both signup and login.
         */

        /**
         * Handles the main button click, login or signup depending on implementation.
         * @param email user's email
         * @param password user's password
         * @param username only used by SignUpPresenter, LoginPresenter ignores this param
         */
        void handleLoginClick(String email, String password, String username);

        /**
         * Handles the secondary button click (go to signup, or go back to login,
         * depending on implementation see child class comments).
         */
        void handleSignUpClick();
    }
}
package com.example.cscb07project.login;

import com.example.cscb07project.entities.User;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07project.R;
public class LoginPresenter implements MVPInterface.presenter {
    private MVPInterface.view v;
    private MVPInterface.model m;



public abstract class LoginView extends Fragment implements MVPInterface.view{
    protected MVPInterface.presenter p;
    protected EditText email;
    protected EditText password;
    protected Button mainButton;
    protected Button secondaryButton;
    protected abstract MVPInterface.presenter createPresenter();
    protected abstract int getLayoutResId();
    protected abstract int getEmailId();
    protected abstract int getPasswordId();
    protected abstract int getMainButtonId();
    protected abstract int getSecondaryButtonId();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.p = createPresenter();
    }

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

    @Override
    public void navigateToLogin() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment_login.class, null)
                .commit();
    }
}

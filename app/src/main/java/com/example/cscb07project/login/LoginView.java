package com.example.cscb07project.login;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cscb07project.R;
import com.example.cscb07project.fragments.HomepageFragment;
import com.example.cscb07project.fragments.LoginFragment;
import com.example.cscb07project.fragments.NewUserFragment;

import android.util.TypedValue;


public abstract class LoginView extends Fragment implements MVPInterface.view{
    /*
    Abstract base class for both Login AND SignUp (hence main/secondary button).
    Fragments supply actual layout and view ids. Class handles click wiring
    common to both screens. Fragments define what each button does in their
    respective presenter classes.
    Prevents yucky duplicated fragment wiring code.
     */
    protected MVPInterface.presenter p;
    protected EditText email;
    protected EditText password;
    protected Button mainButton;
    protected Button secondaryButton;
    protected abstract MVPInterface.presenter createPresenter();

    /**
     * Layout resource for screen (not the same for login and signup).
     */
    protected abstract int getLayoutResId();
    protected abstract int getEmailId();
    protected abstract int getPasswordId();
    protected abstract int getMainButtonId();
    protected abstract int getSecondaryButtonId();
    private int buttonColor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.p = createPresenter();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.p = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // Binds views, wires the two buttons, and color from theme
        // used later in setLoading, to differentiate loading (grey) and normal button color..
        this.email = view.findViewById(getEmailId());
        this.password = view.findViewById(getPasswordId());
        this.mainButton = view.findViewById(getMainButtonId());
        this.secondaryButton = view.findViewById(getSecondaryButtonId());

        mainButton.setOnClickListener(view1 -> p.handleMainButtonClick(email.getText().toString(), password.getText().toString(), ""));

        secondaryButton.setOnClickListener(view2 -> p.handleSecondaryButtonClick());
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        this.buttonColor = typedValue.data;
    }


    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    //Below this are methods to navigate to places we could go to
    @Override
    public void navigateToHome() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, HomepageFragment.newInstance(false), null)
                .commit();
    }

    @Override
    public void navigateToAdmin() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, HomepageFragment.newInstance(true), null)
                .commit();

    }

    @Override
    public void navigateToSignUp() {
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, NewUserFragment.class, null)
                .commit();
    }

    @Override
    public void navigateToLogin() {
        //recall: this is a base class for both login and signup screens
        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, LoginFragment.class, null)
                .commit();
    }
    @Override
    public void setLoading(boolean isLoading){
        //disable all buttons while async request has not come back, used in presenters.
        mainButton.setEnabled(!isLoading);
        secondaryButton.setEnabled(!isLoading);
        mainButton.setBackgroundColor(isLoading? Color.GRAY:this.buttonColor);
    }
}

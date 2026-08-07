package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import org.junit.Before;
import org.junit.Test;
import com.example.cscb07project.login.*;

import static org.mockito.Mockito.*;

public class SignUpPresenterTest {
    // more less same as LoginPresenterTest's structure, SignUpPresenter reuses the shared MVPInterface.presenter contract
    private SignUpPresenter signUpPresenter;
    private MVPInterface.view view;
    private MVPInterface.model model;

    @Before
    public void setUp() {
        this.view = mock(MVPInterface.view.class);
        this.model = mock(MVPInterface.model.class);
        this.signUpPresenter = new SignUpPresenter(view, model);
    }

    @Test
    public void emptyEmail_invokesError(){
        signUpPresenter.handleMainButtonClick("", "pass123", "user1");
        verify(view).showError("fields cannot be empty");
        verify(view, never()).navigateToHome();
        verify(model, never()).authenticateUser(anyString(), anyString(), anyString(), any());
    }

    @Test
    public void emptyPassword_invokesError(){
        signUpPresenter.handleMainButtonClick("test@test.com", "", "user1");
        verify(view).showError("fields cannot be empty");
        verify(view, never()).navigateToHome();
        verify(model, never()).authenticateUser(anyString(), anyString(), anyString(), any());
    }

    @Test
    public void emptyUsername_invokesError(){
        signUpPresenter.handleMainButtonClick("test@test.com", "pass123", "");
        verify(view).showError("fields cannot be empty");
        verify(view, never()).navigateToHome();
        verify(model, never()).authenticateUser(anyString(), anyString(), anyString(), any());
    }

    @Test
    public void setsLoadingTrue_beforeCallingModel(){
        signUpPresenter.handleMainButtonClick("test@test.com", "pass123", "user1");
        verify(view).setLoading(true);
        verify(model).authenticateUser(eq("test@test.com"), eq("pass123"), eq("user1"), any());
    }

    @Test
    public void signUp_success_navigatesToHome(){
        User user = mock(User.class);
        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onSuccess(user);
            return null;
        }).when(model).authenticateUser(anyString(), anyString(), anyString(), any());
        signUpPresenter.handleMainButtonClick("test@test.com", "pass123", "user1");

        verify(view).setLoading(true);
        verify(view).navigateToHome();
        verify(view, never()).showError(anyString());
    }

    @Test
    public void signUp_failure_invokesErrorAndStopsLoading(){
        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onError("Username is already taken.");
            return null;
        }).when(model).authenticateUser(anyString(), anyString(), anyString(), any());
        signUpPresenter.handleMainButtonClick("test@test.com", "pass123", "user1");
        verify(view).showError("Username is already taken.");
        verify(view).setLoading(false);
        verify(view, never()).navigateToHome();
    }

    @Test
    public void handleSecondaryButtonClick_navigatesToLogin(){
        signUpPresenter.handleSecondaryButtonClick();
        verify(view).navigateToLogin();
    }
}
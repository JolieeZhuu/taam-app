package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import org.junit.Before;
import org.junit.Test;
import com.example.cscb07project.login.*;

import static org.mockito.Mockito.*;

public class LoginPresenterTest {
    private LoginPresenter loginPresenter;
    private MVPInterface.view view;
    private MVPInterface.model model;

    private interface AdminModel extends MVPInterface.model, MVPInterface.AdminCheckable {} //tests only,
    // so that "m instanceof MVPInterface.AdminCheckable" is true.  perchance

    @Before
    public void setUp() {
        this.view = mock(MVPInterface.view.class);
        this.model = mock(MVPInterface.model.class);
        this.loginPresenter = new LoginPresenter(view, model);
    }

    @Test
    public void emptyEmail_invokesError(){
        loginPresenter.handleLoginClick("", "123", "");
        verify(view).showError("fields cannot be empty");
        verify(view, never()).navigateToAdmin();
        verify(view, never()).navigateToHome();
    }

    @Test
    public void emptyPassword_invokesError(){
        loginPresenter.handleLoginClick("123", "", "");
        verify(view).showError("fields cannot be empty");
        verify(view, never()).navigateToAdmin();
        verify(view, never()).navigateToHome();
    }

    @Test
    public void correctUser_navigate_to_home(){
        User user = mock(User.class);
        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onSuccess(user);
            return null;
        }).when(model).authenticateUser(anyString(), anyString(), anyString(), any());

        loginPresenter.handleLoginClick("test@test.com", "pass123", "");

        verify(view, never()).navigateToAdmin();
        verify(view).navigateToHome();
        verify(view, never()).showError(anyString());
    }

    @Test
    public void incorrectUser_invokesError(){
        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onError("Incorrect username or password");
            return null;
        }).when(model).authenticateUser(anyString(), anyString(), anyString(), any());

        loginPresenter.handleLoginClick("test@test.com", "pass123", "");

        verify(view).showError("Incorrect username or password");
        verify(view, never()).navigateToHome();
        verify(view, never()).navigateToAdmin();
    }

    @Test
    public void adminUser_success_navigatesToAdmin(){
        AdminModel adminModel = mock(AdminModel.class);
        LoginPresenter adminPresenter = new LoginPresenter(view, adminModel);
        User user = mock(User.class);

        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onSuccess(user);
            return null;
        }).when(adminModel).authenticateUser(anyString(), anyString(), anyString(), any());

        doAnswer(invocation -> {
            MVPInterface.AdminCheckable.AdminCallback callback = invocation.getArgument(1);
            callback.onResult(true);
            return null;
        }).when(adminModel).checkAdmin(eq(user), any());

        adminPresenter.handleLoginClick("admin@test.com", "pass123", "");

        verify(view).navigateToAdmin();
        verify(view, never()).navigateToHome();
    }

    @Test
    public void nonAdminUser_success_navigatesToHome(){
        AdminModel adminModel = mock(AdminModel.class);
        LoginPresenter adminPresenter = new LoginPresenter(view, adminModel);
        User user = mock(User.class);

        doAnswer(invocation -> {
            MVPInterface.model.callback callback = invocation.getArgument(3);
            callback.onSuccess(user);
            return null;
        }).when(adminModel).authenticateUser(anyString(), anyString(), anyString(), any());

        doAnswer(invocation -> {
            MVPInterface.AdminCheckable.AdminCallback callback = invocation.getArgument(1);
            callback.onResult(false);
            return null;
        }).when(adminModel).checkAdmin(eq(user), any());

        adminPresenter.handleLoginClick("test@test.com", "pass123", "");

        verify(view).navigateToHome();
        verify(view, never()).navigateToAdmin();
    }

    @Test
    public void handleSignUpClick_navigatesToSignUp(){
        //my favourite test :D
        loginPresenter.handleSignUpClick();
        verify(view).navigateToSignUp();
    }
}
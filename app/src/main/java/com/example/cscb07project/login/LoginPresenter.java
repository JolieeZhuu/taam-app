package com.example.cscb07project.login;

public class LoginPresenter implements MVPInterface.presenter{
    private MVPInterface.view view;
    private MVPInterface.model model;
    public LoginPresenter(MVPInterface.view view){
        this.view = view;
        this.model = new LoginModel();
    }
    @Override
    public void handleLoginClick(String email, String password) {
        if(password.isEmpty() || email.isEmpty()){
            view.showError(("fields cannot be empty"));
            return;
        }
        User user = model.authenticateUser(email,password);
        if(user == null){
            view.showError("incorrect email or password");
            return;
        }
        else if(user.getIsAdmin()){
            view.navigateToAdmin();
            return;
        }
        view.navigateToHome();
    }

    @Override
    public void handleSignUpClick() {
        view.navigateToSignUp();
    }
}

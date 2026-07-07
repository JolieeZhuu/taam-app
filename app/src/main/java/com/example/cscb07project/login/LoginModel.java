package com.example.cscb07project.login;

public class LoginModel implements MVPInterface.model{

    @Override
    public User authenticateUser(String email, String password) {
        if(email.equals("admin@test.com") && password.equals("123")) return new User(email, password, true);
        else if(email.equals("test@test.com") && password.equals("123")) return new User(email, password, false);
        return null;

    }
}

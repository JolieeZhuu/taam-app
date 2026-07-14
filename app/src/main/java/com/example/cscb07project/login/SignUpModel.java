package com.example.cscb07project.login;

public class SignUpModel implements MVPInterface.model{
    @Override
    public User authenticateUser(String email, String password) {
        // TODO: add account creation logic here!
        // returning null here means "signup failed"
        return new User(email, password, false);
    }
}

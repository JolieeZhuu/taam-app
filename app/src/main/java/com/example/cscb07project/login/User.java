package com.example.cscb07project.login;

public class User {
    private boolean isAdmin;
    private String email;
    private String password;
    public User(String email, String password, boolean isAdmin){
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }
    public boolean getIsAdmin(){
        return this.isAdmin;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }
}

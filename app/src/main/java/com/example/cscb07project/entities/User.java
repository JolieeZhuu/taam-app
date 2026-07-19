package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String email;
    private String username;
    private String password;

    // will add createdAt and updatedAt later

    public User() {}

    public User(String email, String username, String password) {
        this.userId = null;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public User(String userId, String email, String username, String password) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("username", username);
        result.put("password", password);

        return result;
    }

    @Override
    public String toString() {
        return "{ " + userId + ", " + email + ", " + username + ", " + password + " }";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

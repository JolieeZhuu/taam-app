package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String email;
    private String username;
    private String usernameLower;

    public User() {}

    public User(String userId, String username, String email) {
        this.userId = userId;
        this.email = email;
        setUsername(username);
    }

    public User(String userId, String username) {
        this.userId = userId;
        setUsername(username);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("username", username);
        result.put("usernameLower", usernameLower);

        return result;
    }

    @Override
    public String toString() {
        return "{ " + userId + ", " + email + ", " + username + " }";
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
        this.username = username == null? null: username.strip();
        this.usernameLower = username == null? null: username.toLowerCase().strip();
    }
    public String getUsernameLower() {
        return usernameLower;
    }
}

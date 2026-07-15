package com.example.cscb07project.login;
import org.mindrot.jbcrypt.BCrypt;
public class SignUpModel implements MVPInterface.model{
    @Override
    public User authenticateUser(String email, String password) {
        // TODO: add account creation logic here!
        // returning null here means "signup failed"
        String hashedPW = BCrypt.hashpw(password, BCrypt.gensalt());
        return new User(email, hashedPW, false);
    }
}

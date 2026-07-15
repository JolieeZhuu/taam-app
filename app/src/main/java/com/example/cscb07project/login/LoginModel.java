package com.example.cscb07project.login;
import org.mindrot.jbcrypt.BCrypt;
public class LoginModel implements MVPInterface.model{

    @Override
    public User authenticateUser(String email, String password) {
        String hashedPW = BCrypt.hashpw("123", BCrypt.gensalt());
        if(email.equals("admin@test.com") && BCrypt.checkpw(password,hashedPW )){
            return new User(email, hashedPW, true);
        }
        else if(email.equals("test@test.com") && BCrypt.checkpw(password, hashedPW)) {
            return new User(email, hashedPW, false);
        }
        return null;

    }
}

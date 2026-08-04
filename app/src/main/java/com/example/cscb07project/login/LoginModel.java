package com.example.cscb07project.login;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginModel implements MVPInterface.model, MVPInterface.AdminCheckable {
    /*
    Uses UserRepository methods to verify validity of users
    Implements AdminCheckable so the presenter can branch to admin and regular homepage.
     */
    private final UserRepository userRepo;

    public LoginModel(UserRepository userRepo) {
        //A test constructor only, so I can use mocked userRepos.
        this.userRepo = userRepo;
    }

    public LoginModel() {
        //Real production constructor, links to our firebase db.
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        this.userRepo = new UserRepository(db, FirebaseAuth.getInstance());
    }

    /**
     * Signs in an existing user via Firebase Auth.
     * @param email user email
     * @param password user password
     * @param username unused (see MVPInterface comment for why this param is here)
     * @param callback onSuccess receives the signed in User; onError receives a generic
     *                 "Incorrect username or password" message. Kept intentionally vague
     *                 to prevent enumeration attacks.
     */
    @Override
    public void authenticateUser(String email, String password, String username, callback callback) {
        //As mentioned in MVP interface comments, username is not used here, but is kept
        //for cleaner code.
        userRepo.signIn(email, password)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError("Incorrect username or password"));
    }

    /**
     * Checks whether the given user is an admin.
     * @param user the user to check
     * @param callback receives Boolean; defaults to false if the check itself fails
     */
    @Override
    public void checkAdmin(User user, AdminCallback callback) {
        userRepo.isAdmin(user.getUserId())
                .addOnSuccessListener(callback::onResult)
                .addOnFailureListener(e->callback.onResult(false));

    }
}

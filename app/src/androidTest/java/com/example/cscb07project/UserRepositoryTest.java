package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import android.util.Log;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest {

    private UserRepository userRepository;
    private FirebaseDatabase dbRef;
    private FirebaseAuth dbAuth;
    private static String userId;

    @Before
    public void setup() {
        dbRef = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        dbAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(dbRef, dbAuth);
    }

    @Test
    public void test1CreateUser() throws Exception {
        Task<User> task = userRepository.createUser("meow", "meow@gmail.com", "password");
        Tasks.await(task);

        assertEquals("meow", task.getResult().getUsername());
        assertEquals("meow@gmail.com", task.getResult().getEmail());

        userId = task.getResult().getUserId();
    }

    @Test
    public void test2SignIn() throws Exception {
        Task<User> task = userRepository.signIn("meow@gmail.com", "password");
        Tasks.await(task);

        assertEquals("meow", task.getResult().getUsername());
        assertEquals("meow@gmail.com", task.getResult().getEmail());
    }

    @Test
    public void test3UpdateUsername() throws Exception {
        User user = new User(userId, "meow", "meow@gmail.com");
        Task<Void> task = userRepository.updateUsername(user, "woof");
    }

    @Test
    public void test4IsAdmin() throws Exception {
        Task<Boolean> task = userRepository.isAdmin(userId);
        Tasks.await(task);

        Log.d("isAdmin test", String.valueOf(task.getResult()));
        assertFalse(task.getResult());
    }

    @Test
    public void test9DeleteUserById() throws Exception {
        Task<Void> task = userRepository.deleteUser();
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }
}

package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private static String userId;

    @Before
    public void setup() {
        dbRef = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        userRepository = new UserRepository(dbRef);
    }

    @Test
    public void test1AddUser() throws Exception {
        User user = new User(null, "bob@gmail.com", "bobby", "badpassword");
        Task<Void> task = userRepository.addUser(user);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
        userId = user.getUserId();
    }

    @Test
    public void test2GetUserById() throws Exception {
        Task<User> task = userRepository.getUserById(userId);
        Tasks.await(task);

        assertEquals("bob@gmail.com", task.getResult().getEmail());
        assertEquals("bobby", task.getResult().getUsername());
    }

    @Test
    public void test3UpdateUser() throws Exception {
        User user = new User(userId, "newemail@gmail.com", "newname", "badpassword");
        Task<Void> task = userRepository.updateUser(user);
        Tasks.await(task);

        assertTrue(task.isSuccessful());
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
        Task<Void> task = userRepository.deleteUserById(userId);
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }
}

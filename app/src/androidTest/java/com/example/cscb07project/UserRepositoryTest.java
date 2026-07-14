package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

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
    public void test3DeleteUserById() throws Exception {
        Task<Void> task = userRepository.deleteUserById(userId);
        Tasks.await(task);
        assertTrue(task.isSuccessful());
    }
}

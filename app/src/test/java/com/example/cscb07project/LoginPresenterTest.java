package com.example.cscb07project;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Test;
import com.example.cscb07project.login.*;

import static org.mockito.Mockito.*;
public class LoginPresenterTest {
    private UserRepository userRepository;
    private LoginModel loginModel;
    private Task<User> task;
    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        task = mock(Task.class);
        loginModel = new LoginModel(userRepository);

        when(task.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(task);
        when(task.addOnFailureListener(any(OnFailureListener.class))).thenReturn(task);
    }

}

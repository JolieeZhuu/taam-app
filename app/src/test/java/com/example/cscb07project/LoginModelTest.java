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

public class LoginModelTest {

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

    @Test
    public void authenticateUser_success_invokesCallbackOnSuccess() {
        User expectedUser = mock(User.class);
        when(userRepository.signIn("test@test.com", "pass123")).thenReturn(task);

        doAnswer(invocation -> {
            OnSuccessListener<User> listener = invocation.getArgument(0);
            listener.onSuccess(expectedUser);
            return task;
        }).when(task).addOnSuccessListener(any(OnSuccessListener.class));

        MVPInterface.model.callback callback = mock(MVPInterface.model.callback.class);

        loginModel.authenticateUser("test@test.com", "pass123", "", callback);

        verify(userRepository).signIn("test@test.com", "pass123");
        verify(callback).onSuccess(expectedUser);
        verify(callback, never()).onError(anyString());
    }

    @Test
    public void authenticateUser_failure_invokesCallbackOnError() {
        when(userRepository.signIn("bad@test.com", "wrongpass")).thenReturn(task);

        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("auth failed"));
            return task;
        }).when(task).addOnFailureListener(any(OnFailureListener.class));

        MVPInterface.model.callback callback = mock(MVPInterface.model.callback.class);

        loginModel.authenticateUser("bad@test.com", "wrongpass", "", callback);

        verify(callback).onError("Incorrect username or password");
        verify(callback, never()).onSuccess(any());
    }
}
package com.example.cscb07project;

import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Test;
import com.example.cscb07project.login.*;
import com.example.cscb07project.login.MVPInterface;

import static org.mockito.Mockito.*;

public class LoginModelTest {

    private UserRepository userRepository;
    private LoginModel loginModel;
    private Task<User> task;
    private Task<Boolean> adminTask;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        task = mock(Task.class);
        adminTask = mock(Task.class);
        loginModel = new LoginModel(userRepository);

        when(task.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(task);
        when(task.addOnFailureListener(any(OnFailureListener.class))).thenReturn(task);
        when(adminTask.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(adminTask);
        when(adminTask.addOnFailureListener(any(OnFailureListener.class))).thenReturn(adminTask);
    }

    @Test
    public void authenticateUser_success_invokesCallbackOnSuccess() {
        //same general process for everything else, commented unnecessarily for future sanity reasons
        User expectedUser = mock(User.class);
        when(userRepository.signIn("test@test.com", "pass123")).thenReturn(task); //"return task when signin is called with this specifically"

        doAnswer(invocation -> {
            OnSuccessListener<User> listener = invocation.getArgument(0); //set listener to be first argument
            listener.onSuccess(expectedUser);
            return task;
        }).when(task).addOnSuccessListener(any(OnSuccessListener.class)); //"whenever .addSuccessListener is called on task, run that code for now"

        MVPInterface.model.callback callback = mock(MVPInterface.model.callback.class);

        loginModel.authenticateUser("test@test.com", "pass123", "", callback); //returns task :)

        verify(userRepository).signIn("test@test.com", "pass123"); //passed through correctly?
        verify(callback).onSuccess(expectedUser); //was onSuccess called?
        verify(callback, never()).onError(anyString()); // onFailure not called

    }

    @Test
    public void authenticateUser_failure_invokesCallbackOnError() {
        when(userRepository.signIn("test@test.com", "pass123")).thenReturn(task);

        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("auth failed"));
            return task;
        }).when(task).addOnFailureListener(any());

        MVPInterface.model.callback callback = mock(MVPInterface.model.callback.class);

        loginModel.authenticateUser("test@test.com", "pass123", "", callback);

        verify(callback).onError("Incorrect username or password");
        verify(callback, never()).onSuccess(any());
    }

    @Test
    public void authenticateAdmin_success_invokesCallbackOnSuccess(){
        User user = mock(User.class);
        when(user.getUserId()).thenReturn("67");
        when(userRepository.isAdmin("67")).thenReturn(adminTask);
        doAnswer(invocation -> {
            OnSuccessListener<Boolean> listener = invocation.getArgument(0);
            listener.onSuccess(true);
            return adminTask;
        }).when(adminTask).addOnSuccessListener(any());
        MVPInterface.AdminCheckable.AdminCallback callback = mock(MVPInterface.AdminCheckable.AdminCallback.class);
        loginModel.checkAdmin(user, callback);
        verify(callback).onResult(true);
    }
    @Test
    public void authenticateAdmin_failure_invokesCallbackOnError(){
        User user = mock(User.class);
        when(user.getUserId()).thenReturn("67");
        when(userRepository.isAdmin("67")).thenReturn(adminTask);

        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("not an admin"));
            return adminTask;
        }).when(adminTask).addOnFailureListener(any());

        MVPInterface.AdminCheckable.AdminCallback callback = mock(MVPInterface.AdminCheckable.AdminCallback.class);
        loginModel.checkAdmin(user, callback);
        verify(callback).onResult(false);
    }


}
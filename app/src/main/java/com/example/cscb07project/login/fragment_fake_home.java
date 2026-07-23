package com.example.cscb07project.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cscb07project.R;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class fragment_fake_home extends Fragment {

    protected Button logoutButton;
    protected UserRepository userRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/"); //will properly handle later...
        this.userRepo = new UserRepository(db, FirebaseAuth.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fake_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        this.logoutButton = view.findViewById(R.id.logout);

        logoutButton.setOnClickListener(view1 -> {
            userRepo.signOut();
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, fragment_login.class, null)
                    .commit();
        });
    }
}
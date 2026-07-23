package com.example.cscb07project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.login.fragment_login;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FilterState main_filters;
    ArtifactRepository aRep;
    UserRepository uRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        aRep = new ArtifactRepository(db);
        uRep = new UserRepository(db);

        main_filters = new FilterState();
        if (savedInstanceState == null){
            loadFragment(new fragment_login());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public FilterState getMainFS() {
        return main_filters;
    }
    public ArtifactRepository getMainARep() {return aRep; }
    public UserRepository getMainURep() {return uRep; }
}
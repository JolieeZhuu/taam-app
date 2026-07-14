package com.example.cscb07project;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FilterState main_filters;
    UserRepository uRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference("firebaseTest");

        // a test
        myRef.setValue("Hello, Firebase!");

        // a test for adding user
        UserRepository userRepository = new UserRepository(db);
        // userRepository.addUser(new User(null, "bob@gmail.com", "bobby", "badpassword"));

        // a test for adding artifact
        ExpandedViewRepository expandedViewRepository = new ExpandedViewRepository(db);
//        Artifact artifact = new Artifact("whyamidoingthis", "myArt", "there is not really a description", "History", "wood", null, null, null, null, null, null, null, null, null, null);
//        ArtifactRepository artifactRepository = new ArtifactRepository(db, expandedViewRepository);
//        artifactRepository.addArtifact(artifact);

        // a test for adding comment
        expandedViewRepository.addComment("-Ox3IGwmUjhcaP9tmfjU", new Comment("whyamidoingthis", "-Ox3IGwmUjhcaP9tmfjU", "hihi this is a comment test"));

        // a test for getting user
        User newUser = new User();
        String userId = "-Ox3IGwmUjhcaP9tmfjU";
        userRepository.getUserById(userId).addOnSuccessListener(snapshot -> {
            Log.d("firebase", String.valueOf(snapshot.getValue()));
            newUser.setUserId(userId);
            newUser.setEmail(snapshot.child("email").getValue(String.class));
            newUser.setUsername(snapshot.child("username").getValue(String.class));
            newUser.setUsername(snapshot.child("password").getValue(String.class));

            Log.d("user", newUser.getEmail());
        }).addOnFailureListener(e -> {
            Log.e("firebase", "Error getting data", e);
        });

        main_filters = new FilterState();

        if (savedInstanceState == null) {
            loadFragment(new FilterFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public FilterState getMainFilters() {
        return main_filters;
    }
}
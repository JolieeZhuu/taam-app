package com.example.cscb07project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.fragments.ExpandedArtifactFragment;
import com.example.cscb07project.fragments.HomepageFragment;
import com.example.cscb07project.fragments.LoginFragment;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    ArtifactRepository aRep;
    FilterState mainFilters;
    Set<Artifact> selectedArtifacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        aRep = new ArtifactRepository(db);

        mainFilters = new FilterState();
        selectedArtifacts = new HashSet<>();
        //please keep everything after this line when merging

        if (savedInstanceState == null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                UserRepository userRepo = new UserRepository(
                        FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/"),
                        FirebaseAuth.getInstance()
                );
                userRepo.isAdmin(currentUser.getUid())
                        .addOnSuccessListener(isAdmin -> loadFragment(HomepageFragment.newInstance(isAdmin)))
                        .addOnFailureListener(e -> loadFragment(HomepageFragment.newInstance(false)));
                //we dont know if user is admin or not, assume not for safety.
            }
            else loadFragment(new LoginFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public FilterState getMainFS() {
        return mainFilters;
    }
    public FirebaseDatabase getDb() {return db; }
    public ArtifactRepository getMainARep() { return aRep; }
    public void setMainSelection(Set<Artifact> new_selection) {selectedArtifacts = new_selection; }
}
package com.example.cscb07project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.fragments.HomeFragment;
import com.example.cscb07project.fragments.CatalogueFragment;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    ArtifactRepository aRep;
    CollectionRepository cRep;
    UserRepository uRep;
    FilterState mainFilters;
    Set<Artifact> selectedArtifacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        aRep = new ArtifactRepository(db);
        cRep = new CollectionRepository(db);
        uRep = new UserRepository(db, FirebaseAuth.getInstance());

        mainFilters = new FilterState();
        selectedArtifacts = new HashSet<>();

        if (savedInstanceState == null){
            loadFragment(new HomeFragment());
        }
        /*comment out above if and uncomment this to test colelcton

        if (savedInstanceState == null){
            uRep.signIn("bruh123@gmail.com","password123")
                    .addOnSuccessListener(user -> {
                        Toast.makeText(this, user.getUsername(),
                                Toast.LENGTH_SHORT).show();
                        loadFragment(CatalogueFragment.withParameters(8));//catalogue
                        //sepcifically for collections
            });*/

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
    public ArtifactRepository getMainARep() {return aRep; }
    public CollectionRepository getMainCRep() {return cRep; }
    public UserRepository getMainURep() {return uRep; }
    public void setMainSelection(Set<Artifact> new_selection) {selectedArtifacts = new_selection; }
}
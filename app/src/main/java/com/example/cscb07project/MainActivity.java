package com.example.cscb07project;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.fragments.HomepageFragment;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.example.cscb07project.fragments.LoginFragment;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

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
//    private String searchQuery; TODO: REMOVE THIS???

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        aRep = new ArtifactRepository(db);
        cRep = new CollectionRepository(db);
        uRep = new UserRepository(db, FirebaseAuth.getInstance());

//        handleMainIntent(); TODO: Remove if it works.

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

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

//    private void handleMainIntent(){ TODO: Remove if it works.
//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            if (query == null) {
//                return;
//            }
//            searchQuery = query.toLowerCase();
//        }
//    }


//    public void getSoughtArtifacts(String query) { TODO: Handle this logic better.
//
//    }

    public FirebaseDatabase getDb() {return db; }
    public ArtifactRepository getMainARep() { return aRep; }
    public CollectionRepository getMainCRep() {return cRep; }
    public UserRepository getMainURep() {return uRep; }
    public FilterState getMainFS() {return mainFilters;}
//    public String getSearchQuery(){ TODO: Remove if it works.
//        String out = searchQuery;
//        searchQuery = null;
//        return out;
//    }
    public void setMainSelection(Set<Artifact> new_selection) {selectedArtifacts = new_selection; }
}
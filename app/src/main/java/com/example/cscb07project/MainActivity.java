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
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private HomepageFragment activeFragment;
    Intent intent;
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

        intent = getIntent();
        handleMainIntent();

        mainFilters = new FilterState();
        selectedArtifacts = new HashSet<>();

        activeFragment = new HomepageFragment();
        if (savedInstanceState == null){
            loadFragment(activeFragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent){ // For our purposes this is called on search queries.
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void handleMainIntent(){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query == null) {
                return;
            }
            query = query.toLowerCase();
            getSoughtArtifacts(query);
        }
    }

    /**
     * @param query, the substring on which we must search.
     * Note that the search is applied in addition to the currently specified FilterState.
     */
    public void getSoughtArtifacts(String query) {
        aRep.getFilteredArtifacts(mainFilters, new BatchArtifactRetriever() {
            @Override
            public void onResult(List<Artifact> artifactList) {
                artifactList.removeIf(artifact -> !(
                        artifact.getName().toLowerCase().contains(query)
                                || artifact.getCategory().toLowerCase().contains(query)
                                || artifact.getMaterial().toLowerCase().contains(query)
                                || artifact.getPeriod().toLowerCase().contains(query)
                ));

                activeFragment.updateArtifactsFromSearch(artifactList);
            }

            @Override
            public void onError(DatabaseError error) {
                getDbErrorToaster();
            }
        });
    }

    public void getDbErrorToaster(){
        Toast.makeText(
                this,
                "Database error, please try again.",
                Toast.LENGTH_SHORT
        ).show();
    }
    public FilterState getMainFS() {
        return mainFilters;
    }
    public FirebaseDatabase getDb() {return db; }
    public ArtifactRepository getMainARep() { return aRep; }
    public void setMainSelection(Set<Artifact> new_selection) {selectedArtifacts = new_selection; }
}
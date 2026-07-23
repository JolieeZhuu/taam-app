package com.example.cscb07project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.fragments.HomepageFragment;
import com.example.cscb07project.systems.FilterState;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FilterState main_filters;
    UserRepository uRep;

    Set<String> selectedArtifacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance(getString(R.string.database_url));
        DatabaseReference myRef = db.getReference("firebaseTest");

        main_filters = new FilterState();

        if (savedInstanceState == null) {
            loadFragment(new HomepageFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack("homepage_fragment");
        transaction.commit();
    }

    public FirebaseDatabase getDB() { return db; }
    public FilterState getFilters() {
        return main_filters;
    }
    public void setFilters(FilterState fs){
        main_filters = fs;
    }
    public Set<String> getSelectedArtifacts(){
        return selectedArtifacts;
    }
    public void setSelectedArtifacts(Set<String> selectionBuffer){
        selectedArtifacts = selectionBuffer;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
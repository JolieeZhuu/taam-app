package com.example.cscb07project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.R;
import com.example.cscb07project.fragments.HomepageFragment;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FilterState main_filters;
    ArtifactRepository aRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        aRep = new ArtifactRepository(db);

//        myRef.setValue("B07 Demo!");
//         myRef.child("movies").setValue("B07 Demo!");

      // this was from artifact form creation
//         if (savedInstanceState == null) {
//             loadFragment(new AddArtifactFragment());
        main_filters = new FilterState();
        if (savedInstanceState == null){
            loadFragment(
                    ExpandedArtifactFragment.newInstance("1")
            );
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
    public FirebaseDatabase getDatabase() {
        return db;
    }
    public ArtifactRepository getMainARep() {return aRep; }
}
package com.example.cscb07project.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.systems.FieldScraper;
import com.example.cscb07project.systems.FilterState;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ArtifactRepository {
    private final DatabaseReference dbRef;
    private final ExpandedViewRepository expandedViewRepository;

    public ArtifactRepository(FirebaseDatabase rootRef) {
        this.dbRef = rootRef.getReference("artifacts");
        this.expandedViewRepository = new ExpandedViewRepository(rootRef);
    }
    public ArtifactRepository(FirebaseDatabase rootRef, ExpandedViewRepository expandedViewRepository) { // expected to use rootRef for expandedView
        this.dbRef = rootRef.getReference("artifacts");
        this.expandedViewRepository = expandedViewRepository;
    }



    /**
     * 4 common CRUD database operations below:
     * Add (create), Get by ID (Read), Update, Delete
     */

    public Task<Void> addArtifact(Artifact artifact) {
        return dbRef.child(artifact.getLotNumber()).setValue(artifact).continueWithTask(snapshot -> {
            if (!snapshot.isSuccessful()) throw Objects.requireNonNull(snapshot.getException());
            return expandedViewRepository.addExpandedView(artifact.getLotNumber(), new ExpandedView(artifact.getLotNumber()));
        });
    } // tested

    public Task<Artifact> getArtifactByLotNumber(String lotNumber) {
        return dbRef.child(lotNumber).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Artifact.class);
            }
            return null;
        });
    } // tested

    public Task<Void> updateArtifact(Artifact artifact) {
        return dbRef.child(artifact.getLotNumber()).updateChildren(artifact.toMap());
    } // tested

    public Task<Void> deleteArtifactByLotNumber(String lotNumber) {
        return dbRef.child(lotNumber).removeValue().continueWithTask(snapshot -> {
            if (!snapshot.isSuccessful()) throw Objects.requireNonNull(snapshot.getException());
            return expandedViewRepository.deleteExpandedViewByLotNumber(lotNumber);
            // since every artifact has an expanded view, that view must be deleted too
        }).addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted artifact with id: " + lotNumber);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting artifact with id: " + lotNumber);
        });
    } // tested

    public void scrapeFieldValues(String key, FieldScraper callback){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> values = new HashSet<>(); // Use hashset to get O(1) Membership checks.

                for (DataSnapshot artifact : snapshot.getChildren()){ // Assuming we pull artifacts
                    String value = artifact.child(key).getValue(String.class);

                    if (value != null){
                        values.add(value);
                    }
                }

                callback.onResult(new ArrayList<>(values)); //CONVERT Hashset to be parseable by adapter.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Firebase only supports "ordering" (for our purposes filtering) by a single key at a time.
     * We will implement a hierarchy then to pull these as follows:
     * 1. Period; 2. Material; 3. Category;
     * The Db will filter by the first specified filter value, then we locally filter the rest.
     */
    public void getFilteredArtifacts(FilterState fs, BatchArtifactRetriever callback){
        Query firstFiltered; // This will be the first filtered layer for the db.
        String firstMatch;
        if (!fs.getFilterValue(FilterState.PERIOD_FILTER_KEY).equals(FilterState.NO_FILTER)){
            firstFiltered = dbRef.orderByChild(FilterState.PERIOD_FILTER_KEY).equalTo(
                    fs.getFilterValue(FilterState.PERIOD_FILTER_KEY));
            firstMatch = FilterState.PERIOD_FILTER_KEY;
        } else if (!fs.getFilterValue(FilterState.MATERIAL_FILTER_KEY).equals(FilterState.NO_FILTER)) {
            firstFiltered = dbRef.orderByChild(FilterState.MATERIAL_FILTER_KEY).equalTo(
                    fs.getFilterValue(FilterState.MATERIAL_FILTER_KEY));
            firstMatch = FilterState.MATERIAL_FILTER_KEY;
        } else if (!fs.getFilterValue(FilterState.CATEGORY_FILTER_KEY).equals(FilterState.NO_FILTER)) {
            firstFiltered = dbRef.orderByChild(FilterState.CATEGORY_FILTER_KEY).equalTo(
                    fs.getFilterValue(FilterState.CATEGORY_FILTER_KEY));
            firstMatch = FilterState.CATEGORY_FILTER_KEY;
        } else {
            firstFiltered = dbRef;
            firstMatch = FilterState.NO_FILTER;
        }

        firstFiltered.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<Artifact> filtrate = new HashSet<>();
                for (DataSnapshot artifactSnapshot : snapshot.getChildren()){
                    Artifact artifact = artifactSnapshot.getValue(Artifact.class);
                    if (artifact != null){
                        filtrate.add(artifact);
                    }
                }


                switch(firstMatch) { // We use the switch statement to capture the other filtering.
                    case FilterState.CATEGORY_FILTER_KEY:
                    case FilterState.NO_FILTER:
                        break;
                    case FilterState.PERIOD_FILTER_KEY:
                        if (!Objects.equals(fs.getFilterValue(FilterState.MATERIAL_FILTER_KEY), FilterState.NO_FILTER)) {
                            filtrate.removeIf(solute -> !solute.getMaterial().equals(
                                    fs.getFilterValue(FilterState.MATERIAL_FILTER_KEY)));
                        }
                    case FilterState.MATERIAL_FILTER_KEY:
                        if (!Objects.equals(fs.getFilterValue(FilterState.CATEGORY_FILTER_KEY), FilterState.NO_FILTER)){
                            filtrate.removeIf(solute -> !solute.getMaterial().equals(
                                    fs.getFilterValue(FilterState.CATEGORY_FILTER_KEY)));
                        }
                }

                callback.onResult(new ArrayList<>(filtrate));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });
    }
}

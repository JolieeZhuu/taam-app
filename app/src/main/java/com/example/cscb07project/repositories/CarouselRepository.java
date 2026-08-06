package com.example.cscb07project.repositories;

import android.util.Log;

import com.example.cscb07project.entities.Carousel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarouselRepository {

    private final DatabaseReference dbRef;
    public CarouselRepository(FirebaseDatabase rootRef) {
        this.dbRef = rootRef.getReference("carousels");
    }

    public Task<Carousel> getCarouselById(String carouselId) {
        return dbRef.child(carouselId).get().continueWith(snapshot -> {
            if (snapshot.getResult() != null) {
                return snapshot.getResult().getValue(Carousel.class);
            }
            return null;
        });
    }

    public Task<Void> addArtifactToCarousel(String lotNumber, Carousel carousel) {
        Map<String, Boolean> artifacts = carousel.getArtifacts();
        if (artifacts == null) {
            artifacts = new HashMap<>();
        }
        if (artifacts.get(lotNumber) == null) {
            artifacts.put(lotNumber, true);
            carousel.setArtifacts(artifacts);
            return dbRef.child(carousel.getCarouselId()).updateChildren(carousel.toMap());
        }
        return Tasks.forResult(null);
    }

    public Task<Void> deleteCarousel(String carouselId) {
        return dbRef.child(carouselId).removeValue().addOnSuccessListener(snapshot -> {
            Log.d("delete from firebase", "successfully deleted carousel with id: " + carouselId);
        }).addOnFailureListener(e -> {
            Log.e("firebase error", "error from deleting carousel with id: " + carouselId);
        });
    }
}

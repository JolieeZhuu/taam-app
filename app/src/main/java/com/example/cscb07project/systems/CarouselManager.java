package com.example.cscb07project.systems;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Carousel;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.CarouselRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CarouselManager {

    public interface OnArtifactsLoadedListener {
        void onArtifactsLoaded(List<Artifact> carouselArtifacts);
        void onError(Exception e);
    }

    private final Context context; // To read R.integer.carousel_count
    private final ArtifactRepository artifactRepository;
    private final CarouselRepository carouselRepository;
    private final FirebaseDatabase rootRef;

    public CarouselManager(Context context, FirebaseDatabase rootRef) {
        this.context = context;
        this.rootRef = rootRef;
        this.artifactRepository = new ArtifactRepository(rootRef);
        this.carouselRepository = new CarouselRepository(rootRef);
    }

    /**
     * Loads today's daily carousel. Uses pre-existing carousel if one has already been
     * randomized for the day, otherwise generates a new one.
     *
     * @param listener callback for loaded artifacts
     */
    public void loadDailyCarousel(OnArtifactsLoadedListener listener) {
        String todayDateStr = new SimpleDateFormat("yyyyMMdd", Locale.CANADA).format(new Date());
        String dailyCarouselId = "daily_" + todayDateStr;

        cleanupOldCarousels(dailyCarouselId);

        carouselRepository.getCarouselById(dailyCarouselId).addOnSuccessListener(existingCarousel -> {
            // Try to re-use today's already-generated carousel first
            if (existingCarousel != null && existingCarousel.getArtifacts() != null && !existingCarousel.getArtifacts().isEmpty()) {
                Set<String> lotNumbers = existingCarousel.getArtifacts().keySet();
                fetchArtifactDetails(new ArrayList<>(lotNumbers), listener);
            } else {
                generateAndSaveNewDailyCarousel(dailyCarouselId, todayDateStr, listener);
            }
        }).addOnFailureListener(e -> {
            generateAndSaveNewDailyCarousel(dailyCarouselId, todayDateStr, listener);
        });
    }

    /**
     * Deletes previous days' carousels. Only keep today's carousel data to save database storage.
     */
    private void cleanupOldCarousels(String todayCarouselId) {
        DatabaseReference carouselsRef = rootRef.getReference("carousels");

        carouselsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) return;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String carouselId = child.getKey();

                    if (carouselId != null && carouselId.startsWith("daily_") && !carouselId.equals(todayCarouselId)) {
                        carouselRepository.deleteCarousel(carouselId)
                                .addOnSuccessListener(aVoid -> Log.d("CarouselManager", "Cleaned up old carousel: " + carouselId))
                                .addOnFailureListener(e -> Log.e("CarouselManager", "Failed to delete old carousel: " + carouselId, e));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CarouselManager", "Error during carousel cleanup: "  + error.getMessage());
            }
        });
    }

    /**
     * Chooses random artifacts for the carousel based on the current date.
     *
     * @param carouselId unique carousel identifier
     * @param dateSeedStr date string to use as seed
     * @param listener callback for loaded artifacts
     */
    private void generateAndSaveNewDailyCarousel(String carouselId, String dateSeedStr, OnArtifactsLoadedListener listener) {
        int carouselCount = context.getResources().getInteger(R.integer.carousel_count);
        DatabaseReference artifactsRef = rootRef.getReference("artifacts");

        artifactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> allLotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String lotNumber = child.getKey();
                    if (lotNumber != null) {
                        allLotNumbers.add(lotNumber);
                    }
                }

                if (allLotNumbers.isEmpty()) {
                    if (listener != null) {
                        listener.onArtifactsLoaded(new ArrayList<>());
                    }
                    return;
                }

                long seed = Long.parseLong(dateSeedStr);
                Collections.shuffle(allLotNumbers, new Random(seed));
                int count = Math.min(carouselCount, allLotNumbers.size());
                List<String> selectedLotNumbers = new ArrayList<>(allLotNumbers.subList(0, count));

                saveDailyCarouselToFirebase(carouselId, selectedLotNumbers, "Daily Features");
                fetchArtifactDetails(selectedLotNumbers, listener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CarouselManager", "Error loading artifacts: " + error.getMessage());
                if (listener != null) {
                    listener.onError(error.toException());
                }
            }
        });
    }

    /**
     * Fetches Artifact objects corresponding to the given lot numbers.
     *
     * @param lotNumbers artifact lot numbers
     * @param listener callback for loaded artifacts
     */
    private void fetchArtifactDetails(List<String> lotNumbers, OnArtifactsLoadedListener listener) {
        List<Task<Artifact>> tasks = new ArrayList<>();
        for (String lotNumber : lotNumbers) {
            tasks.add(artifactRepository.getArtifactByLotNumber(lotNumber));
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            List<Artifact> dailyArtifacts = new ArrayList<>();
            for (Object obj : results) {
                if (obj instanceof Artifact) {
                    dailyArtifacts.add((Artifact) obj);
                }
            }

            if (listener != null) {
                listener.onArtifactsLoaded(dailyArtifacts);
            }
        }).addOnFailureListener(e -> {
            if (listener != null) listener.onError(e);
        });
    }

    /**
     * Saves new Carousel with artifacts to Firebase.
     *
     * @param carouselId unique carousel identifier
     * @param lotNumbers lot numbers of artifacts in carousel
     * @param title title of carousel
     */
    private void saveDailyCarouselToFirebase(String carouselId, List<String> lotNumbers, String title) {
        Carousel dailyCarousel = new Carousel(title);
        dailyCarousel.setCarouselId(carouselId);

        for (String lotNumber : lotNumbers) {
            carouselRepository.addArtifactToCarousel(lotNumber, dailyCarousel);
        }
    }
}

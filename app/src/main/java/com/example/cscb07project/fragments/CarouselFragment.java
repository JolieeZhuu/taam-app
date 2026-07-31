package com.example.cscb07project.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.R;
import com.example.cscb07project.carousel.CarouselAdapter;
import com.example.cscb07project.carousel.CarouselManager;
import com.example.cscb07project.entities.Artifact;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CarouselFragment extends Fragment {
    public CarouselFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private CarouselAdapter adapter;
    private List<Artifact> artifactList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private boolean isPlaying = true;
    private long timeInterval = 6000L;
    private int currentActiveIndex = 0;

    private ImageView btnPlayPause;
    private LinearLayout indicatorContainer;
    private final List<View> indicatorLines = new ArrayList<>();

    public interface OnCarouselItemClickListener {
        void onItemClicked(Artifact artifact);
    }

    private OnCarouselItemClickListener itemClickListener;

    public void setOnCarouselItemClickListener(OnCarouselItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeInterval = requireContext().getResources().getInteger(R.integer.carousel_time_interval);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carousel, container, false);

        recyclerView = view.findViewById(R.id.carouselRecyclerView);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        indicatorContainer = view.findViewById(R.id.indicatorContainer);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CarouselAdapter(artifactList);
        adapter.setOnItemClickListener(artifact -> {
            if (itemClickListener != null) itemClickListener.onItemClicked(artifact);
        });
        recyclerView.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        setupIndicators();
        updateIndicatorSelection(0);

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && layoutManager != null && adapter != null && adapter.getItemCount() > 0) {
                    currentActiveIndex++;

                    if (currentActiveIndex >= adapter.getItemCount()) {
                        currentActiveIndex = 0;
                    }

                    recyclerView.smoothScrollToPosition(currentActiveIndex);
                    updateIndicatorSelection(currentActiveIndex);
                }

                if (layoutManager != null) {
                    autoScrollHandler.postDelayed(this, timeInterval);
                }
            }
        };

        btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                startAutoScroll();
            } else {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                stopAutoScroll();
            }
        });

        // retrieves same shared instance as MainActivity's rootRef via Singleton connection
        FirebaseDatabase rootRef = FirebaseDatabase.getInstance();
        CarouselManager manager = new CarouselManager(requireContext(), rootRef);

        manager.loadDailyCarousel(new CarouselManager.OnArtifactsLoadedListener() {
            @Override
            public void onArtifactsLoaded(List<Artifact> artifacts) {
                if (!isAdded()) return;

                artifactList.clear();
                artifactList.addAll(artifacts);

                adapter.notifyDataSetChanged();

                setupIndicators();
                updateIndicatorSelection(0);

                if(isPlaying) {
                    startAutoScroll();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(
                        requireContext(),
                        "Failed to load items. Please try again.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        return view;
    }

    // small indicator lines under carousel images
    private void setupIndicators() {
        indicatorContainer.removeAllViews();
        indicatorLines.clear();

        for (int i = 0; i < artifactList.size(); i++) {
            View line = new View(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 8);
            params.setMargins(8, 0, 8, 0);
            line.setLayoutParams(params);
            line.setBackgroundColor(0xFFCCCCCC);

            indicatorLines.add(line);
            indicatorContainer.addView(line);
        }
    }

    private void updateIndicatorSelection(int selectedIndex) {
        for (int i = 0; i < indicatorLines.size(); i++) {
            if (i == selectedIndex) {
                indicatorLines.get(i).setBackgroundColor(0xFF333333);
            } else {
                indicatorLines.get(i).setBackgroundColor(0xFFCCCCCC);
            }
        }
    }

    private void startAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
        autoScrollHandler.postDelayed(autoScrollRunnable, timeInterval);
    }

    private void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPlaying && !artifactList.isEmpty()) {
            startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
        autoScrollHandler.removeCallbacksAndMessages(null);
        if (adapter != null) {
            adapter.clearBitmaps();
            recyclerView.setAdapter(null);
            adapter = null;
        }
        artifactList.clear();
        layoutManager = null;
        indicatorLines.clear();
        if (indicatorContainer != null) indicatorContainer.removeAllViews();
    }
}
package com.example.cscb07project.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Objects;

public class HomepageFragment extends Fragment {

    public HomepageFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("FieldCanBeLocal")
    private ImageView savedArtifactsBtn;
    private ImageView profileBtn;
    @SuppressWarnings("FieldCanBeLocal")
    private Button filterBtn;
    private View carouselOverlayContainer;
    private ImageButton dailyCarouselHighlightsBtn;
    @SuppressWarnings("FieldCanBeLocal")
    private ImageButton addArtifactBtn;
    private CarouselFragment dailyCarouselFragment;
    private boolean isAdmin = false; // user admin status
    private static final String ARG_IS_ADMIN = "is_admin";
    @SuppressWarnings("FieldCanBeLocal")
    private SearchView searchView;
    private MainActivity mainActivity;
    private CatalogueFragment catalogueFragment;

    public static HomepageFragment newInstance(boolean isAdmin){
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) isAdmin = getArguments().getBoolean(ARG_IS_ADMIN, false);

        mainActivity = (MainActivity) requireActivity();
        catalogueFragment = new CatalogueFragment(); // Open in EAV mode.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        savedArtifactsBtn = view.findViewById(R.id.savedArtifactsBtn);
        profileBtn = view.findViewById(R.id.profileBtn);
        filterBtn = view.findViewById(R.id.filterBtn);
        addArtifactBtn = view.findViewById(R.id.addArtifactBtn);
        carouselOverlayContainer = view.findViewById(R.id.carouselOverlayContainer);
        dailyCarouselHighlightsBtn = view.findViewById(R.id.dailyCarouselHighlightsBtn);

        searchView = view.findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                updateDisplayBySearch(s);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isBlank()) {
                    catalogueFragment.populateFromDb();
                    return true;
                }
                return false;
            }
        });

        savedArtifactsBtn.setOnClickListener(v -> {
            // TODO: load collections fragment
        });

        profileBtn.setOnClickListener(v-> showProfileDropdown());

        filterBtn.setOnClickListener(v -> loadFragment(new FilterFragment()));

        getChildFragmentManager().beginTransaction()
                .replace(R.id.homepage_catalogue_container, catalogueFragment)
                .commit();

        if (isAdmin) {
            addArtifactBtn.setVisibility(View.VISIBLE);
            addArtifactBtn.setEnabled(true);

            addArtifactBtn.setOnClickListener(v -> loadFragment(new AddArtifactFragment()));
        } else {
            addArtifactBtn.setVisibility(View.GONE);
            addArtifactBtn.setEnabled(false);
        }

        dailyCarouselHighlightsBtn.setOnClickListener(v -> showDailyCarouselOverlay());
        carouselOverlayContainer.setOnClickListener(v -> hideDailyCarouselOverlay());

        return view;
    }


    private void showProfileDropdown() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), profileBtn);
        popupMenu.getMenuInflater().inflate(R.menu.profile_dropdown_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_logout) {
                logOut();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showDailyCarouselOverlay() {
        if (carouselOverlayContainer.getVisibility() == View.VISIBLE) return;

        if (dailyCarouselFragment == null) {
            dailyCarouselFragment = new CarouselFragment();
            dailyCarouselFragment.setOnCarouselItemClickListener(artifact -> {
                loadFragment(ExpandedArtifactFragment.newInstance(artifact.getLotNumber()));
                hideDailyCarouselOverlay();
            });
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.carouselFragmentContainer, dailyCarouselFragment)
                    .commitNow();
        }

        carouselOverlayContainer.setVisibility(View.VISIBLE);
        dailyCarouselHighlightsBtn.setVisibility(View.GONE);
    }

    private void hideDailyCarouselOverlay() {
        if (carouselOverlayContainer.getVisibility() != View.VISIBLE) return;
        carouselOverlayContainer.setVisibility(View.GONE);
        dailyCarouselHighlightsBtn.setVisibility(View.VISIBLE);

        if (dailyCarouselFragment != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .remove(dailyCarouselFragment)
                    .commitNowAllowingStateLoss();
            dailyCarouselFragment = null;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        getParentFragmentManager().popBackStack();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void updateDisplayBySearch(String query){
        mainActivity.getMainARep().getFilteredArtifacts(
                mainActivity.getMainFS(), new BatchArtifactRetriever() {
            @Override
            public void onResult(List<Artifact> artifactList) {
                artifactList.removeIf(artifact -> !(
                        artifact.getName().toLowerCase().contains(query)
                                || artifact.getCategory().toLowerCase().contains(query)
                                || artifact.getMaterial().toLowerCase().contains(query)
                                || artifact.getPeriod().toLowerCase().contains(query)
                ));

                catalogueFragment.populateFromList(artifactList);
            }
            @Override
            public void onError(DatabaseError error) {
                Toast.makeText(
                        requireContext(),
                        "Database error upon this search.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }
}
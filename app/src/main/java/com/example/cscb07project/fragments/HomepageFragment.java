package com.example.cscb07project.fragments;

import android.app.SearchManager;
import android.content.Context;
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

import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;

import java.util.List;

public class HomepageFragment extends Fragment {

    public HomepageFragment() {
        // Required empty public constructor
    }

    private ImageView savedArtifactsBtn, profileBtn;
    private Button filterBtn;
    private View carouselOverlayContainer;
    private ImageButton dailyCarouselHighlightsBtn, addArtifactBtn;
    private CarouselFragment dailyCarouselFragment;
    private boolean isAdmin = false; // user admin status
    private SearchView searchBar;
    private SearchManager searchManager; // Probably going to be main.
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
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
        searchBar = view.findViewById(R.id.searchBar);
        searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);

        savedArtifactsBtn.setOnClickListener(v -> {
            // TODO: load collections fragment
        });

        profileBtn.setOnClickListener(v-> {
            showProfileDropdown();
        });

        filterBtn.setOnClickListener(v -> {
            loadFragment(new FilterFragment());
        });

        // TODO: load catalogue fragment
        // Fragment catalogueFragment = new CatalogueFragment();
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(
                mainActivity.getComponentName()));
        searchBar.setIconifiedByDefault(false);


        return view;
    }

    public void updateArtifactsFromSearch(List<Artifact> searchList) {
        // catalogueFragment.populateFromList(searchList); TODO: Replace this with actual reference once added.
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fragment catalogueFragment = CatalogueFragment.newInstance(0);

        /* getChildFragmentManager().beginTransaction()
                .replace(R.id.homepageCatalogueContainer, catalogueFragment)
                .commit(); */

        if (isAdmin) {
            addArtifactBtn.setVisibility(View.VISIBLE);
            addArtifactBtn.setEnabled(true);

            addArtifactBtn.setOnClickListener(v -> {
                loadFragment(new AddArtifactFragment());
            });
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

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_logout) {
                    // TODO: logout
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showDailyCarouselOverlay() {
        if (carouselOverlayContainer.getVisibility() == View.VISIBLE) return;

        if (dailyCarouselFragment == null) {
            dailyCarouselFragment = new CarouselFragment();
            dailyCarouselFragment.setOnCarouselItemClickListener(artifact -> {

                // TODO: load expanded view for artifact from carousel
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
}
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

import com.example.cscb07project.R;

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
    private static final String ARG_IS_ADMIN = "is_admin";
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
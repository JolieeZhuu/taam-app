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
    private ImageButton dailyCarouselHighlightsBtn;
    private CarouselFragment dailyCarouselFragment;
    private boolean isAdmin = true; // user admin status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        savedArtifactsBtn = view.findViewById(R.id.savedArtifactsBtn);
        profileBtn = view.findViewById(R.id.profileBtn);
        filterBtn = view.findViewById(R.id.filterBtn);
        carouselOverlayContainer = view.findViewById(R.id.carouselOverlayContainer);
        dailyCarouselHighlightsBtn = view.findViewById(R.id.dailyCarouselHighlightsBtn);

        savedArtifactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loadFragment(new SavedArtifactsFragment());
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileDropdown();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FilterFragment());
            }
        });

        dailyCarouselHighlightsBtn.setOnClickListener(v -> showDailyCarouselOverlay());
        carouselOverlayContainer.setOnClickListener(v -> hideDailyCarouselOverlay());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fragment catalogueFragment = CatalogueFragment.newInstance(0);

        /* getChildFragmentManager().beginTransaction()
                .replace(R.id.homepageCatalogueContainer, catalogueFragment)
                .commit(); */
    }

    private void showProfileDropdown() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), profileBtn);
        popupMenu.getMenuInflater().inflate(R.menu.profile_dropdown_menu, popupMenu.getMenu());

        if (isAdmin) {
            popupMenu.getMenu().findItem(R.id.action_admin_page).setVisible(true);
        } else {
            popupMenu.getMenu().findItem(R.id.action_admin_page).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_logout) {
                    // logout
                    return true;
                } else if (itemId == R.id.action_admin_page) {
                    // load Admin fragment
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
                // go to ExpandedView
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

package com.example.cscb07project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class HomepageFragment extends Fragment {

    public HomepageFragment() {
        // Required empty public constructor
    }

    private ImageView savedArtifactsBtn, profileBtn;
    private boolean isAdmin = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        savedArtifactsBtn = view.findViewById(R.id.savedArtifactsBtn);
        profileBtn = view.findViewById(R.id.profileBtn);

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

        return view;
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
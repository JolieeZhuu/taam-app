package com.example.cscb07project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.R;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button buttonCatalogueViewmode = view.findViewById(R.id.buttonCatalogue);
        buttonCatalogueViewmode.setOnClickListener(v -> loadFragment(new CatalogueFragment()));

        Button buttonCatalogueSelect = view.findViewById(R.id.buttonSelectCatalogue);
        buttonCatalogueSelect.setOnClickListener(v -> loadFragment(new CatalogueFragment().withParameters(1)));
        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

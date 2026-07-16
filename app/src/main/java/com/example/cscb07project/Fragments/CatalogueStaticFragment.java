package com.example.cscb07project.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.Artifact;
import com.example.cscb07project.ArtifactAdapter;
import com.example.cscb07project.R;

import java.util.ArrayList;
import java.util.List;

public class CatalogueStaticFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private List<Artifact> artifactList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        artifactList = new ArrayList<>();
        loadStaticItems();
        artifactAdapter = new ArtifactAdapter(artifactList);
        recyclerView.setAdapter(artifactAdapter);

        return view;
    }

    private void loadStaticItems() {
        // Load static items from strings.xml or hardcoded values
        artifactList.add(new Artifact("Item1", "Static Book 1", "Static Author 1", "Static Genre 1", "Static Description 1"));
        artifactList.add(new Artifact("Item2", "Static Book 2", "Static Author 2", "Static Genre 2", "Static Description 2"));
    }
}

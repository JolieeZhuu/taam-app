package com.example.cscb07project.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.MainActivity;
import com.example.cscb07project.systems.ArtifactAdapter;
import com.example.cscb07project.systems.ExpandedArtifactAdapter;
import com.example.cscb07project.systems.SelectionArtifactAdapter;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.R;
import com.example.cscb07project.systems.BatchArtifactRetriever;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogueFragment extends Fragment {
    private static final String ARG_SELECTION_COUNT = "selectionLimit";
    private int selectionLimit = 0; // 0 triggers view-only mode.
    private static final int PAGINATION_WIDTH = 2;

    private MainActivity mainActivity;
    private List<Artifact> artifactList;
    private Set<Artifact> selectionBuffer;

    @SuppressWarnings("all")
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    @SuppressWarnings("all")
    private Button buttonSelect;
    @SuppressWarnings("all")
    private Button buttonClear;
    @SuppressWarnings("all")
    private Button buttonChangeFilters;

    /**
     * @param selectionLimit Use only if you need to enter selection mode, specifying limit.
     */
    public static CatalogueFragment withParameters(int selectionLimit) {
        CatalogueFragment catalogueFrag = new CatalogueFragment();

        if (selectionLimit < 0){
            throw new IllegalArgumentException("Selection limit cannot be negative.");
        }
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTION_COUNT, selectionLimit);

        catalogueFrag.setArguments(args);
        return catalogueFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            selectionLimit = args.getInt(ARG_SELECTION_COUNT);
        }

        mainActivity = (MainActivity) requireActivity();
        selectionBuffer = new HashSet<>();
        artifactList = new ArrayList<>();
    }

    @Nullable
    @Override
    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), PAGINATION_WIDTH));

        buttonSelect = view.findViewById(R.id.selectButton);
        buttonClear = view.findViewById(R.id.clearButton);
        buttonChangeFilters = view.findViewById(R.id.filterButton);
        buttonChangeFilters.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FilterFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()

        );

        if (selectionLimit != 0){
            buttonSelect.setOnClickListener(v -> {
                if (selectionBuffer.size() <= selectionLimit) {
                    mainActivity.setMainSelection(selectionBuffer);
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Select at most " + selectionLimit + "artifacts.\n" +
                                "You currently have: " + selectionBuffer.size(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            });
            buttonClear.setOnClickListener(v -> {
                selectionBuffer.clear();
                SelectionArtifactAdapter selectionAdapter = (SelectionArtifactAdapter) artifactAdapter;
                selectionAdapter.notifyDataSetChanged();
            });

            artifactAdapter = new SelectionArtifactAdapter(
                    artifactList,
                    selectionBuffer,
                    artifact -> {
                        if (selectionBuffer.contains(artifact)){
                            selectionBuffer.remove(artifact);
                        } else {
                            selectionBuffer.add(artifact);
                        }
                        artifactAdapter.notifyDataSetChanged();
                    }
            );
        } else { // We must hide the button views, it's not enough to just disable them.
            buttonSelect.setVisibility(View.GONE);
            buttonClear.setVisibility(View.GONE);
            artifactAdapter = new ExpandedArtifactAdapter(
                    artifactList,
                    artifact ->{
                        // TODO: WENQING OPEN EXPANDED VIEW HERE.
            });
        }

        recyclerView.setAdapter(artifactAdapter);
        populateFromDb();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecyclerViews(List<Artifact> artifacts){
        artifactList.clear();
        artifactList.addAll(artifacts);
        artifactAdapter.notifyDataSetChanged();
    }

//    public void populateFromSpecified(final List<Artifact> artifactList){ // TODO: Implement? If needed for collections.
//        setRecyclerViews(artifactList);
//    }

    public void populateFromDb() {
        mainActivity.getMainARep().getFilteredArtifacts(mainActivity.getMainFS(), new BatchArtifactRetriever() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResult(List<Artifact> artifactsFromDb) {
                artifactList.clear();
                artifactList.addAll(artifactsFromDb);
                artifactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(DatabaseError error) {
                // TODO: HANDLE DB ERRORS.
            }
        });
    }

}

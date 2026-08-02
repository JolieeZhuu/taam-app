package com.example.cscb07project.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private static final String PREF_PAGINATION_COUNT = "pagination_count";
    private static final String CATALOGUE_PREFS = "catalogue_preferences";
    private int PAGINATION_COUNT;
    private int curPageNo = 0;
    private int selectionLimit = 0;

    private MainActivity mainActivity;
    private List<Artifact> artifactList;
    private List<Artifact> currentPage;
    private Set<Artifact> selectionBuffer;

    @SuppressWarnings("all")
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonNextPage;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonBackPage;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonSelect;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonClear;
    //@SuppressWarnings("FieldCanBeLocal")
    //private Button buttonChangeFilters; should already be in the home screen.
    @SuppressWarnings("FieldCanBeLocal")
    private Spinner spinnerPagination;


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
        currentPage = new ArrayList<>();

        SharedPreferences prefs = mainActivity.getSharedPreferences(
                CATALOGUE_PREFS, Context.MODE_PRIVATE);
        PAGINATION_COUNT = prefs.getInt(PREF_PAGINATION_COUNT, -1);
    }

    @Nullable
    @Override
    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        spinnerPagination = view.findViewById(R.id.spinnerPagination);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.pagination_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPagination.setAdapter(spinnerAdapter);
        spinnerPagination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String paginationValue = adapterView.getItemAtPosition(i).toString();
                if (paginationValue.equals("All")){
                    PAGINATION_COUNT = -1;
                } else {
                    PAGINATION_COUNT = Integer.parseInt(paginationValue);
                }
                setPaginationSharedPref(PAGINATION_COUNT);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        switch (PAGINATION_COUNT){
            case -1:
                spinnerPagination.setSelection(0);
                break;
            case 24:
                spinnerPagination.setSelection(1);
                break;
            case 12:
                spinnerPagination.setSelection(2);
                break;
        }

        buttonNextPage = view.findViewById(R.id.NextButton);
        buttonBackPage = view.findViewById(R.id.BackButton);
        buttonSelect = view.findViewById(R.id.selectButton);
        buttonClear = view.findViewById(R.id.clearButton);

        buttonNextPage.setOnClickListener(v -> {
            if ( PAGINATION_COUNT == -1 ||
                    (curPageNo + 1) * PAGINATION_COUNT >= artifactList.size()){
                Toast.makeText(
                        requireContext(),
                        "Reached end of artifacts",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                curPageNo++;
                setCurrentPage();
            }
        });

        buttonBackPage.setOnClickListener(v -> {
            if (curPageNo > 0) {
                curPageNo--;
                setCurrentPage();
            } else {
                Toast.makeText(
                        requireContext(),
                        "Reached start of artifacts",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

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
                if (!selectionBuffer.isEmpty()) {
                    selectionBuffer.clear();
                    artifactAdapter.notifyDataSetChanged();
                }
            });

            artifactAdapter = new SelectionArtifactAdapter(
                    currentPage,
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
                    currentPage,
                    artifact -> getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,
                                    ExpandedArtifactFragment.newInstance(artifact.getLotNumber()))
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit());
        }

        recyclerView.setAdapter(artifactAdapter);
        populateFromDb();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setCurrentPage() {
        List<Artifact> subList;
        if (PAGINATION_COUNT == -1){
            subList = artifactList;
        } else {
            subList = artifactList.subList(
                curPageNo * PAGINATION_COUNT,
                Math.min((curPageNo + 1) * PAGINATION_COUNT, artifactList.size())
            );
        }

        currentPage.clear();
        currentPage.addAll(subList);
        artifactAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("all")
    public void populateFromList(List<Artifact> artifacts){
        artifactList.clear();
        artifactList.addAll(artifacts);
        setCurrentPage();
    }

    public void setPaginationSharedPref(int newPref){
        SharedPreferences prefs = mainActivity.getSharedPreferences(
                CATALOGUE_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(PREF_PAGINATION_COUNT, newPref).apply();
    }

    public void populateFromDb() {
        mainActivity.getMainARep().getFilteredArtifacts(mainActivity.getMainFS(), new BatchArtifactRetriever() {
            @Override
            public void onResult(List<Artifact> artifactsFromDb) {
                artifactList.clear();
                artifactList.addAll(artifactsFromDb);
                setCurrentPage();
            }

            @Override
            public void onError(DatabaseError error) {
                Toast.makeText(
                        requireContext(),
                        "Failed to fetch artifacts from database, please try again.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

}

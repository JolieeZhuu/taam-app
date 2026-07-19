package com.example.cscb07project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.systems.ArtifactAdapter;
import com.example.cscb07project.MainActivity;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.R;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Set;

public class CatalogueFragment extends Fragment {
    private MainActivity mainActivity;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private FilterState filters;
    private int selectCount;
    private List<Artifact> artifactList;

    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private Button buttonSave;
    private Button buttonClear;
    private Button buttonExit;
    private Set<String> selectionBuffer;


    public CatalogueFragment(){
    }
    public static CatalogueFragment newInstance (int selectionLimit){
        CatalogueFragment catalogueFrag = new CatalogueFragment();

        Bundle args = new Bundle();
        args.putInt("selectionLimit", selectionLimit);

        catalogueFrag.setArguments(args);
        return catalogueFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null){
            selectCount = args.getInt("selectionLimit");
        }
        else{ // Default pagination, selection counts
            selectCount = 0;
        }

        mainActivity = (MainActivity) requireActivity();
        db = mainActivity.getDB();
        filters = mainActivity.getFilters();
        fetchItemsFromDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        artifactAdapter = new ArtifactAdapter(artifactList);
        recyclerView.setAdapter(artifactAdapter);

        buttonSave = view.findViewById(R.id.save_selection);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mainActivity.setSelectedArtifacts(selectionBuffer); }
        });
        buttonClear = view.findViewById(R.id.clear_selection);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { selectionBuffer.clear(); }
        });
        buttonExit = view.findViewById(R.id.exit_screen);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { getParentFragmentManager().popBackStack(); }
        });

        return view;
    }

    public void getAdapterSelections(){
        selectionBuffer = artifactAdapter.getSelectionBuffer();
    }

    /**
     * Fetches items from database according to filterstate. null means no filter in that category.
     * Items will be loaded into artifactList.
     */
    private void fetchItemsFromDatabase() {
    }
}

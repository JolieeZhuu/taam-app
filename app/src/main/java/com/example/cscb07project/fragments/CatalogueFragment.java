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
import com.google.android.gms.tasks.Task;//from e
import com.google.android.gms.tasks.Tasks;//from e
//added these libs so we can wait 4 each artifact to b fetched b4 showing:whenAllSuccess()
//kinda works the same as wait() 09 mention >>>_+

//LOTS OF THE COMMENTS I MADE IS JUST FOR THE PPL READING AND SHOULD BE DELETED/MODIFIED AFTER

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogueFragment extends Fragment {
    private static final String ARG_SELECTION_COUNT = "selectionLimit";
    private int selectionLimit = 0; // 0 triggers view-only mode.
    private static final int PAGINATION_WIDTH = 2;

    //VARIABLES FOR COLLECTION
    private ArrayList<String> specifiedLotNumbers = null;
    private static final String ARG_SPECIFIED_LOT_NUMBERS = "specifiedLotNumbers";
    private static final String ARG_COLLECTION_USER_ID = "collectionUserId";
    private String collectionUserId = null;

    // vars so catalogue can stay multipurpose
    public static final String PURPOSE_COLLECTION = "collection";
    public static final String PURPOSE_UNSAVE = "unsave";
    private static final String ARG_SELECTION_PURPOSE = "selectionPurpose";
    private String selectionPurpose = PURPOSE_COLLECTION; //as of now default will be coll

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

    //this pops back to the previous screen when items are picked (what andy originally had)
    //multipurpose
    // loadFragment(CatalogueFragment.withParameters(<num>, CatalogueFragment.PURPOSE_RETURN));
    // function for collection:
    // loadFragment(CatalogueFragment.withParameters(<num>));

    public static CatalogueFragment withParameters(int selectionLimit) {
        return withParameters(selectionLimit, PURPOSE_COLLECTION);
    } //defaults to collection

    //below is for other purposes, added extra param
    /**
     * @param selectionLimit Use only if you need to enter selection mode, specifying limit.
     */
    public static CatalogueFragment withParameters(int selectionLimit, String purpose) {
        CatalogueFragment catalogueFrag = new CatalogueFragment();

        if (selectionLimit < 0){
            throw new IllegalArgumentException("Selection limit cannot be negative.");
        }
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTION_COUNT, selectionLimit);
        args.putString(ARG_SELECTION_PURPOSE, purpose); //added for purpose of mode
        // why selection purpose? example option: collection, mass view, etc

        catalogueFrag.setArguments(args);
        return catalogueFrag;
    } 

    // called for showing specific list of artifacts so in collections case
    @NonNull
    public static CatalogueFragment withLotNumbers(ArrayList<String> lotNumbers) {
        CatalogueFragment catalogueFrag = new CatalogueFragment();

        Bundle args = new Bundle();
        args.putStringArrayList(ARG_SPECIFIED_LOT_NUMBERS, lotNumbers);
        args.putInt(ARG_SELECTION_COUNT, 0); //view only mode with the # results from da selection
        args.putString(ARG_SELECTION_PURPOSE, PURPOSE_COLLECTION);
        catalogueFrag.setArguments(args);
        return catalogueFrag;
    }

    //func below gonna be needed to share cols
    @NonNull
    public static CatalogueFragment withUserId(String userId) {
        return withUserId(userId, 0, PURPOSE_COLLECTION);
    }

    @NonNull
    public static CatalogueFragment withUserId(String userId, int selectionLimit, String purpose) {
        CatalogueFragment catalogueFrag = new CatalogueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLLECTION_USER_ID, userId);
        args.putInt(ARG_SELECTION_COUNT, selectionLimit);
        args.putString(ARG_SELECTION_PURPOSE, purpose);
        catalogueFrag.setArguments(args);
        return catalogueFrag;
    }// TODO: implement sharing feature by calling this method

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            selectionLimit = args.getInt(ARG_SELECTION_COUNT);
            collectionUserId = args.getString(ARG_COLLECTION_USER_ID);//now can see other ppl cols
            specifiedLotNumbers = args.getStringArrayList(ARG_SPECIFIED_LOT_NUMBERS); //COLLECITON
            selectionPurpose = args.getString(ARG_SELECTION_PURPOSE);
            if (selectionPurpose == null) selectionPurpose = PURPOSE_COLLECTION;
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
        if (PURPOSE_UNSAVE.equals(selectionPurpose)){
            buttonSelect.setText("Unsave");
        }else if(PURPOSE_COLLECTION.equals(selectionPurpose)){
            buttonSelect.setText("Save");
        }

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
                if (!selectionBuffer.isEmpty() && selectionBuffer.size() <= selectionLimit) {
                    mainActivity.setMainSelection(selectionBuffer);
                    if(PURPOSE_COLLECTION.equals(selectionPurpose))saveCollection();
                    else if (PURPOSE_UNSAVE.equals(selectionPurpose))unsaveCollection();
                    else getParentFragmentManager().popBackStack(); // for multipurpose

                } else if (selectionBuffer.isEmpty()) {
                    Toast.makeText(requireContext(),
                            "Please select at least 1 artifact.", Toast.LENGTH_SHORT).show();
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

    // FROMELINa: top function unneeded, could js do below, also need userid for seeing others
    public void populateFromDb() {
        if (collectionUserId != null) { //need the users id
            mainActivity.getMainCRep().getCollection(collectionUserId)
                    .addOnSuccessListener(collection -> {
                if (collection != null && collection.getArtifacts() != null) {
                    loadArtifactsByIds(new ArrayList<>(collection.getArtifacts().keySet()));
                }else {
                    Toast.makeText(getContext(), "User collection is empty",
                            Toast.LENGTH_SHORT).show();
                    artifactList.clear();
                    artifactAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Eror getting user collection",
                        Toast.LENGTH_SHORT).show();
            });
            return;
        }

        // everything from now to next return is for collections
        if (specifiedLotNumbers != null && !specifiedLotNumbers.isEmpty()) {
            loadArtifactsByIds(specifiedLotNumbers);
            return;
        }//by e

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

    //loads the artifacts
    private void loadArtifactsByIds(@NonNull List<String> lotNumbers) {
        List<Task<Artifact>> artifacts = new ArrayList<>();
        for (String lotNumber:lotNumbers)artifacts.add(mainActivity.getMainARep()
                .getArtifactByLotNumber(lotNumber));
        //maybe change getfilteredartifacts() cuz rn it goes thru all artifacts
        // then i can call it for this so all this can be removed
        Tasks.whenAllSuccess(artifacts).addOnSuccessListener(results -> {
            artifactList.clear();
            com.example.cscb07project.systems.FilterState fs = mainActivity.getMainFS();
            String catF = fs.getFilterValue(com.example.cscb07project.systems.FilterState
                    .CATEGORY_FILTER_KEY);
            String matF = fs.getFilterValue(com.example.cscb07project.systems.FilterState
                    .MATERIAL_FILTER_KEY);
            String perF = fs.getFilterValue(com.example.cscb07project.systems.FilterState
                    .PERIOD_FILTER_KEY);
            for (Object result:results) {
                if (result instanceof Artifact){
                    Artifact artifactF = (Artifact)result;
                    boolean matching = true;
                    if(!catF.equals(com.example.cscb07project.systems.FilterState.NO_FILTER)
                            && !artifactF.getCategory().equals(catF))
                        matching = false;
                    if(!matF.equals(com.example.cscb07project.systems.FilterState.NO_FILTER)
                            && !artifactF.getMaterial().equals(matF))
                        matching = false;
                    if(!perF.equals(com.example.cscb07project.systems.FilterState.NO_FILTER)
                            && !artifactF.getPeriod().equals(perF))
                        matching = false;
                    if(matching)artifactList.add(artifactF);
                }}
            artifactAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error filtering artifacts",
                    Toast.LENGTH_SHORT).show();
        });
    }

//EVERYTHING HERE TO END FOR OCLLECITON
    private void saveCollection() {
        com.google.firebase.auth.FirebaseUser theUser = com.google.firebase
                .auth.FirebaseAuth.getInstance().getCurrentUser();
            if(theUser==null){
                Toast.makeText(getContext(), "user is not logged in error ",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> lotNumbers = new ArrayList<>();
            for (Artifact b :selectionBuffer) lotNumbers.add(b.getLotNumber());

            mainActivity.getMainCRep().addArtifactsToCollectionAndGetFullList(theUser.getUid(),
                            lotNumbers)
                    .addOnSuccessListener(allLotNumbers -> {
                        Toast.makeText(getContext(), "added to collection success",
                                Toast.LENGTH_SHORT).show();

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, CatalogueFragment
                                        .withLotNumbers(new ArrayList<>(allLotNumbers)))
                                .setReorderingAllowed(true).addToBackStack(null).commit();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "failed to save coll",
                                Toast.LENGTH_SHORT).show();
                    });
    }

    private void unsaveCollection() {
        com.google.firebase.auth.FirebaseUser theUser = com.google.firebase
                .auth.FirebaseAuth.getInstance().getCurrentUser();
        if (theUser==null){
            Toast.makeText(getContext(), "user is not logged in error ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> lotNumbers = new ArrayList<>();
        for (Artifact b :selectionBuffer) lotNumbers.add(b.getLotNumber());

        mainActivity.getMainCRep().removeArtifactsFromCollectionAndGetFullList(theUser.getUid(),
                        lotNumbers)
                .addOnSuccessListener(allLotNumbers -> {
                    Toast.makeText(getContext(), "Removed from collection success",
                            Toast.LENGTH_SHORT).show();

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, CatalogueFragment
                                    .withLotNumbers(new ArrayList<>(allLotNumbers)))
                            .setReorderingAllowed(true).addToBackStack(null).commit();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "failed to remove from coll",
                            Toast.LENGTH_SHORT).show();
                });
    }

}

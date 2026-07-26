package com.example.cscb07project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.firebase.database.FirebaseDatabase;


public class ExpandedArtifactFragment extends Fragment {

    private FirebaseDatabase db;
    private TextView artifactName;
    private TextView artifactCategory;
    private TextView artifactPeriod;
    private TextView artifactOrigin;
    private TextView artifactLotNumber;
    private TextView artifactMaterial;
    private TextView artifactDimensions;
    private TextView artifactCondition;
    private TextView artifactLocation;
    private TextView artifactAcquisition;
    private TextView artifactProvenance;
    private TextView artifactAccession;
    private TextView artifactNotes;
    private TextView artifactDescription;
    private TextView likeCount;

    private EditText commentInput;

    private Button likeButton;
    private Button saveButton;
    private Button editButton;
    private Button deleteButton;
    private Button postCommentButton;

    private ArtifactRepository artifactRepo;
    private ExpandedViewRepository expandedViewRepo;

    private String current_lotNumber;

    private Artifact artifact;
    private ExpandedView ev;

    public ExpandedArtifactFragment() {
    }

    public static ExpandedArtifactFragment newInstance(String lotNumber) {
        ExpandedArtifactFragment fragment = new ExpandedArtifactFragment();

        Bundle bun = new Bundle();
        bun.putString("lot_number", lotNumber);
        fragment.setArguments(bun);

        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    )
    {

        View view= inflater.inflate(R.layout.fragment_expanded_artifact_view, container, false);

        artifactName = view.findViewById(R.id.artifactName);
        artifactCategory = view.findViewById(R.id.artifact_category);
        artifactPeriod = view.findViewById(R.id.artifact_dynasty_period);
        artifactOrigin = view.findViewById(R.id.artifact_culture_origin);
        artifactLotNumber = view.findViewById(R.id.artifact_lot_number);

        artifactMaterial = view.findViewById(R.id.artifact_material);
        artifactDimensions = view.findViewById(R.id.Dimensions);
        artifactCondition = view.findViewById(R.id.condition_report);
        artifactLocation = view.findViewById(R.id.current_location);
        artifactAcquisition = view.findViewById(R.id.acquisition_method);
        artifactProvenance = view.findViewById(R.id.provenance);
        artifactAccession = view.findViewById(R.id.accession_number);
        artifactNotes = view.findViewById(R.id.Notes);
        artifactDescription = view.findViewById(R.id.artifact_description);
        likeCount = view.findViewById(R.id.num_of_likes);

        commentInput = view.findViewById(R.id.editTextComment);

        likeButton = view.findViewById(R.id.like_button);
        saveButton = view.findViewById(R.id.save_button);
        editButton = view.findViewById(R.id.edit_button);
        deleteButton = view.findViewById(R.id.delete_button);
        postCommentButton = view.findViewById(R.id.buttonPostComment);


        setupRepo();

        Bundle bun2 = getArguments();
        assert bun2 != null;
        current_lotNumber = bun2.getString("lot_number");

        artifact = artifactRepo.getArtifactByLotNumber(current_lotNumber).getResult();
        displayArtifactDataModelInformation(artifact);

        ev = expandedViewRepo.getExpandedViewByLotNumber(current_lotNumber).getResult();
        likeCount.setText("Like: " + ev.getLikeNumber());


        setOnClickListenersForButtons();

        return view;
    }

    public void setupRepo(){
        MainActivity mainActivity = (MainActivity) requireActivity();
        db = mainActivity.getDatabase();
        expandedViewRepo = new ExpandedViewRepository(db);
        artifactRepo = new ArtifactRepository(db,expandedViewRepo);

    }

    @SuppressLint("SetTextI18n")
    private void displayArtifactDataModelInformation(Artifact artifact) {
        artifactName.setText(artifact.getName());
        artifactCategory.setText("Category: " + checkEmptyOrNot(artifact.getCategory()));
        artifactPeriod.setText("Dynasty/Period: " + checkEmptyOrNot(artifact.getPeriod()));
        artifactOrigin.setText("Culture origin: " + checkEmptyOrNot(artifact.getOrigin()));
        artifactLotNumber.setText("Lot number: " + artifact.getLotNumber());
        artifactMaterial.setText("Material: " + checkEmptyOrNot(artifact.getMaterial()));
        artifactDimensions.setText("Dimensions: " + checkEmptyOrNot(artifact.getDimensions()));
        artifactCondition.setText("Condition report: " + checkEmptyOrNot(artifact.getConditionReport()));
        artifactLocation.setText("Current location: " + checkEmptyOrNot(artifact.getCurrentLocation()));
        artifactAcquisition.setText("Acquisition method: " + checkEmptyOrNot(artifact.getAcquiredMethod()));
        artifactProvenance.setText("Provenance: " + checkEmptyOrNot(artifact.getProvenance()));
        artifactAccession.setText("Accession number: " + checkEmptyOrNot(artifact.getAccessionNumber()));
        artifactNotes.setText("Notes: " + checkEmptyOrNot(artifact.getNotes()));
        artifactDescription.setText("Description: " + checkEmptyOrNot(artifact.getDescription()));
    }

    private String checkEmptyOrNot(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "N/A";
        }

        return input;
    }

    private void setOnClickListenersForButtons(){
        postCommentButton.setOnClickListener(v->postComment());
        likeButton.setOnClickListener(v->likeTheArtifact());
        saveButton.setOnClickListener(v->saveTheArtifact());
        editButton.setOnClickListener(v->editTheArtifact());
        deleteButton.setOnClickListener(v->deleteTheArtifact());

    }

    private void postComment(){
        String text = commentInput.getText().toString().trim();
        if(text.isEmpty()){
            commentInput.setError("An empty comment cannot be posted, please enter a comment");
            return;
        }

        String userId = "";

        Comment comment = new Comment(current_lotNumber,userId,text);
        expandedViewRepo.addComment(current_lotNumber, comment).addOnSuccessListener(unused -> {
            commentInput.setText("");

            Toast.makeText(requireContext(), "Comment posted", Toast.LENGTH_SHORT).show();
        });

    }

    private void likeTheArtifact(){

    }
    private void saveTheArtifact(){

    }
    private void editTheArtifact(){

    }
    private void deleteTheArtifact(){

    }

}

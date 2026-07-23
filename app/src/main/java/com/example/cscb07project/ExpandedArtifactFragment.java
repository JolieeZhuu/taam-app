package com.example.cscb07project;

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

    private static final String ARG_LOT_NUMBER = "lot_number";

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

    private ArtifactRepository artifactRepository;
    private ExpandedViewRepository expandedViewRepository;

    private String lotNumber;

    public ExpandedArtifactFragment() {
        // Required empty constructor
    }

    public static ExpandedArtifactFragment newInstance(String lotNumber) {
        ExpandedArtifactFragment fragment = new ExpandedArtifactFragment();

        Bundle args = new Bundle();
        args.putString(ARG_LOT_NUMBER, lotNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_expanded_artifact_view,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        connectViews(view);

    }
    private void connectViews(View view) {
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
    }
}

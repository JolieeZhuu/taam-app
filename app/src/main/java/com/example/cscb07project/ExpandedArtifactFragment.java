package com.example.cscb07project;

import com.google.android.material.button.MaterialButton;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class ExpandedArtifactFragment extends Fragment {
    private User user;

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

    private MaterialButton likeButton;
    private MaterialButton saveButton;
    private MaterialButton editButton;
    private MaterialButton deleteButton;
    private MaterialButton postCommentButton;
    private MaterialButton viewCommentsButton;

    private ArtifactRepository artifactRepo;
    private ExpandedViewRepository expandedViewRepo;
    private CollectionRepository collectionRepo;
    private UserRepository userRepo;

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
        viewCommentsButton  = view.findViewById(R.id.enter_comment_section);

        setupRepo();

        Bundle bun2 = getArguments();

        current_lotNumber = bun2.getString("lot_number");

        artifact = artifactRepo.getArtifactByLotNumber(current_lotNumber).getResult();
        displayArtifactDataModelInformation(artifact);

        checkLikeStatus(current_lotNumber);
        checkSaveStatus(current_lotNumber);

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
        collectionRepo = new CollectionRepository(db);

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
    private void checkLikeStatus(String current_lotNumber){
        FirebaseUser currentUser = userRepo.getCurrentUser();
        if(currentUser==null){
            return;
        }
        String currentUid = currentUser.getUid();
        boolean liked = expandedViewRepo.isArtifactLikedByUser(currentUid,ev);
        if(liked){
            likeButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );
            likeButton.setIconTint(
                    ColorStateList.valueOf(Color.WHITE)
            );
        }
        else{
            likeButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE)
            );
            likeButton.setIconTint(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );
        }

    }


    private void checkSaveStatus(String current_lotNumber){
        FirebaseUser currentUser = userRepo.getCurrentUser();
        if(currentUser==null){
            return;
        }
        String currentUid = currentUser.getUid();
        if(collectionRepo.getCollectionByName(currentUid,"Saved Artifacts")==null){
            updateSaveButton(false);
            return;
        }
        boolean saved = collectionRepo.isArtifactSavedByUser(current_lotNumber, collectionRepo.getCollectionByName(currentUid,"Saved Artifacts").getResult());
        if(saved){
            saveButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );

            saveButton.setIconTint(
                    ColorStateList.valueOf(Color.WHITE)
            );
        }
        else{
            saveButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE)
            );

            saveButton.setIconTint(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );
        }
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
        viewCommentsButton.setOnClickListener(v->openCommentsSection());
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
        FirebaseUser currentUser = userRepo.getCurrentUser();
        if(currentUser==null){
            return;
        }
        String userId = currentUser.getUid();

        boolean alreadyLiked = expandedViewRepo.isArtifactLikedByUser(userId, ev);

        likeButton.setEnabled(false);

        if (alreadyLiked) {
            expandedViewRepo.unlike(userId,ev)
                    .addOnSuccessListener(a->{updateLikeDisplayAndButton(alreadyLiked);})
                    .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not unlike the artifact", Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> likeButton.setEnabled(true));;
        } else {
            expandedViewRepo.like(userId,ev)
                    .addOnSuccessListener(a->{updateLikeDisplayAndButton(alreadyLiked);})
                    .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not like the artifact", Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> likeButton.setEnabled(true));
        }

    }

    @SuppressLint("SetTextI18n")
    public void updateLikeDisplayAndButton(boolean alreadyLiked){
        int numberOfLikes;
        if(ev.getLikeNumber() == null){
            numberOfLikes=0;
        }
        else {
            numberOfLikes=ev.getLikeNumber();
        }

        likeCount.setText("Likes: " + numberOfLikes);



        if (alreadyLiked) {
            likeButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE)
            );
            likeButton.setIconTint(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );
        }
        else {
            likeButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.rgb(183, 40, 45))
            );
            likeButton.setIconTint(
                    ColorStateList.valueOf(Color.WHITE)
            );
        }

    }


    private void saveTheArtifact(){
        Bundle bun3 = getArguments();
        if(bun3==null){
            return;
        }
        current_lotNumber = bun3.getString("lot_number");
        FirebaseUser currentUser = userRepo.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No artifact selected", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserid=currentUser.getUid();

        saveButton.setEnabled(false);


        collectionRepo.getCollectionByName(currentUserid,"Saved Artifacts").addOnSuccessListener(savedArtifactCollection -> {
            if(savedArtifactCollection==null){
                Collection newCollection = new Collection(currentUserid, "Saved Artifacts");
                collectionRepo.createNewCollection(newCollection)
                        .addOnSuccessListener(unused -> {
                            collectionRepo.addArtifactToCollection(current_lotNumber, newCollection);

                            updateSaveButton(true);
                        })
                        .addOnFailureListener(error ->
                                Toast.makeText(requireContext(), "Could not create a collection", Toast.LENGTH_SHORT).show()
                        );
            }
            else{
                boolean alreadySaved = collectionRepo.isArtifactSavedByUser(current_lotNumber, savedArtifactCollection);

                if (alreadySaved) {
                    updateSaveButton(false);
                    collectionRepo.removeArtifactFromCollection(current_lotNumber,savedArtifactCollection);
                }
                else {
                    updateSaveButton(true);
                    collectionRepo.addArtifactToCollection(current_lotNumber,savedArtifactCollection);
                }
                collectionRepo.addArtifactToCollection(current_lotNumber, collectionRepo.getCollectionByName(currentUserid,"Saved Artifacts").getResult());
            }
        });
        saveButton.setEnabled(true);

    }
    private void updateSaveButton(boolean saved) {
        int red = Color.rgb(183, 40, 45);
        if (saved) {
            // Saved: red background, white bookmark
            saveButton.setBackgroundTintList(
                    ColorStateList.valueOf(red)
            );

            saveButton.setIconTint(
                    ColorStateList.valueOf(Color.WHITE)
            );
        } else {
            // Unsaved: white background, red bookmark
            saveButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE)
            );

            saveButton.setIconTint(
                    ColorStateList.valueOf(red)
            );
        }
    }
    private void editTheArtifact(){
        if(current_lotNumber== null || current_lotNumber.trim().isEmpty()){
            return;
        }
        editButton.setEnabled(false);

        editButton.setEnabled(true);

    }
    private void deleteTheArtifact(){
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }
        deleteButton.setEnabled(false);
        artifactRepo.deleteArtifactByLotNumber(current_lotNumber)
                .addOnSuccessListener(unused -> {
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                });

        deleteButton.setEnabled(true);
    }
    private void openCommentsSection() {
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }
        viewCommentsButton.setEnabled(false);
        CommentsFragment commentFragment = CommentsFragment.newInstance(current_lotNumber);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).addToBackStack(null).commit();
        viewCommentsButton.setEnabled(true);
    }
}



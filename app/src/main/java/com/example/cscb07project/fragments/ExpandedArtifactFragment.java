package com.example.cscb07project.fragments;

import com.bumptech.glide.Glide;
import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.google.android.material.button.MaterialButton;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class ExpandedArtifactFragment extends Fragment {

    //Creating global variables for the artifact data model
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
    private ImageView imageview;


    //This boolean stores information about the admin information
    private boolean isAdmin;

    //A test field that takes in user comments input
    private EditText commentInput;

    //Buttons for different features
    private MaterialButton likeButton;
    private MaterialButton saveButton;
    private MaterialButton editButton;
    private MaterialButton deleteButton;
    private MaterialButton postCommentButton;
    private MaterialButton viewCommentsButton;
    private MaterialButton backButton;

    //Repositories that contains all the useful methods to integrate the fragment with the database
    private ArtifactRepository artifactRepo;
    private ExpandedViewRepository expandedViewRepo;
    private CollectionRepository collectionRepo;

    //Important variables that contain the lot number and user id
    private String current_lotNumber;
    private String currentUid;


    //The artifact object we opened and its corresponding expanded view
    private Artifact artifact;
    private ExpandedView ev;


    //the constructor method
    public ExpandedArtifactFragment() {
    }

    //this method creates the ExpandedArtifactFragment and stores the lotnumber into a bundle that can be
    // A bundle helps the expanded view remember the artifact it is displaying when we open it.
    public static ExpandedArtifactFragment newInstance(String lotNumber) {
        ExpandedArtifactFragment fragment = new ExpandedArtifactFragment();

        Bundle bun = new Bundle();
        bun.putString("lot_number", lotNumber);
        fragment.setArguments(bun);

        return fragment;
    }



    //This method create the view
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
    )
    {

        View view= inflater.inflate(R.layout.fragment_expanded_artifact_view, container, false);

        //Assigning all the fields to their corresponding placeholder in the xml file
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
        imageview=view.findViewById(R.id.artifactImage);

        //assign input sections
        commentInput = view.findViewById(R.id.editTextComment);

        //Assigning buttons
        likeButton = view.findViewById(R.id.like_button);
        saveButton = view.findViewById(R.id.save_button);
        editButton = view.findViewById(R.id.edit_button);
        deleteButton = view.findViewById(R.id.delete_button);
        postCommentButton = view.findViewById(R.id.buttonPostComment);
        viewCommentsButton  = view.findViewById(R.id.enter_comment_section);
        backButton = view.findViewById(R.id.back_button);


        //load information from data base and construct repositories
        MainActivity mainActivity = (MainActivity) requireActivity();
        FirebaseDatabase db = mainActivity.getDb();
        expandedViewRepo = new ExpandedViewRepository(db);
        artifactRepo = new ArtifactRepository(db,expandedViewRepo);
        collectionRepo = new CollectionRepository(db);
        UserRepository userRepo = new UserRepository(db, FirebaseAuth.getInstance());

        //Using bundles to get lot number
        Bundle bun2 = getArguments();
        if(bun2==null){
            return view;
        }
        current_lotNumber = bun2.getString("lot_number");

        FirebaseUser currentUser = userRepo.getCurrentUser();
        if (currentUser != null) {
            currentUid = currentUser.getUid();
        }

        artifactRepo.getArtifactByLotNumber(current_lotNumber).addOnSuccessListener(loadedArtifact -> {
            if (loadedArtifact == null) {
                Toast.makeText(requireContext(), "The artifact lot number do not exist" + current_lotNumber, Toast.LENGTH_LONG).show();
                return;
            }
            artifact = loadedArtifact;
            displayArtifactDataModelInformation(artifact);
        });
        likeButton.setEnabled(false);

        expandedViewRepo.getExpandedViewByLotNumber(current_lotNumber).addOnSuccessListener(loadedExpandedView ->
        {
            if (loadedExpandedView == null) {
                ExpandedView newEv = new ExpandedView(current_lotNumber);
                expandedViewRepo.addExpandedView(current_lotNumber, newEv)
                        .addOnSuccessListener(snapshot -> {
                            ev = newEv;
                            likeButton.setEnabled(true);
                            likeButton.setText("0");
                        });
                return;
            }
            ev = loadedExpandedView;

            likeButton.setText(String.valueOf(ev.getLikeNumber() == null ? 0 : ev.getLikeNumber()));
            boolean liked = expandedViewRepo.isArtifactLikedByUser(currentUid,ev);
            likeButton.setEnabled(true);
            updateLikeButton(liked);


        }
        ).addOnFailureListener(error -> {
            likeButton.setEnabled(true);
        });
        checkSaveStatus(current_lotNumber);
        userRepo.isAdmin(currentUid).addOnSuccessListener(isOrNot ->{
            isAdmin = isOrNot;
        }).addOnCompleteListener(task->setOnClickListenersForButtons());

        return view;
    }

    //This method display the artifact data model information from the database into their corresponding text views
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

        String artifact_url = artifact.getImage();
        if (artifact_url==null||artifact_url.isEmpty()) {return;}
        Glide.with(this).load(artifact_url).into(imageview);
    }

    //This method help check whether a specific artifact data model is empty or not
    private String checkEmptyOrNot(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "N/A";
        }
        return input;
    }

    //This method enabled buttons features and link the click action to other methods in order to modify the database
    private void setOnClickListenersForButtons(){
        if(isAdmin){
            editButton.setOnClickListener(v->editTheArtifact());
            deleteButton.setOnClickListener(v->deleteTheArtifact());
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            Toast.makeText(
                    requireContext(),
                    "Admin User",
                    Toast.LENGTH_SHORT
            ).show();

        }
        else{
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
//            editButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
//            editButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
//            editButton.setTextColor(Color.WHITE);
//            editButton.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            editButton.setVisibility(View.GONE);
//            deleteButton.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
//            deleteButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
//            deleteButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
//            deleteButton.setTextColor(Color.WHITE);
            deleteButton.setVisibility(View.GONE);
            Toast.makeText(
                    requireContext(),
                    "Regular User",
                    Toast.LENGTH_SHORT
            ).show();
        }
        postCommentButton.setOnClickListener(v->postComment());
        likeButton.setOnClickListener(v->likeTheArtifact());
        saveButton.setOnClickListener(v->saveTheArtifact());
        viewCommentsButton.setOnClickListener(v->openCommentsSection());
        backButton.setOnClickListener(v->returnToLastStack());
    }
    private void likeTheArtifact(){
        if (ev == null) {
            return;
        }

        if (currentUid == null || currentUid.trim().isEmpty()) {
            return;
        }
        boolean alreadyLiked = expandedViewRepo.isArtifactLikedByUser(currentUid, ev);

        likeButton.setEnabled(false);

        if (alreadyLiked) {
            expandedViewRepo.unlike(currentUid,ev)
                    .addOnSuccessListener(a->{updateLikeButton(false);})
                    .addOnCompleteListener(task -> likeButton.setEnabled(true));;
        }
        else {
            expandedViewRepo.like(currentUid,ev)
                    .addOnSuccessListener(a->{ updateLikeButton(true);})
                    .addOnCompleteListener(task -> likeButton.setEnabled(true));
        }



    }

    @SuppressLint("SetTextI18n")
    public void updateLikeButton(boolean Liked){



         //not exactly sure why, but it doesnt work otherwise...
        int numberOfLikes = (ev.getLikeNumber() == null) ? 0 : ev.getLikeNumber();


        likeButton.setText(String.valueOf(numberOfLikes));

        int red = ContextCompat.getColor(requireContext(), R.color.crimson_red);
        likeButton.setIconResource(Liked ? R.drawable.heart_icon_filled :R.drawable.heart_icon );
        likeButton.setBackgroundTintList(ColorStateList.valueOf(Liked ?  red:Color.WHITE ));
        likeButton.setIconTint(ColorStateList.valueOf(Liked ? Color.WHITE : red));
        likeButton.setTextColor(Liked ?  Color.WHITE: red);
}


    private void checkSaveStatus(String current_lotNumber){
        collectionRepo.isArtifactInCollection(current_lotNumber, currentUid).addOnSuccessListener(this::updateSaveButton);
    }

    private void saveTheArtifact() {
        Bundle bun3 = getArguments();
        if (bun3 == null) {
            return;
        }
        current_lotNumber = bun3.getString("lot_number");
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }

        saveButton.setEnabled(false);

        collectionRepo.isArtifactInCollection(current_lotNumber, currentUid).addOnSuccessListener(result -> {
            if (result) { // if it is already in collection
                collectionRepo
                        .removeArtifactFromCollection(current_lotNumber, currentUid)
                        .addOnSuccessListener(unused -> {
                            updateSaveButton(false);
                        })
                        .addOnCompleteListener(task ->
                                saveButton.setEnabled(true)
                        );
            }
            else {
                collectionRepo
                        .addArtifactToCollection(current_lotNumber, currentUid).addOnSuccessListener(unused -> {
                            updateSaveButton(true);
                        })
                        .addOnCompleteListener(task ->
                                saveButton.setEnabled(true)
                        );
            }
        });
    }
    private void updateSaveButton(boolean saved) {
        int red = ContextCompat.getColor(requireContext(), R.color.crimson_red);
        saveButton.setIconResource(saved ? R.drawable.save_icon_filled :R.drawable.save_icon );
        saveButton.setBackgroundTintList(ColorStateList.valueOf(saved ?  red:Color.WHITE ));
        saveButton.setIconTint(ColorStateList.valueOf(saved ? Color.WHITE : red));
        saveButton.setTextColor(saved ?  Color.WHITE: red);
    }
    private void editTheArtifact(){
        if(current_lotNumber== null || current_lotNumber.trim().isEmpty()){
            return;
        }
        editButton.setEnabled(false);
        EditArtifactFragment editArtifactFragment = EditArtifactFragment.editFrag(current_lotNumber);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editArtifactFragment).commit();
        editButton.setEnabled(true);
    }
    private void deleteTheArtifact(){
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }
        deleteButton.setEnabled(false);
        artifactRepo.deleteArtifactByLotNumber(current_lotNumber).continueWithTask(task -> collectionRepo.removeArtifactFromAllCollections(current_lotNumber))
            .addOnSuccessListener(unused -> {
                Toast.makeText(requireContext(), "Artifact deleted", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            })
            .addOnCompleteListener(unused->{
                deleteButton.setEnabled(true);
            });
    }

    private void postComment(){
        String text = commentInput.getText().toString().trim();
        if(text.isEmpty()){
            commentInput.setError("An empty comment cannot be posted, please enter a comment");
            return;
        }
        Comment comment = new Comment(current_lotNumber,currentUid,text);
        expandedViewRepo.addComment(current_lotNumber, comment).addOnSuccessListener(unused -> {
            commentInput.setText("");
            Toast.makeText(requireContext(), "Comment posted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener( unused -> commentInput.setText(""));

    }
    private void openCommentsSection() {
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }
        viewCommentsButton.setEnabled(false);
        CommentsFragment commentFragment = CommentsFragment.newInstance(current_lotNumber,currentUid);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, commentFragment).commit();
        viewCommentsButton.setEnabled(true);
    }

    private void returnToLastStack(){
        backButton.setEnabled(false);
        requireActivity().getSupportFragmentManager().popBackStack();
        backButton.setEnabled(true);
    }


}



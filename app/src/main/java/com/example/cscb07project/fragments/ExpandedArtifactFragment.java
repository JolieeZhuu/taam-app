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
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
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
    private ImageView imageview;

    private String current_lotNumber;
    private String currentUid;

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

        commentInput = view.findViewById(R.id.editTextComment);

        likeButton = view.findViewById(R.id.like_button);
        saveButton = view.findViewById(R.id.save_button);
        editButton = view.findViewById(R.id.edit_button);
        deleteButton = view.findViewById(R.id.delete_button);
        postCommentButton = view.findViewById(R.id.buttonPostComment);
        viewCommentsButton  = view.findViewById(R.id.enter_comment_section);

        imageview=view.findViewById(R.id.artifactImage);


        setupRepo();

        Bundle bun2 = getArguments();
        if(bun2==null){
            return view;
        }

        current_lotNumber = bun2.getString("lot_number");

        Toast.makeText(
                requireContext(),
                "Opening lot: [" + current_lotNumber + "]",
                Toast.LENGTH_LONG
        ).show();

        //        FirebaseUser currentUser = userRepo.getCurrentUser();
//
//        if (currentUser == null) {
//            Toast.makeText(
//                    requireContext(),
//                    "Please log in",
//                    Toast.LENGTH_SHORT
//            ).show();
//        } else {
//            currentUid = currentUser.getUid();
//        }
        ////////////////////////////////////////////////////
        currentUid =  "H8jfDo0xjmScP8IVCJD2bX9EPKq1";
///////////////////////////////////////////////////////////////////////////




        artifactRepo.getArtifactByLotNumber(current_lotNumber).addOnSuccessListener(loadedArtifact -> {
            if (loadedArtifact == null) {
                Toast.makeText(
                        requireContext(),
                        "The artifact lot number do not exist"
                                + current_lotNumber,
                        Toast.LENGTH_LONG
                ).show();
                return;
            }
            artifact = loadedArtifact;
            displayArtifactDataModelInformation(artifact);
        })
        .addOnFailureListener(error ->
            Toast.makeText(
                    requireContext(),
                    "Could not load artifact: "
                            + error.getMessage(),
                    Toast.LENGTH_LONG
            ).show()
        );
        likeButton.setEnabled(false);

        expandedViewRepo
                .getExpandedViewByLotNumber(current_lotNumber)
                .addOnSuccessListener(loadedExpandedView -> {
                    if (loadedExpandedView == null) {
                        ExpandedView newEv = new ExpandedView(current_lotNumber);
                        expandedViewRepo.addExpandedView(current_lotNumber, newEv)
                                .addOnSuccessListener(snapshot -> {
                                    ev = newEv;
                                    likeButton.setEnabled(true);
                                    likeButton.setText("0");
//                                    Toast.makeText(
//                                            requireContext(),
//                                            "No expanded view found for lot: "
//                                                    + current_lotNumber,
//                                            Toast.LENGTH_LONG
//                                    ).show();
                                });
                        return;
                    }
                    ev = loadedExpandedView;

                    likeButton.setText(String.valueOf(ev.getLikeNumber() == null ? 0 : ev.getLikeNumber()));
                    boolean liked = expandedViewRepo.isArtifactLikedByUser(currentUid,ev);
                    likeButton.setEnabled(true);
                    updateLikeButton(liked);


                }).addOnFailureListener(error -> {
                    likeButton.setEnabled(true);

                    Toast.makeText(
                            requireContext(),
                            "Could not load like information: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
        checkSaveStatus(current_lotNumber);
        setOnClickListenersForButtons();

        return view;
    }

    public void setupRepo(){
        MainActivity mainActivity = (MainActivity) requireActivity();
        db = mainActivity.getDb();
        expandedViewRepo = new ExpandedViewRepository(db);
        artifactRepo = new ArtifactRepository(db,expandedViewRepo);
        collectionRepo = new CollectionRepository(db);
        userRepo = new UserRepository(db, FirebaseAuth.getInstance());
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

        String artifact_url = artifact.getImage();
        if (artifact_url==null||artifact_url.isEmpty()) {return;}
        Glide.with(this).load(artifact_url).into(imageview);
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

    

    private void likeTheArtifact(){

//        FirebaseUser currentUser = userRepo.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(
//                    requireContext(),
//                    "Please log in before like a artifact",
//                    Toast.LENGTH_SHORT
//            ).show();
//        } else {
//            currentUid = currentUser.getUid();
//        }
        if (ev == null) {
            Toast.makeText(
                    requireContext(),
                    "Like information is still loading",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (currentUid == null || currentUid.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "User ID is missing",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        boolean alreadyLiked = expandedViewRepo.isArtifactLikedByUser(currentUid, ev);

        likeButton.setEnabled(false);

        if (alreadyLiked) {
            expandedViewRepo.unlike(currentUid,ev)
                    .addOnSuccessListener(a->{updateLikeButton(false);Toast.makeText(requireContext(),"Artifact unliked", Toast.LENGTH_SHORT).show();})
                    .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not unlike the artifact", Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> likeButton.setEnabled(true));;
        } else {
            expandedViewRepo.like(currentUid,ev)
                    .addOnSuccessListener(a->{ updateLikeButton(true);Toast.makeText(requireContext(),"Artifact liked", Toast.LENGTH_SHORT).show();})
                    .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not like the artifact", Toast.LENGTH_SHORT).show())
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
        collectionRepo.isArtifactInCollection(current_lotNumber, currentUid).addOnSuccessListener(result -> {
            updateSaveButton(result);
        });

//        collectionRepo.getCollectionByUserId(currentUid).addOnSuccessListener(savedCollection ->{
//            if(savedCollection ==null){
//                updateSaveButton(false);
//                return;
//            }
//            boolean saved = collectionRepo.isArtifactSavedByUser(current_lotNumber, savedCollection);
//            updateSaveButton(saved);
//        });
    }
//    private void saveTheArtifact(){
//        saveButton.setEnabled(false);
//        Bundle bun3 = getArguments();
//        if(bun3==null){
//            return;
//        }
//        current_lotNumber = bun3.getString("lot_number");
//
//        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
//            Toast.makeText(requireContext(), "No artifact selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        collectionRepo.getCollectionByName(currentUid,"Saved Artifacts").addOnSuccessListener(savedArtifactCollection -> {
//            if(savedArtifactCollection==null){
//                Collection newCollection = new Collection(currentUid, "Saved Artifacts");
//                collectionRepo.createNewCollection(newCollection)
//                        .addOnSuccessListener(unused -> {
//                            collectionRepo.addArtifactToCollection(current_lotNumber, newCollection);
//                            Toast.makeText(requireContext(), "A new save collection is created", Toast.LENGTH_SHORT).show();
//                            updateSaveButton(true);
//                        })
//                        .addOnFailureListener(error ->
//                                Toast.makeText(requireContext(), "Could not create a collection", Toast.LENGTH_SHORT).show()
//                        );
//            }
//            else{
//                boolean alreadySaved = collectionRepo.isArtifactSavedByUser(current_lotNumber, savedArtifactCollection);
//
//                if (alreadySaved) {
//                    updateSaveButton(false);
//                    collectionRepo.removeArtifactFromCollection(current_lotNumber,savedArtifactCollection);
//                }
//                else {
//                    updateSaveButton(true);
//                    collectionRepo.addArtifactToCollection(current_lotNumber,savedArtifactCollection);
//                }
////                collectionRepo.addArtifactToCollection(current_lotNumber, collectionRepo.getCollectionByName(currentUserid,"Saved Artifacts").getResult());
//            }
//        })
//        .addOnCompleteListener(task ->
//                saveButton.setEnabled(true)
//        );
//    }

private void saveTheArtifact() {
    Bundle bun3 = getArguments();

    if (bun3 == null) {
        return;
    }

    current_lotNumber = bun3.getString("lot_number");

    if (current_lotNumber == null
            || current_lotNumber.trim().isEmpty()) {
        Toast.makeText(
                requireContext(),
                "No artifact selected",
                Toast.LENGTH_SHORT
        ).show();
        return;
    }

    saveButton.setEnabled(false);

    collectionRepo.isArtifactInCollection(current_lotNumber, currentUid).addOnSuccessListener(result -> {
        if (result) { // if it is already in collection
            collectionRepo
                    .removeArtifactFromCollection(current_lotNumber, currentUid)
                    .addOnSuccessListener(unused -> {
                        updateSaveButton(false);

                        Toast.makeText(
                                requireContext(),
                                "Artifact unsaved",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(error ->
                            Toast.makeText(
                                    requireContext(),
                                    "Could not unsave: "
                                            + error.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    )
                    .addOnCompleteListener(task ->
                            saveButton.setEnabled(true)
                    );
        }
        else {
            collectionRepo
                    .addArtifactToCollection(current_lotNumber, currentUid).addOnSuccessListener(unused -> {
                        updateSaveButton(true);
                        Toast.makeText(requireContext(), "Artifact saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(error ->
                            Toast.makeText(requireContext(), "Could not save: " + error.getMessage(), Toast.LENGTH_LONG).show()
                    )
                    .addOnCompleteListener(task ->
                            saveButton.setEnabled(true)
                    );
        }
    }).addOnFailureListener(error -> {
        error.printStackTrace();
        saveButton.setEnabled(true);
        Toast.makeText(
                requireContext(),
                "Could not load saved collection: "
                        + error.getClass().getSimpleName()
                        + " - "
                        + error.getMessage(),
                Toast.LENGTH_LONG
        ).show();});

//    boolean alreadyLiked = expandedViewRepo.isArtifactLikedByUser(currentUid, ev);
//
//    likeButton.setEnabled(false);
//
//    if (alreadyLiked) {
//        expandedViewRepo.unlike(currentUid,ev)
//                .addOnSuccessListener(a->{updateLikeButton(false);Toast.makeText(requireContext(),"Artifact unliked", Toast.LENGTH_SHORT).show();})
//                .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not unlike the artifact", Toast.LENGTH_SHORT).show())
//                .addOnCompleteListener(task -> likeButton.setEnabled(true));;
//    } else {
//        expandedViewRepo.like(currentUid,ev)
//                .addOnSuccessListener(a->{ updateLikeButton(true);Toast.makeText(requireContext(),"Artifact liked", Toast.LENGTH_SHORT).show();})
//                .addOnFailureListener(error -> Toast.makeText(requireContext(),"Could not like the artifact", Toast.LENGTH_SHORT).show())
//                .addOnCompleteListener(task -> likeButton.setEnabled(true));
//    }

//    collectionRepo.addArtifactToCollection(current_lotNumber, currentUid).addOnSuccessListener(unused2 -> {
//            updateSaveButton(true);
//            Toast.makeText(requireContext(), "Artifact saved", Toast.LENGTH_SHORT).show();
//        })
//        .addOnFailureListener(error ->
//                Toast.makeText(requireContext(), "Could not save: " + error.getMessage(), Toast.LENGTH_LONG).show()
//        )
//        .addOnCompleteListener(task -> saveButton.setEnabled(true));

//    collectionRepo.getCollectionByUserId(currentUid).addOnSuccessListener(savedArtifactCollection -> {
//            if (savedArtifactCollection == null) {
//                Collection newCollection = new Collection(currentUid, "My Default Collection");
//                collectionRepo.createNewCollection(newCollection)
//                    .addOnSuccessListener(unused ->
//                        collectionRepo.addArtifactToCollection(current_lotNumber, currentUid).addOnSuccessListener(unused2 -> {
//                            updateSaveButton(true);
//                            Toast.makeText(requireContext(), "Artifact saved", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(error ->
//                                Toast.makeText(requireContext(), "Could not save: " + error.getMessage(), Toast.LENGTH_LONG).show()
//                        )
//                        .addOnCompleteListener(task -> saveButton.setEnabled(true))
//                    )
//                    .addOnFailureListener(error -> {
//                        saveButton.setEnabled(true);
//                        Toast.makeText(requireContext(), "Could not create collection: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    });
//
//            }
//            else {
//                boolean alreadySaved = collectionRepo.isArtifactSavedByUser(current_lotNumber, savedArtifactCollection);
//                if (alreadySaved) {
//                    collectionRepo
//                            .removeArtifactFromCollection(
//                                    current_lotNumber,
//                                    savedArtifactCollection
//                            )
//                            .addOnSuccessListener(unused -> {
//                                updateSaveButton(false);
//
//                                Toast.makeText(
//                                        requireContext(),
//                                        "Artifact unsaved",
//                                        Toast.LENGTH_SHORT
//                                ).show();
//                            })
//                            .addOnFailureListener(error ->
//                                    Toast.makeText(
//                                            requireContext(),
//                                            "Could not unsave: "
//                                                    + error.getMessage(),
//                                            Toast.LENGTH_LONG
//                                    ).show()
//                            )
//                            .addOnCompleteListener(task ->
//                                    saveButton.setEnabled(true)
//                            );
//                }
//                else {
//                    collectionRepo
//                        .addArtifactToCollection(current_lotNumber, currentUid).addOnSuccessListener(unused -> {
//                            updateSaveButton(true);
//                            Toast.makeText(requireContext(), "Artifact saved", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(error ->
//                                Toast.makeText(requireContext(), "Could not save: " + error.getMessage(), Toast.LENGTH_LONG).show()
//                        )
//                        .addOnCompleteListener(task ->
//                                saveButton.setEnabled(true)
//                        );
//                }
//            }
//        })
//        .addOnFailureListener(error -> {
//            error.printStackTrace();
//
//            Toast.makeText(
//                    requireContext(),
//                    "Could not load saved collection: "
//                            + error.getClass().getSimpleName()
//                            + " - "
//                            + error.getMessage(),
//                    Toast.LENGTH_LONG
//            ).show();});
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

        editButton.setEnabled(true);

        // probably where elina's code has to go

    }
    private void deleteTheArtifact(){
        Toast.makeText(requireContext(), "Delete clicked. Lot: " + current_lotNumber, Toast.LENGTH_LONG).show();
        if (current_lotNumber == null || current_lotNumber.trim().isEmpty()) {
            return;
        }
        deleteButton.setEnabled(false);
        artifactRepo.deleteArtifactByLotNumber(current_lotNumber).continueWithTask(task -> {
            return collectionRepo.removeArtifactFromAllCollections(current_lotNumber);
            })
            .addOnSuccessListener(unused -> {
                Toast.makeText(requireContext(), "Artifact deleted", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            })
            .addOnFailureListener(error ->
                    Toast.makeText(requireContext(), "Delete failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
            )
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
        }).addOnFailureListener( unused -> {commentInput.setText("");
            Toast.makeText(requireContext(), "Fail to post comments", Toast.LENGTH_SHORT).show();});

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



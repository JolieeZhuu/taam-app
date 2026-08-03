package com.example.cscb07project.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.net.Uri;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;

import com.example.cscb07project.repositories.ArtifactRepository;
import com.google.firebase.database.FirebaseDatabase;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditArtifactFragment extends Fragment{

    //mandatory
    private EditText editLotNumber, editName, editDescription;
    private Spinner editCategory, editMaterial, editDynasty; //optioned
    //optional
    private EditText editOrigin, editDimensions, editConditionReport, editCurrentLocation,
            editAcquiredMethod, editProvenance, editAccessionNumber, editNotes;

    private Button buttonUploadArtifactImage, buttonUpdateArtifact,exit_button;

    private FirebaseDatabase db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImageUri;
    private String artifactImageURL = "";
    private ArtifactRepository artifactRepository;
    public static EditArtifactFragment editFrag(String lotNumber){
        EditArtifactFragment fragment = new EditArtifactFragment();
        Bundle args = new Bundle();
        args.putString("lotNumber", lotNumber); //2nd is just the lotnum u wanna use
        fragment.setArguments(args);
        return fragment;
    }
    private void uploadImage(){
        if(selectedImageUri==null){updateArtifact(); return;}

        final StorageReference ref = storageRef.child("artifactImages/"
                + editLotNumber.getText().toString().trim() + ".jpg");
        //uploadTask = ref.putFile(selectedImageUri);
        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                artifactImageURL = downloadUri.toString();
                                updateArtifact();})
                            .addOnFailureListener(e -> {
                                Toast.makeText(
                                        requireContext(),
                                        "Image Upload failure", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(
                            requireContext(),
                            "Image Upload failure", Toast.LENGTH_LONG
                    ).show();
                });

    }
    private final ActivityResultLauncher<PickVisualMediaRequest>
            pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null){
                    selectedImageUri = uri;
                    Toast.makeText(getContext(), "Image selected", Toast.LENGTH_SHORT).show();
                }else{Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();}
            });
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artifact, container, false);

        editLotNumber = view.findViewById(R.id.editLotNumber);
        editName = view.findViewById(R.id.editArtifactName);
        editDescription = view.findViewById(R.id.editDescription);
        editCategory = view.findViewById(R.id.spinnerCategory); //overlaps with additemfragment.java
        editMaterial = view.findViewById(R.id.spinnerMaterial);
        editDynasty = view.findViewById(R.id.spinnerDynasty);
        editOrigin = view.findViewById(R.id.editOrigin);
        editDimensions = view.findViewById(R.id.editDimensions);
        editConditionReport = view.findViewById(R.id.editConditionReport);
        editCurrentLocation = view.findViewById(R.id.editCurrentLocation);
        editAcquiredMethod = view.findViewById(R.id.editAcquiredMethod);
        editProvenance = view.findViewById(R.id.editProvenance);
        editAccessionNumber = view.findViewById(R.id.editAccessionNumber);
        editNotes = view.findViewById(R.id.editNotes);
        buttonUploadArtifactImage = view.findViewById(R.id.buttonUploadArtifactImage);
        buttonUpdateArtifact = view.findViewById(R.id.buttonAddArtifact);
        exit_button = view.findViewById(R.id.buttonExit);


        buttonUpdateArtifact.setText("Update Artifact");

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        artifactRepository = new ArtifactRepository(db);
        //artifactsRef = db.getReference("artifacts");
        storage = FirebaseStorage.getInstance("gs://cscb07-project-e0581.firebasestorage.app");
        storageRef = storage.getReference();//hi

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> materialAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.material_array, android.R.layout.simple_spinner_item);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editMaterial.setAdapter(materialAdapter);

        ArrayAdapter<CharSequence> dynastyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.dynasty_array, android.R.layout.simple_spinner_item);
        dynastyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDynasty.setAdapter(dynastyAdapter);

        buttonUploadArtifactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        buttonUpdateArtifact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        exit_button.setOnClickListener(v->return_to_expanded_view());

        Bundle args = getArguments();
        if(args != null){
            String lotNumber = args.getString("lotNumber");

            if (lotNumber != null && !lotNumber.isEmpty()){
                editLotNumber.setText(lotNumber);
                editLotNumber.setEnabled(false);
                loadArtifact(lotNumber);
            } else {
                Toast.makeText(getContext(),
                        "Lot number failed to load", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(),
                    "Lot number failed to load", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void loadArtifact(String lotNumber){
        artifactRepository.getArtifactByLotNumber(lotNumber).addOnSuccessListener(artifact -> {
            if (artifact==null) {Toast.makeText(getContext(), "Artifact not found",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            fillFields(artifact);
        }).addOnFailureListener(e -> {Toast.makeText(getContext(), "Failed to load artifact",
                                Toast.LENGTH_SHORT).show();
        });
    }
    private void fillFields(Artifact artifact){

        editLotNumber.setText(artifact.getLotNumber());
        editLotNumber.setEnabled(false);
        editName.setText(artifact.getName());
        editDescription.setText(artifact.getDescription());
        editOrigin.setText(artifact.getOrigin());
        editDimensions.setText(artifact.getDimensions());
        editConditionReport.setText(artifact.getConditionReport());
        editCurrentLocation.setText(artifact.getCurrentLocation());
        editAcquiredMethod.setText(artifact.getAcquiredMethod());
        editProvenance.setText(artifact.getProvenance());
        editAccessionNumber.setText(artifact.getAccessionNumber());
        editNotes.setText(artifact.getNotes());
        artifactImageURL = artifact.getImage();

        setSpinnerValue(editCategory, artifact.getCategory());
        setSpinnerValue(editMaterial, artifact.getMaterial());
        setSpinnerValue(editDynasty, artifact.getPeriod());
    }
    private void setSpinnerValue(Spinner spinner, String value){
        if(value == null) return;
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();

        for (int i=0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if(item != null && item.toString().equals(value)){
                spinner.setSelection(i);
                return;
            }
        }
    }
    private void updateArtifact(){
        String lotNumber = editLotNumber.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String category = editCategory.getSelectedItem().toString();
        String material = editMaterial.getSelectedItem().toString();
        String dynasty = editDynasty.getSelectedItem().toString();
        String origin = editOrigin.getText().toString().trim();
        String dimensions = editDimensions.getText().toString().trim();
        String conditionReport = editConditionReport.getText().toString().trim();
        String currentLocation = editCurrentLocation.getText().toString().trim();
        String acquiredMethod = editAcquiredMethod.getText().toString().trim();
        String provenance = editProvenance.getText().toString().trim();
        String accessionNumber = editAccessionNumber.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if ( name.isEmpty() || description.isEmpty() || category.equals("Select category")
                || material.equals("Select material") || dynasty.equals("Select dynasty")) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Artifact artifact = new Artifact(lotNumber, name, description, category, material, dynasty,
                origin, dimensions, conditionReport, currentLocation, acquiredMethod, provenance,
                accessionNumber, notes, artifactImageURL);

        artifactRepository.updateArtifact(artifact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {Toast.makeText(getContext(), "Artifact updated",
                                Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update artifact", Toast.LENGTH_SHORT)
                                .show();
            }
    });



    }
    private void return_to_expanded_view(){
        Bundle args2 = getArguments();
        String lot_number;
        if(args2==null){
            return;
        }
        lot_number = args2.getString("lotNumber");
        if ( lot_number== null || lot_number.isEmpty()) {
            return;
        }
        exit_button.setEnabled(false);
        ExpandedArtifactFragment expandedArtifactFragment = ExpandedArtifactFragment.newInstance(lot_number);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, expandedArtifactFragment).addToBackStack(null).commit();
        exit_button.setEnabled(true);
    }

}

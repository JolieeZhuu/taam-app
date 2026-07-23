package com.example.cscb07project.artifact_creation_page;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddArtifactFragment extends Fragment{

    //mandatory
    private EditText editLotNumber, editName, editDescription;
    private Spinner editCategory, editMaterial, editDynasty; //optioned
    //optional
    private EditText editOrigin, editDimensions, editConditionReport, editCurrentLocation,
            editAcquiredMethod, editProvenance, editAccessionNumber, editNotes;

    private Button buttonUploadArtifactImage, buttonAddArtifact;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private Uri selectedImageUri;
    private String artifactImageURL = "";

    private void uploadImage(){
        if(selectedImageUri==null){addArtifact(); return;}

        final StorageReference ref = storageRef.child("artifactImages/"
                + editLotNumber.getText().toString().trim() + ".jpg");
        //uploadTask = ref.putFile(selectedImageUri);
        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                artifactImageURL = downloadUri.toString();
                                addArtifact();})
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
        buttonAddArtifact = view.findViewById(R.id.buttonAddArtifact);

        db = FirebaseDatabase.getInstance("https://cscb07-project-e0581-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");
        storage = FirebaseStorage.getInstance("gs://cscb07-project-e0581.firebasestorage.app");
        storageRef = storage.getReference();

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

        buttonAddArtifact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        return view;
    }
    private void clearFields(){
        editLotNumber.setText("");
        editName.setText("");
        editDescription.setText("");
        editOrigin.setText("");
        editDimensions.setText("");
        editConditionReport.setText("");
        editCurrentLocation.setText("");
        editAcquiredMethod.setText("");
        editProvenance.setText("");
        editAccessionNumber.setText("");
        editNotes.setText("");
        editCategory.setSelection(0);
        editMaterial.setSelection(0);
        editDynasty.setSelection(0);
        selectedImageUri = null;
        artifactImageURL = "";
    }

    private void addArtifact(){
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

        if (lotNumber.isEmpty() || name.isEmpty() || description.isEmpty() || category.equals("Select category")
                || material.equals("Select material") || dynasty.equals("Select dynasty")) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        artifactsRef = db.getReference("artifacts");
        String artifactId = artifactsRef.push().getKey();
        if(artifactId==null){
            Toast.makeText(getContext(), "Artifact creation failed", Toast.LENGTH_SHORT).show();
            return;
        }
        Artifact artifact = new Artifact(lotNumber, name, description, category, material, dynasty,
                origin, dimensions, conditionReport, currentLocation, acquiredMethod, provenance,
                accessionNumber, notes, artifactImageURL);

        artifactsRef.child(artifactId).setValue(artifact).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Artifact added", Toast.LENGTH_SHORT).show();
                clearFields();
            }else {
                Toast.makeText(getContext(), "Failed to add artifact", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

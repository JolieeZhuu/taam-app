package com.example.cscb07project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cscb07project.interfaces.FieldScraper;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterFragment extends Fragment {
    private Map<String, String> filter_buffer;
    private FilterState main_fs;
    private boolean saved_flag = true;
    private ArtifactRepository repo;
    private Button buttonPushFilters;

    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonClearFilters;
    @SuppressWarnings("FieldCanBeLocal")
    private Button buttonExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity main_activity = (MainActivity) requireActivity();
        main_fs = main_activity.getMainFilters();
        filter_buffer = new HashMap<>(main_fs.getFilters());
        repo = main_activity.getArtifactRepository();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        buttonPushFilters = view.findViewById(R.id.save_filters);
        buttonPushFilters.setOnClickListener(v -> {
            SaveBuffer();
            updateSaveColour();
        });

        buttonClearFilters = view.findViewById(R.id.clear_filters);
        buttonClearFilters.setOnClickListener(v -> ClearFilters());

        buttonExit = view.findViewById(R.id.exit_screen);
        buttonExit.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Spinner categorySpinner = view.findViewById(R.id.type_spinner); // Hardcode each filter.
        Spinner materialSpinner = view.findViewById(R.id.material_spinner);
        Spinner periodSpinner = view.findViewById(R.id.period_spinner);

        fetchDbFieldValues(categorySpinner, "category");
        fetchDbFieldValues(materialSpinner, "material");
        fetchDbFieldValues(periodSpinner, "period");

        setSpinnerSelectionListener(categorySpinner, "category");
        setSpinnerSelectionListener(materialSpinner, "material");
        setSpinnerSelectionListener(periodSpinner, "period");

        return view;
    }

    private void fetchDbFieldValues(Spinner spinner, String key){
        repo.scrapeFieldValues(key, new FieldScraper() {
            @Override
            public void onResult(List<String> values) {
                populateSpinner(spinner, values);
            }

            @Override
            public void onError(DatabaseError err) {
                // TODO: What are we supposed to do if we can't access the database? Crash the app?
            }
        });
    }
    private void populateSpinner(Spinner spinner, List<String> values){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                values
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }
    private void setSpinnerSelectionListener (Spinner spinner, String key){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                filter_buffer.replace(key, value);
                saved_flag = false;
                updateSaveColour();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                filter_buffer.replace(key, null);
                saved_flag = false;
                updateSaveColour();
            }
        });
    }

    private void updateSaveColour(){
        buttonPushFilters.setBackgroundResource(
                saved_flag
                    ? R.drawable.button_alt_background
                    : R.drawable.button_default_background
        );
    }
    public void SaveBuffer(){
        if (main_fs.getFilters().equals(filter_buffer)) {
            main_fs.UpdateFilters(filter_buffer);
        }
        saved_flag = true;
        updateSaveColour();
    }
    public void ClearFilters(){
        for (Map.Entry<String, String> entry : filter_buffer.entrySet()){
            filter_buffer.replace(entry.getKey(), null);
        }
        saved_flag = false;
        updateSaveColour();
    }

//    public void loadFilters(FilterState fs){// Implement if needed.}
}

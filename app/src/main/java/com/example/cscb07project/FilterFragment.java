package com.example.cscb07project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;


public class FilterFragment extends Fragment {
    private Map<String, String> filter_buffer;
    private FilterState main_fs;
    private boolean saved_flag = true;

    private Button buttonPushFilters;
    private Button buttonClearFilters;
    private Button buttonExit;
    private Spinner categorySpinner;
    private Spinner materialSpinner;
    private Spinner periodSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity main_activity = (MainActivity) requireActivity();
        main_fs = main_activity.getMainFilters();
        filter_buffer = new HashMap<>(main_fs.getFilters());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        buttonPushFilters = view.findViewById(R.id.save_filters);
        buttonPushFilters.setOnClickListener(v -> SaveBuffer());

        buttonClearFilters = view.findViewById(R.id.clear_filters);
        buttonClearFilters.setOnClickListener(v -> ClearFilters());

        buttonExit = view.findViewById(R.id.exit_screen);
        buttonExit.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        categorySpinner = view.findViewById(R.id.type_spinner);
        materialSpinner = view.findViewById(R.id.material_spinner);
        periodSpinner = view.findViewById(R.id.period_spinner);

        // REFACTOR BLOCK: AY1
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.dev_filters,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        materialSpinner.setAdapter(adapter);
        periodSpinner.setAdapter(adapter);
        // END REFACTOR BLOCK

        return view;
    }

    private void updateSaveColour(){
        buttonPushFilters.setBackgroundResource(
                saved_flag
                    ? R.drawable.button_alt_background
                    : R.drawable.button_default_background
        );
    }

    public void SaveBuffer(){
        if (main_fs.getFilters() != filter_buffer) {
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

//    public void loadFilters(FilterState fs){ // Implement if needed.
//        filter_buffer = fs.getFilters();
//        saved_flag = false;
//        updateSaveColour();
//    }
}

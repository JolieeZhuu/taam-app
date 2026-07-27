package com.example.cscb07project.fragments;

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

import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.example.cscb07project.systems.FieldScraper;
import com.example.cscb07project.systems.FilterState;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterFragment extends Fragment {
    private MainActivity mainActivity;
    private Map<String, String> filter_buffer;
    private boolean saved_flag = true;
    List<String> materialList = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();
    List<String> periodList = new ArrayList<>();

    Spinner categorySpinner;
    Spinner materialSpinner;
    Spinner periodSpinner;
    private Button buttonPushFilters;
    private Button buttonClearFilters;
    private Button buttonExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        filter_buffer = new HashMap<>(mainActivity.getMainFS().getFilters());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        buttonPushFilters = view.findViewById(R.id.save_filters);
        buttonClearFilters = view.findViewById(R.id.clear_filters);
        buttonExit = view.findViewById(R.id.selectButton);

        categorySpinner = view.findViewById(R.id.type_spinner);
        materialSpinner = view.findViewById(R.id.material_spinner);
        periodSpinner = view.findViewById(R.id.period_spinner);

        adaptFromDb(categorySpinner, FilterState.CATEGORY_FILTER_KEY, categoryList);
        adaptFromDb(materialSpinner, FilterState.MATERIAL_FILTER_KEY, materialList);
        adaptFromDb(periodSpinner, FilterState.PERIOD_FILTER_KEY, periodList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle SavedInstanceState){
        buttonPushFilters.setOnClickListener(v -> {
            SaveBuffer();
            updateSaveColour();
        });
        buttonClearFilters.setOnClickListener(v -> ClearFilters());
        buttonExit.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setSpinnerSelectionListener(categorySpinner, FilterState.CATEGORY_FILTER_KEY, categoryList);
        setSpinnerSelectionListener(materialSpinner, FilterState.MATERIAL_FILTER_KEY, materialList);
        setSpinnerSelectionListener(periodSpinner, FilterState.PERIOD_FILTER_KEY, periodList);
    }

    private void adaptFromDb(Spinner spinner, String key, List<String> refList){
        mainActivity.getMainARep().scrapeFieldValues(key, new FieldScraper() {
            @Override
            public void onResult(List<String> values) {
                populateSpinner(spinner, values, refList, key);
            }

            @Override
            public void onError(DatabaseError err) {
                // TODO: Handle this.
            }
        });
    }

    @SuppressWarnings("all") // refList otherwise produces compiler warnings.
    private void populateSpinner(Spinner spinner, List<String> values, List<String> refList, String key){
        refList = values;
        refList.add(0, FilterState.NO_FILTER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                refList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        MatchSpinner(spinner, refList, key);
    }

    @SuppressWarnings("all") // refList otherwise produces compiler warnings.
    private void setSpinnerSelectionListener (Spinner spinner, String key, List<String> refList){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter_buffer.replace(key, adapterView.getItemAtPosition(i).toString());
                saved_flag = false;
                updateSaveColour();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                filter_buffer.replace(key, FilterState.NO_FILTER);
                saved_flag = false;
                updateSaveColour();
            }

        });
    }

    public void SaveBuffer(){
        mainActivity.getMainFS().UpdateFilters(filter_buffer);
        saved_flag = true;
        updateSaveColour();
    }
    public void ClearFilters(){
        for (Map.Entry<String, String> entry : filter_buffer.entrySet()){
            filter_buffer.replace(entry.getKey(), FilterState.NO_FILTER);
        }
        MatchSpinner(categorySpinner, categoryList, FilterState.CATEGORY_FILTER_KEY);
        MatchSpinner(materialSpinner, materialList, FilterState.MATERIAL_FILTER_KEY);
        MatchSpinner(periodSpinner, periodList, FilterState.PERIOD_FILTER_KEY);
        saved_flag = false;
        updateSaveColour();
    }

    private void updateSaveColour(){
        buttonPushFilters.setBackgroundColor(
                saved_flag
                        ? getResources().getColor(
                        R.color.state_button_1_alt, null)
                        : getResources().getColor(
                        R.color.state_button_1_default, null)
        );
    }
    public void MatchSpinner(Spinner spinner, List<String> list, String key){
        spinner.setSelection(Math.max(list.indexOf(filter_buffer.get(key)), 0));
    }

//    public void loadFilters(FilterState fs){// Implement if needed.}
}

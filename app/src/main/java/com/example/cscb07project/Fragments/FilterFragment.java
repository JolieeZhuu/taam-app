package com.example.cscb07project.Fragments;

import static android.graphics.Color.rgb;

import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.cscb07project.entities.FilterState;
import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;

import java.util.HashMap;
import java.util.Map;


public class FilterFragment extends Fragment {
    private Map<String, String> filter_buffer;
    private FilterState main_fs;
    private boolean saved_flag = true;

    private Button buttonPushFilters;
    private Button buttonClearFilters;
    private Button buttonExit;
    private Spinner type_spinner;
    private Spinner material_spinner;
    private Spinner period_spinner;
    private Spinner origin_spinner;
    private ColorStateList unsaved_button_tint;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity main_activity = (MainActivity) requireActivity();
        main_fs = main_activity.getMainFilters();
        filter_buffer = new HashMap<String, String>(main_fs.getFilters());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        buttonPushFilters = view.findViewById(R.id.save_filters);
        unsaved_button_tint = buttonPushFilters.getBackgroundTintList();
        buttonPushFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveBuffer();
            }
        });

        buttonClearFilters = view.findViewById(R.id.clear_filters);
        buttonClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { ClearFilters(); }
        });

        buttonExit = view.findViewById(R.id.exit_screen);
        buttonExit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        type_spinner = (Spinner) view.findViewById(R.id.type_spinner);
        material_spinner = (Spinner) view.findViewById(R.id.material_spinner);
        period_spinner = (Spinner) view.findViewById(R.id.period_spinner);
        origin_spinner = (Spinner) view.findViewById(R.id.origin_spinner);

        // REFACTOR BLOCK: AY1
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.dev_filters,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
        material_spinner.setAdapter(adapter);
        period_spinner.setAdapter(adapter);
        origin_spinner.setAdapter(adapter);
        // END REFACTOR BLOCK

        return view;
    }

    private void updateSaveColour(){
        if (saved_flag){
            buttonPushFilters.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
        else {
            buttonPushFilters.setBackgroundTintList(unsaved_button_tint);
        }
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

    public void loadFilters(FilterState fs){
        filter_buffer = fs.getFilters();
        saved_flag = false;
        updateSaveColour();
    }
}

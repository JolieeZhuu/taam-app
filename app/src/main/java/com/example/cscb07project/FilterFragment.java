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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        Button buttonPushFilters = view.findViewById(R.id.save_filters);
        Button buttonDiscardFilters = view.findViewById(R.id.discard_filters);
        Button buttonExit = view.findViewById(R.id.exit_screen);

        buttonPushFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveBuffer();
            }
        });

        buttonDiscardFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Map.Entry<String, String> entry : filter_buffer.entrySet()){
                    filter_buffer.replace(entry.getKey(), null);
                }
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        Spinner type_spinner = (Spinner) view.findViewById(R.id.type_spinner);
        Spinner material_spinner = (Spinner) view.findViewById(R.id.material_spinner);
        Spinner period_spinner = (Spinner) view.findViewById(R.id.period_spinner);
        Spinner origin_spinner = (Spinner) view.findViewById(R.id.origin_spinner);

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



    /**
     * Default constructor that loads main_filters into buffer.
     */
    public FilterFragment(){
        MainActivity main_activity = (MainActivity) requireActivity();
        main_fs = main_activity.getMainFilters();
        filter_buffer = new HashMap<String, String>(main_fs.getFilters());
    }
    public FilterFragment(FilterState filterstate){
        filter_buffer = filterstate.getFilters();
    }

    public void SaveBuffer(){
        if (main_fs.getFilters() != filter_buffer) {
            main_fs.UpdateFilters(filter_buffer);
            System.out.println("Filters Saved!");
            // Set text to "Saved!"
        }
        // Button should be greyed out but in case it gets pressed do nothing;
    }

    public void ClearFilters(){
        for (Map.Entry<String, String> entry : filter_buffer.entrySet()){
            String category = entry.getKey();
            String value = entry.getKey();
            value = null;
        }
    }


}

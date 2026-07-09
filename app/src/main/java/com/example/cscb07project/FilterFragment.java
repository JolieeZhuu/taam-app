package com.example.cscb07project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.HashMap;
import java.util.Map;


public class FilterFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        Button buttonPushFilters = view.findViewById(R.id.push_filters);

        buttonPushFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveBuffer();
            }
        });
        return view;
    }

    private Map<String, String> filter_buffer;
    private FilterState main_fs;

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

package com.example.cscb07project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
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

        return view;
    }

    private Map<String, String> filter_buffer;

    /**
     * Default constructor that loads main_filters into buffer.
     */
    public FilterFragment(){
        MainActivity main_activity = (MainActivity) requireActivity();
        FilterState main_fs = main_activity.getMainFilters();
        filter_buffer = new HashMap<String, String>(main_fs.getFilters());
    }
    public FilterFragment(FilterState filterstate){
        filter_buffer = filterstate.getFilters();
    }

}

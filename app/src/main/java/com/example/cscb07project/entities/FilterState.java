package com.example.cscb07project.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores filter set in fixed fields.
 */
public class FilterState {

    private Map<String, String> filters = new HashMap<>();

    public FilterState(){
        filters.put("name", null);
        filters.put("object_type", null);
        filters.put("material", null);
        filters.put("time_period", null);
        filters.put("origin", null);
    }

    public Map<String, String> getFilters(){
        return filters;
    }
    public void UpdateFilters(Map<String, String> new_filters){
        filters = new_filters;
    }


    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof FilterState)){
            return false;
        }
        FilterState other = (FilterState) obj;
        return filters.equals(other.getFilters());
    }
}

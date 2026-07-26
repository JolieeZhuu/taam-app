package com.example.cscb07project.systems;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores filter set in fixed fields.
 */
public class FilterState {

    private Map<String, String> filters = new HashMap<>();
    public static final String NO_FILTER = "All";
    public static final String CATEGORY_FILTER_KEY = "category";
    public static final String MATERIAL_FILTER_KEY = "material";
    public static final String PERIOD_FILTER_KEY = "period";

    public FilterState(){
        filters.put("name", NO_FILTER);
        filters.put(CATEGORY_FILTER_KEY, NO_FILTER);
        filters.put(MATERIAL_FILTER_KEY, NO_FILTER);
        filters.put(PERIOD_FILTER_KEY, NO_FILTER);
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

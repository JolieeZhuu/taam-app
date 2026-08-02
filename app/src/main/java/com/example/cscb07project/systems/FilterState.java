package com.example.cscb07project.systems;

import com.example.cscb07project.entities.Artifact;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores filter set in fixed fields.
 */
public class FilterState {

    private Map<String, String> filters = new HashMap<>();
    public static final String NO_FILTER = "All";
    public static final String CATEGORY_FILTER_KEY = Artifact.CATEGORY_KEY;
    public static final String MATERIAL_FILTER_KEY = Artifact.MATERIAL_KEY;
    public static final String PERIOD_FILTER_KEY = Artifact.PERIOD_KEY;
    public FilterState(){
        filters.put("name", NO_FILTER);
        filters.put(CATEGORY_FILTER_KEY, NO_FILTER);
        filters.put(MATERIAL_FILTER_KEY, NO_FILTER);
        filters.put(PERIOD_FILTER_KEY, NO_FILTER);
    }

    public Map<String, String> getFilters(){
        return filters;
    }

    /**
     * @param filterBy should be one of the constant filter types defined in the FilterState class.
     * @return The corresponding value of that filter as a string.
     */
    public String getFilterValue(String filterBy){
        return filters.get(filterBy);
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

package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Activity;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface ActivityInterface {
    public Task<Void> addActivity(Activity activity);
    public Task<List<Activity>> getFirstTenActivities();
    public Task<Void> deleteActivityById(String activityId);
}

package com.example.cscb07project.interfaces;

import com.google.android.gms.tasks.Task;

public interface AdminInterface {
    Task<Void> createAdmin(String userId);

    // only deletes the userId in the admin table
    Task<Void> deleteAdminById(String userId);
}

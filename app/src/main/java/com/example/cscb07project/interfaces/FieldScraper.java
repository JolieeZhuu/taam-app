package com.example.cscb07project.interfaces;

import com.google.firebase.database.DatabaseError;

import java.util.List;

public interface FieldScraper {
    void onResult(List<String> values);
    void onError(DatabaseError err);
}

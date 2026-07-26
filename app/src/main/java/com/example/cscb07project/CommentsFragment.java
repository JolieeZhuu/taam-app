package com.example.cscb07project;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.firebase.database.FirebaseDatabase;


public class CommentsFragment {

    public CommentsFragment() {
    }

    public static CommentsFragment newInstance(String lotNumber) {
        CommentsFragment fragment = new CommentsFragment();

        Bundle bun = new Bundle();
        bun.putString("lot_number", lotNumber);
        fragment.setArguments(bun);

        return fragment;
    }
}

package com.example.cscb07project.systems;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.R;

public class ArtifactViewHolder extends RecyclerView.ViewHolder {
    TextView textViewName, textViewCategory, textViewMaterial, textViewPeriod;
    public ArtifactViewHolder (@NonNull View artifactView) {
        super(artifactView);
        textViewName = artifactView.findViewById(R.id.textViewName);
        textViewCategory = artifactView.findViewById(R.id.textViewCategory);
        textViewMaterial = artifactView.findViewById(R.id.textViewMaterial);
        textViewPeriod = artifactView.findViewById(R.id.textViewPeriod);
    }
}

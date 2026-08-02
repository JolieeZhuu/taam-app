package com.example.cscb07project.systems;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.R;

public class ArtifactViewHolder extends RecyclerView.ViewHolder {
    TextView textViewName;
    ImageView imageViewArtifact;

    public ArtifactViewHolder (@NonNull View artifactView) {
        super(artifactView);
        textViewName = artifactView.findViewById(R.id.textViewName);
        imageViewArtifact = artifactView.findViewById(R.id.imageViewArtifact);
    }
}

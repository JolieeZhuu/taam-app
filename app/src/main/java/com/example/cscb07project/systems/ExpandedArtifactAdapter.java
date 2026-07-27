package com.example.cscb07project.systems;

import androidx.annotation.NonNull;
import com.example.cscb07project.entities.Artifact;

import java.util.List;

public class ExpandedArtifactAdapter extends ArtifactAdapter {
    public ExpandedArtifactAdapter(List<Artifact> artifacts, OnArtifactClickedListener listener){
        super(artifacts, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);

        holder.textViewName.setText(artifact.getName());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());

        holder.itemView.setOnClickListener(v -> listener.onArtifactClicked(artifact) );
    }


}

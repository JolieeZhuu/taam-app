package com.example.cscb07project.systems;

import com.example.cscb07project.entities.Artifact;

import android.graphics.Typeface;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

public class SelectionArtifactAdapter extends ArtifactAdapter{
    private final Set<Artifact> selectionBuffer;
    public SelectionArtifactAdapter(List<Artifact> artifactList,
                                    Set<Artifact> selectionBuffer,
                                    OnArtifactClickedListener listener) {
        super(artifactList, listener);
        this.selectionBuffer = selectionBuffer;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);

        holder.textViewName.setText(artifact.getName());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());

        holder.itemView.setSelected(selectionBuffer.contains(artifact));
        onItemSelectionChanged(holder);

        holder.itemView.setOnClickListener(v -> listener.onArtifactClicked(artifact));
    }

    public void onItemSelectionChanged(ArtifactViewHolder holder){
        if (holder.itemView.isSelected()) {
            holder.textViewName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.textViewName.setTypeface(null, Typeface.NORMAL);
        }
    }
}
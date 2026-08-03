package com.example.cscb07project.systems;

import com.bumptech.glide.Glide;
import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;

import android.graphics.Typeface;
import android.util.Log;

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
        Log.d("ArtifactImageCheck", "image value: " + artifact.getImage());

        holder.textViewName.setText(artifact.getName());
        Glide.with(holder.imageViewArtifact.getContext())
                .load(artifact.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageViewArtifact);

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
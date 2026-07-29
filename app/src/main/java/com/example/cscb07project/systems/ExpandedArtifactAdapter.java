package com.example.cscb07project.systems;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;

import java.util.List;

public class ExpandedArtifactAdapter extends ArtifactAdapter {
    public ExpandedArtifactAdapter(List<Artifact> artifacts, OnArtifactClickedListener listener){
        super(artifacts, listener);
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

        holder.itemView.setOnClickListener(v -> listener.onArtifactClicked(artifact) );
    }


}

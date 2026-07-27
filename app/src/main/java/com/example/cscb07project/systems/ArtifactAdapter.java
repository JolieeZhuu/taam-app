package com.example.cscb07project.systems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.R;
import com.example.cscb07project.entities.Artifact;

import java.util.List;

public abstract class ArtifactAdapter extends RecyclerView.Adapter<ArtifactViewHolder>{
    protected final List<Artifact> artifactList;
    public interface OnArtifactClickedListener {
        void onArtifactClicked(Artifact artifact);
    }
    protected final OnArtifactClickedListener listener;

    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactClickedListener listener){
        this.artifactList = artifactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_artifact,
                parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

}
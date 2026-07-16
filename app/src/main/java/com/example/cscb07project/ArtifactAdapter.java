package com.example.cscb07project;

import com.example.cscb07project.entities.Artifact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private List<Artifact> artifactList;

    public ArtifactAdapter(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_artifact, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        holder.textViewCategory.setText(artifact.getCategory());
        holder.textViewMaterial.setText(artifact.getMaterial());
        holder.textViewPeriod.setText(artifact.getPeriod());
//        holder.textViewOrigin.setText(artifact.getOrigin());
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategory, textViewMaterial, textViewPeriod;

        public ArtifactViewHolder(@NonNull View artifactView) {
            super(artifactView);
            textViewName = artifactView.findViewById(R.id.textViewName);
            textViewCategory = artifactView.findViewById(R.id.textViewCategory);
            textViewMaterial = artifactView.findViewById(R.id.textViewMaterial);
            textViewPeriod = artifactView.findViewById(R.id.textViewPeriod);
//            textViewOrigin = artifactView.findViewById(R.id.textViewOrigin);
        }
    }
}

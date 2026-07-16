package com.example.cscb07project;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_artifact_adapter, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewTitle.setText(artifact.getTitle());
        holder.textViewAuthor.setText(artifact.getAuthor());
        holder.textViewGenre.setText(artifact.getGenre());
        holder.textViewDescription.setText(artifact.getDescription());
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;

        public ArtifactViewHolder(@NonNull View artifactView) {
            super(artifactView);
            textViewTitle = artifactView.findViewById(R.id.textViewTitle);
            textViewAuthor = artifactView.findViewById(R.id.textViewAuthor);
            textViewGenre = artifactView.findViewById(R.id.textViewGenre);
            textViewDescription = artifactView.findViewById(R.id.textViewDescription);
        }
    }
}

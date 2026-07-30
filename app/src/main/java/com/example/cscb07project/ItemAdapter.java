package com.example.cscb07project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.entities.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_adapter, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textViewTitle.setText(comment.getTitle());
        holder.textViewAuthor.setText(comment.getAuthor());
        holder.textViewGenre.setText(comment.getGenre());
        holder.textViewDescription.setText(comment.getDescription());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;

        public CommentViewHolder(@NonNull View commentView) {
            super(commentView);
            textViewTitle = commentView.findViewById(R.id.textViewTitle);
            textViewAuthor = commentView.findViewById(R.id.textViewAuthor);
            textViewGenre = commentView.findViewById(R.id.textViewGenre);
            textViewDescription = commentView.findViewById(R.id.textViewDescription);
        }
    }
}

package com.example.cscb07project.systems;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.R;
import com.example.cscb07project.entities.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList;
    private final OnDeleteClickListener deleteClickListener;
    private boolean deleteModeEnabled;

    public interface OnDeleteClickListener {
        void onDeleteClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList, OnDeleteClickListener deleteClickListener,  boolean deleteModeEnabled)
    {
        this.commentList = commentList;
        this.deleteClickListener = deleteClickListener;
        this.deleteModeEnabled = deleteModeEnabled;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position
    ) {
        Comment comment = commentList.get(position);
        holder.commentUser.setText(comment.getUserId());
        holder.commentText.setText(comment.getComment());

        int red = Color.rgb(183, 40, 45);
        int grey = Color.rgb(170, 170, 170);

        if (deleteModeEnabled) {
            holder.deleteCommentButton.setEnabled(true);
            holder.deleteCommentButton.setClickable(true);
            holder.deleteCommentButton.setAlpha(1.0f);

            holder.deleteCommentButton.setImageTintList(
                    ColorStateList.valueOf(red)
            );

            holder.deleteCommentButton.setOnClickListener(v ->
                    deleteClickListener.onDeleteClick(comment)
            );
        } else {
            holder.deleteCommentButton.setEnabled(false);
            holder.deleteCommentButton.setClickable(false);
            holder.deleteCommentButton.setAlpha(0.5f);

            holder.deleteCommentButton.setImageTintList(
                    ColorStateList.valueOf(grey)
            );

            holder.deleteCommentButton.setOnClickListener(null);
        }
    }

    public void setDeleteModeEnabled(boolean enabled) {
        deleteModeEnabled = enabled;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final TextView commentUser;
        private final TextView commentText;
        private final ImageButton deleteCommentButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUser = itemView.findViewById(R.id.commentUsername);
            commentText = itemView.findViewById(R.id.commentText);
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);
        }
    }
}
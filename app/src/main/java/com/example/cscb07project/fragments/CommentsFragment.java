package com.example.cscb07project.fragments;
import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.example.cscb07project.systems.CommentAdapter;
import com.google.android.material.button.MaterialButton;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class CommentsFragment extends Fragment{

    private String currentLotNumber ;
    private FirebaseDatabase db;
    ExpandedViewRepository expandedViewRepo;
    private List<Comment> commentList;

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private MaterialButton backButton;
    private MaterialButton deleteCommentsButton;
    private boolean deleteModeEnabled = false;

    public CommentsFragment() {
    }

    public static CommentsFragment newInstance(String lotNumber) {
        CommentsFragment fragment = new CommentsFragment();

        Bundle bun = new Bundle();
        bun.putString("lot_number", lotNumber);
        fragment.setArguments(bun);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_section, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        db = mainActivity.getDb();
        expandedViewRepo = new ExpandedViewRepository(db);
        Bundle bun2 = getArguments();
        if (bun2 != null) {
            currentLotNumber = bun2.getString("lot_number");
        }

        commentList = new ArrayList<>();
        commentRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        deleteCommentsButton = view.findViewById(R.id.buttonDeleteSelectedComments);
        backButton = view.findViewById(R.id.back_button);


        updateDeleteButton();
        displayALLComments();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displayALLComments(){

        commentAdapter = new CommentAdapter(commentList, comment -> deleteComment(comment),deleteModeEnabled);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentRecyclerView.setAdapter(commentAdapter);
        deleteCommentsButton.setOnClickListener(v -> changeDeleteMode());
        backButton.setOnClickListener(v -> returnToExpandedView());


        if(currentLotNumber==null){return;}
        expandedViewRepo.getCommentsByLotNumber(currentLotNumber).addOnSuccessListener( comments ->{
            commentList.clear();
            if(comments!=null){
                commentList.addAll(comments);
            }
            commentAdapter.notifyDataSetChanged();
        });


    }
    private void changeDeleteMode() {
        deleteModeEnabled = !deleteModeEnabled;
        updateDeleteButton();
        commentAdapter.setDeleteModeEnabled(deleteModeEnabled);
    }

    private void updateDeleteButton() {
       int red = Color.rgb(183, 40, 45);
        if (deleteModeEnabled) {
            deleteCommentsButton.setBackgroundTintList(
                    ColorStateList.valueOf(red)
            );

            deleteCommentsButton.setTextColor(Color.WHITE);

            deleteCommentsButton.setIconTint(
                    ColorStateList.valueOf(Color.WHITE)
            );
        } else {
            deleteCommentsButton.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE)
            );

            deleteCommentsButton.setTextColor(red);

            deleteCommentsButton.setIconTint(
                    ColorStateList.valueOf(red)
            );
        }
    }
    @SuppressLint("NotifyDataSetChanged")

    private void deleteComment(Comment comment) {
        if (!deleteModeEnabled) {
            return;
        }
        if (comment ==null||comment.getCommentId()==null) {return;}

        expandedViewRepo.deleteCommentById(currentLotNumber, comment.getCommentId())
                .addOnSuccessListener(unused->{
                    Toast.makeText(
                            requireContext(),
                            "Comment deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                    displayALLComments();
                })
                .addOnFailureListener(error ->
                        Toast.makeText(
                                requireContext(),
                                "Could not delete comment",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void returnToExpandedView() {
        if (currentLotNumber == null || currentLotNumber.trim().isEmpty()) {
            return;
        }
        backButton.setEnabled(false);
        ExpandedArtifactFragment expandedArtifactFragment = ExpandedArtifactFragment.newInstance(currentLotNumber);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, expandedArtifactFragment).addToBackStack(null).commit();
        backButton.setEnabled(true);
    }
}

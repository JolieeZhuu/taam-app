package com.example.cscb07project.fragments;
import com.example.cscb07project.MainActivity;
import com.example.cscb07project.R;
import com.example.cscb07project.repositories.UserRepository;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class CommentsFragment extends Fragment{
    private boolean isAdmin;
    private String currentUid;
    private String currentLotNumber ;
    private FirebaseDatabase db;
    ExpandedViewRepository expandedViewRepo;
    UserRepository userRepo;
    private List<Comment> commentList;

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private MaterialButton backButton;
    private MaterialButton deleteCommentsButton;
    private boolean deleteModeEnabled = false;

    public CommentsFragment() {
    }

    public static CommentsFragment newInstance(String lotNumber, String currentUid) {
        CommentsFragment fragment = new CommentsFragment();

        Bundle bun = new Bundle();
        bun.putString("lot_number", lotNumber);
        bun.putString("user_id",currentUid);
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
        userRepo = new UserRepository(db, FirebaseAuth.getInstance());

        Bundle bun2 = getArguments();
        if (bun2 != null) {
            currentLotNumber = bun2.getString("lot_number");
            currentUid = bun2.getString("user_id");
        }

        commentList = new ArrayList<>();
        commentRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        deleteCommentsButton = view.findViewById(R.id.buttonDeleteSelectedComments);
        backButton = view.findViewById(R.id.back_button);


        userRepo.isAdmin(currentUid).addOnSuccessListener(isOrNot ->{
            isAdmin = isOrNot;
        }).addOnCompleteListener(task->displayALLComments());


        updateDeleteButton();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displayALLComments(){

        commentAdapter = new CommentAdapter(commentList, comment -> deleteComment(comment),deleteModeEnabled,userRepo);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentRecyclerView.setAdapter(commentAdapter);
        backButton.setOnClickListener(v -> returnToExpandedView());

        if(isAdmin){
            deleteCommentsButton.setOnClickListener(v -> changeDeleteMode());
        }
        else{
            deleteCommentsButton.setEnabled(false);
            deleteCommentsButton.setVisibility(View.GONE);
        }


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
            deleteCommentsButton.setBackgroundTintList(ColorStateList.valueOf(red));
            deleteCommentsButton.setTextColor(Color.WHITE);
            deleteCommentsButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
        }
        else {
            deleteCommentsButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            deleteCommentsButton.setTextColor(red);
            deleteCommentsButton.setIconTint(ColorStateList.valueOf(red));
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
                    Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                    displayALLComments();
                })
                .addOnFailureListener(error ->
                        Toast.makeText(requireContext(), "Could not delete comment", Toast.LENGTH_SHORT).show()
                );
    }

    private void returnToExpandedView() {

        if (currentLotNumber == null || currentLotNumber.trim().isEmpty()) {return;}
        backButton.setEnabled(false);
        ExpandedArtifactFragment expandedArtifactFragment = ExpandedArtifactFragment.newInstance(currentLotNumber);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, expandedArtifactFragment).commit();
        backButton.setEnabled(true);
    }
}

package com.example.cscb07project;
import com.google.android.material.button.MaterialButton;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07project.entities.Artifact;
import com.example.cscb07project.entities.Collection;
import com.example.cscb07project.entities.Comment;
import com.example.cscb07project.entities.ExpandedView;
import com.example.cscb07project.entities.User;
import com.example.cscb07project.repositories.ArtifactRepository;
import com.example.cscb07project.repositories.CollectionRepository;
import com.example.cscb07project.repositories.ExpandedViewRepository;
import com.example.cscb07project.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        db = mainActivity.getDatabase();
        expandedViewRepo = new ExpandedViewRepository(db);
        Bundle bun2 = getArguments();
        if (bun2 != null) {
            currentLotNumber = bun2.getString("lot_number");
        }
        commentList = new ArrayList<>();
        commentRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        commentAdapter = new CommentAdapter(commentList,(comment) -> this.deleteComment(comment));
        commentRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        commentRecyclerView.setAdapter(commentAdapter);
        displayALLComments();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void displayALLComments(){

        commentAdapter = new CommentAdapter(commentList, comment -> deleteComment(comment));

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentRecyclerView.setAdapter(commentAdapter);

        if(currentLotNumber==null){return;}
        expandedViewRepo.getCommentsByLotNumber(currentLotNumber).addOnSuccessListener( comments ->{
                    commentList.clear();
                    if(commentList!=null){
                        commentList.addAll(comments);
                    }
                    commentAdapter.notifyDataSetChanged();
                });


    }
    private void deleteComment(Comment comment) {
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
}

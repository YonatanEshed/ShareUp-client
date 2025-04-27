package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.CommentAdapter;
import com.shareup.application.ADPTERS.PostAdapter;
import com.shareup.application.R;
import com.shareup.model.Comment;
import com.shareup.viewmodel.CommentViewModel;

import java.util.ArrayList;
import java.util.List;

public class Comments extends BaseActivity {
    private CommentViewModel commentViewModel;

    private EditText etAddComment;
    private ImageButton ibSendComment;
    private RecyclerView rvComments;

    CommentAdapter commentAdapter;

    private String postId;

    private boolean isLoading = false;

    private Comment DeletedComment;
    private int DeletedCommentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_comments);
        getLayoutInflater().inflate(R.layout.activity_comments, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Comments");
        showBackButton();

        initializeViews();
        setViewModel();
        setAdapters();
        setListeners();
    }

    @Override
    protected void initializeViews() {
        // EditText
        etAddComment = findViewById(R.id.etAddComment);

        // ImageButtons
        ibSendComment = findViewById(R.id.ibSendComment);

        // RecyclerView
        rvComments = findViewById(R.id.rvComments);

        // Get postId from Intent
        postId = getIntent().getStringExtra("postId");
    }

    @Override
    protected void setListeners() {
        ibSendComment.setOnClickListener(view -> {
            String commentContent = etAddComment.getText().toString();

            if (!commentContent.isEmpty()) {
                commentViewModel.addComment(postId, commentContent);
                etAddComment.setText("");
            }
        });
    }

    private void setAdapters() {
        commentAdapter = new CommentAdapter(new ArrayList<>(), R.layout.single_comment,
                holder -> {
                    // Initialize ViewHolder
                    holder.putView("tvCommentUsername", holder.itemView.findViewById(R.id.tvCommentUsername));
                    holder.putView("ivCommentUserPicture", holder.itemView.findViewById(R.id.ivCommentUserPicture));
                    holder.putView("tvCommentContent", holder.itemView.findViewById(R.id.tvCommentContent));
                    holder.putView("ibDeleteComment", holder.itemView.findViewById(R.id.ibDeleteComment));
                    holder.putView("llCommentUserContainer", holder.itemView.findViewById(R.id.llCommentUserContainer));
                },
                (holder, item, position) -> {
                    Log.d("CommentAdapter", "Binding view holder for position: " + position);
                    // Bind ViewHolder
                    ((TextView)holder.getView("tvCommentUsername")).setText(item.getUser().getUsername());
                    ((TextView)holder.getView("tvCommentContent")).setText(item.getContent());

                    ImageButton ibDeleteComment = holder.getView("ibDeleteComment");
                    ibDeleteComment.setOnClickListener(view -> {
                        // prevent multiple delete requests if the delete request is already in progress
                        if (isLoading) {
                            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        commentViewModel.deleteComment(postId, item.getId());

                        // Store the deleted comment and its position
                        DeletedComment = item;
                        DeletedCommentPosition = position;

                        // Remove the comment from the adapter
                        List<Comment> commentsList = commentAdapter.getItems();
                        commentsList.remove(DeletedComment);
                        commentAdapter.setItems(commentsList);
                    });

                    if (item.getUser().getId().equals(getUserId())) {
                        ibDeleteComment.setVisibility(View.VISIBLE);
                    }

                    // Load profile picture using Glide
                    ImageView ivProfilePicture = holder.getView("ivCommentUserPicture");
                    Glide.with(getApplicationContext()).load(item.getUser().getProfilePicture()).into(ivProfilePicture);

                    LinearLayout llCommentUserContainer = holder.getView("llCommentUserContainer");
                    llCommentUserContainer.setOnClickListener(view -> {
                        // Open Profile Activity
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userId", item.getUser().getId());
                        startActivity(intent);
                    });
                });

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }

    @Override
    protected void setViewModel() {
        commentViewModel = new CommentViewModel(getApplication());

        commentViewModel.getDataList().observe(this, comments -> {
            commentAdapter.setItems(comments);
        });

        commentViewModel.getPostData().observe(this, comment -> {
            if (comment != null) {
                List<Comment> comments = commentAdapter.getItems();
                comments.add(comment);
                commentAdapter.setItems(comments);
            }
        });

        commentViewModel.getDeleteData().observe(this, success -> {
            if (!success) {
                List<Comment> comments = commentAdapter.getItems();
                comments.add(DeletedCommentPosition, DeletedComment);
                commentAdapter.setItems(comments);
            }
        });

        commentViewModel.getIsLoading(). observe(this, isLoading -> {
            this.isLoading = isLoading;
        });

        commentViewModel.getMessage(). observe(this, message -> {
            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        commentViewModel.getComments(postId);
    }
}
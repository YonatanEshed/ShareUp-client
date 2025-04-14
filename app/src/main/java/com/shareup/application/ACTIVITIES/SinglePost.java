package com.shareup.application.ACTIVITIES;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.R;
import com.shareup.viewmodel.PostViewModel;

public class SinglePost extends BaseActivity {
    PostViewModel postViewModel;

    TextView tvPostUsername, tvPostLikesCount, tvPostDescription;
    ImageView ivProfilePicture, ivPostPicture;
    ImageButton ibPostLike, ibPostComment;
    Button btnViewComments;

    String postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_single_post);
        getLayoutInflater().inflate(R.layout.activity_single_post, findViewById(R.id.content_frame));
        getLayoutInflater().inflate(R.layout.single_post, findViewById(R.id.flSinglePost)); // inflate the single_post into the activity layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setViewModel();
        setListeners();
    }

    @Override
    protected void initializeViews() {
        tvPostUsername = findViewById(R.id.tvPostUsername);
        tvPostLikesCount = findViewById(R.id.tvPostLikesCount);
        tvPostDescription = findViewById(R.id.tvPostDescription);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivPostPicture = findViewById(R.id.ivPostPicture);
        ibPostLike = findViewById(R.id.ibPostLike);
        ibPostComment = findViewById(R.id.ibPostComment);
        btnViewComments = findViewById(R.id.btnViewComments);

        // Get the postId from the intent
        postId = getIntent().getStringExtra("postId");
    }

    @Override
    protected void setListeners() {
        ibPostLike.setOnClickListener(v -> {
            // Handle like button click
//            postViewModel.likePost(postId);
            // change icon to filled heart
        });

        ibPostComment.setOnClickListener(v -> {
            // Open CommentActivity

        });

        btnViewComments.setOnClickListener(v -> {
            // Open CommentActivity
        });
    }

    @Override
    protected void setViewModel() {
        postViewModel = new PostViewModel(getApplication());

        postViewModel.getData().observe(this, post -> {
            if (post == null) {
                Toast.makeText(this, "Error fetching ", Toast.LENGTH_SHORT).show();
                return;
            }
            // Update UI with post data
            tvPostUsername.setText(post.getUser().getUsername());
            tvPostLikesCount.setText(post.getLikesCount() + " Likes");
            tvPostDescription.setText(post.getCaption());

            Glide.with(getApplicationContext()).load(post.getMediaURL()).into(ivPostPicture);
            if (post.getUser().getProfilePicture() != null) {
                Glide.with(getApplicationContext()).load(post.getUser().getProfilePicture()).into(ivProfilePicture);
            }
        });

        // Load the post data
        postViewModel.getPost(postId);
    }
}
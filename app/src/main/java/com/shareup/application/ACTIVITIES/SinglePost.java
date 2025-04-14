package com.shareup.application.ACTIVITIES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.R;
import com.shareup.viewmodel.LikeViewModel;
import com.shareup.viewmodel.PostViewModel;

public class SinglePost extends BaseActivity {
    PostViewModel postViewModel;
    LikeViewModel likeViewModel;

    TextView tvPostUsername, tvPostLikesCount, tvPostDescription;
    ImageView ivProfilePicture, ivPostPicture;
    ImageButton ibPostLike, ibPostComment;
    Button btnViewComments;
    LinearLayout llProfileContainer;

    String postId;
    String userId;

    boolean isLiked = false;
    int likesCount = 0;

    boolean holdLike = false; // to hold the like state while like request is in progress

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
        // TextViews
        tvPostUsername = findViewById(R.id.tvPostUsername);
        tvPostLikesCount = findViewById(R.id.tvPostLikesCount);
        tvPostDescription = findViewById(R.id.tvPostDescription);

        // ImageViews
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivPostPicture = findViewById(R.id.ivPostPicture);

        // ImageButtons
        ibPostLike = findViewById(R.id.ibPostLike);
        ibPostComment = findViewById(R.id.ibPostComment);

        // Button
        btnViewComments = findViewById(R.id.btnViewComments);

        // LinearLayout
        llProfileContainer = findViewById(R.id.llProfileContainer);

        // Get the postId from the intent
        postId = getIntent().getStringExtra("postId");
    }

    @Override
    protected void setListeners() {
        ibPostLike.setOnClickListener(v -> {
            if (holdLike) {
                return; // Prevent multiple clicks while the request is in progress
            }

            if (isLiked) {
                likeViewModel.unlikePost(postId);
                setLikeCount(likesCount - 1);
            } else {
                likeViewModel.likePost(postId);
                setLikeCount(likesCount + 1);
            }
            toggleLike();
        });

        ibPostComment.setOnClickListener(v -> {
            // Open CommentActivity

        });

        btnViewComments.setOnClickListener(v -> {
            // Open CommentActivity
        });

        llProfileContainer.setOnClickListener(v -> {
            Intent intent = new Intent(SinglePost.this, Profile.class);
            intent.putExtra("userId", userId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void setViewModel() {
        postViewModel = new PostViewModel(getApplication());
        likeViewModel = new LikeViewModel(getApplication());

        postViewModel.getData().observe(this, post -> {
            if (post == null) {
                Toast.makeText(this, "Error fetching ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update UI with post data
            tvPostUsername.setText(post.getUser().getUsername());
            tvPostDescription.setText(post.getCaption());

            // load post picture
            Glide.with(getApplicationContext()).load(post.getMediaURL()).into(ivPostPicture);

            // load profile picture
            if (post.getUser().getProfilePicture() != null) {
                Glide.with(getApplicationContext()).load(post.getUser().getProfilePicture()).into(ivProfilePicture);
            }

            if (post.isLiked()) {
                setLikeOn();
                isLiked = true;
            } else {
                setLikeOff();
                isLiked = false;
            }

            setLikeCount(post.getLikesCount());

            userId = post.getUser().getId();
        });

        likeViewModel.getData().observe(this, success -> {
            if (success == null || !success) {
                // toggle like back to previous state
                toggleLike();
            }
        });

        likeViewModel.getIsLoading().observe(this, isLoading -> {
            holdLike = isLoading;
        });

        // Load the post data
        postViewModel.getPost(postId);
    }

    private void toggleLike() {
        if (isLiked) {
            setLikeOff();
            isLiked = false;
        } else {
            setLikeOn();
            isLiked = true;
        }
    }

    private void setLikeOn() {
        ibPostLike.setImageResource(R.drawable.favorite_fill_24px);
    }

    private void setLikeOff() {
        ibPostLike.setImageResource(R.drawable.favorite_24px);
    }

    private void setLikeCount(int count) {
        likesCount = count;
        tvPostLikesCount.setText(count + " Likes");
    }
}
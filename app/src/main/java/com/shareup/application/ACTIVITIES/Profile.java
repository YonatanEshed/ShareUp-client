package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.ImageAdapter;
import com.shareup.application.R;
import com.shareup.model.Post;
import com.shareup.viewmodel.PostViewModel;
import com.shareup.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class Profile extends BaseActivity {
    ProfileViewModel profileViewModel;
    PostViewModel postViewModel;

    ImageAdapter postsAdapter;

    TextView tvPostsCount, tvFollowersCount, tvFollowingCount, tvProfileUsername, tvBio;
    Button btnProfileFollow, btnProfileMessage, btnEditProfile, btnProfileLogout;
    ImageView ivProfilePicture;
    LinearLayout profileButtons, profileButtonsOwn;
    RecyclerView rvProfilePosts;

    String userId;
    ArrayList<Post> profilePosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile);
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.content_frame));
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
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvBio = findViewById(R.id.tvBio);
        tvFollowingCount = findViewById(R.id.tvFollowingCount);
        tvFollowersCount = findViewById(R.id.tvFollowersCount);
        tvPostsCount = findViewById(R.id.tvPostsCount);

        // ImageViews
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        // Buttons
        btnProfileFollow = findViewById(R.id.btnProfileFollow);
        btnProfileMessage = findViewById(R.id.btnProfileMessage);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);

        // LinearLayouts
        profileButtons = findViewById(R.id.profileButtons);
        profileButtonsOwn = findViewById(R.id.profileButtonsOwn);

        // RecyclerView
        rvProfilePosts = findViewById(R.id.rvProfilePosts);


        postsAdapter = new ImageAdapter(this, profilePosts);
        rvProfilePosts.setLayoutManager(new GridLayoutManager(this, 3));
        rvProfilePosts.setAdapter(postsAdapter);

        // get user id
        userId = getIntent().getStringExtra("userId");
    }

    @Override
    protected void setListeners() {
        btnProfileFollow.setOnClickListener(view -> {
            // follow user
        });

        btnProfileMessage.setOnClickListener(view -> {
            // message user
        });

        btnEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, EditProfile.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnProfileLogout.setOnClickListener(view -> {
            logout();
        });


    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());
        postViewModel = new PostViewModel(getApplication());
        // obsereve data
        profileViewModel.getData().observe(this, profile -> {
            if (profile == null) {
                return;
            }

            tvProfileUsername.setText(profile.getUsername());
            tvBio.setText(profile.getBio());
            tvFollowingCount.setText(String.valueOf(profile.getFollowingCount()));
            tvFollowersCount.setText(String.valueOf(profile.getFollowersCount()));

            if (profile.getId().equals(userId)) {
                profileButtons.setVisibility(View.GONE);
                profileButtonsOwn.setVisibility(View.VISIBLE);
            } else {
                profileButtons.setVisibility(View.VISIBLE);
                profileButtonsOwn.setVisibility(View.GONE);
            }
        });

        profileViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        postViewModel.getDataList().observe(this, posts -> {
            tvPostsCount.setText(String.valueOf(posts.size()));

            profilePosts.clear();
            profilePosts.addAll(posts);
            postsAdapter.notifyDataSetChanged();

            Log.d("Profile", "Posts: " + profilePosts);
        });

        profileViewModel.getProfile(userId);
        postViewModel.getUserPosts(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileViewModel.getProfile(userId);
    }
}
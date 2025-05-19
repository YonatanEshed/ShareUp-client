package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.PostAdapter;
import com.shareup.application.R;
import com.shareup.viewmodel.FollowViewModel;
import com.shareup.viewmodel.PostViewModel;
import com.shareup.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileActivity extends BaseActivity {
    ProfileViewModel profileViewModel;
    PostViewModel postViewModel;
    FollowViewModel followViewModel;

    PostAdapter postsAdapter;

    TextView tvPostsCount, tvFollowersCount, tvFollowingCount, tvProfileUsername, tvBio;
    Button btnProfileFollow, btnEditProfile, btnProfileLogout;
    ImageView ivProfilePicture;
    LinearLayout profileButtons, profileButtonsOwn;
    ConstraintLayout clFollowers, clFollowing;
    RecyclerView rvProfilePosts;

    String userId;

    boolean isFollowed;
    int followersCount;

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
        setAdapters();
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
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);

        // LinearLayouts
        profileButtons = findViewById(R.id.profileButtons);
        profileButtonsOwn = findViewById(R.id.profileButtonsOwn);

        // ConstraintLayouts
        clFollowers = findViewById(R.id.clFollowers);
        clFollowing = findViewById(R.id.clFollowing);

        // RecyclerView
        rvProfilePosts = findViewById(R.id.rvProfilePosts);

        // get user id
        userId = getIntent().getStringExtra("userId");
    }

    @Override
    protected void setListeners() {
        btnProfileFollow.setOnClickListener(view -> {
            if (isFollowed)
                followViewModel.unfollowUser(userId);
            else
                followViewModel.followUser(userId);


            toggleFollow();
        });

        btnEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnProfileLogout.setOnClickListener(view -> {
            logout();
        });

        clFollowers.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileListActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("listType", ProfileListActivity.FOLLOWERS_LIST_TAG);
            startActivity(intent);
        });

        clFollowing.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileListActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("listType", ProfileListActivity.FOLLOWINGS_LIST_TAG);
            startActivity(intent);
        });

        postsAdapter.setOnItemClickListener((item, position) -> {
            // Open SinglePost activity
            Intent intent = new Intent(ProfileActivity.this, SinglePostActivity.class);
            intent.putExtra("postId", item.getId());
            startActivity(intent);
        });
    }

    protected void setAdapters() {
        postsAdapter = new PostAdapter(new ArrayList<>(), R.layout.post_overview,
                holder -> {
                    // Initialize ViewHolder
                    holder.putView("ivPostPicture", holder.itemView.findViewById(R.id.ivPostOverview));
                },
                (holder, item, position) -> {
                    // Bind ViewHolder
                    ImageView ivPostPicture = holder.getView("ivPostPicture");
                    Glide.with(getApplicationContext()).load(item.getMediaURL()).into(ivPostPicture);
                });

        postsAdapter.setOnItemClickListener((item, position) -> {
            // Open SinglePost activity
            Intent intent = new Intent(ProfileActivity.this, SinglePostActivity.class);
            intent.putExtra("postId", item.getId());
            startActivity(intent);
        });

        rvProfilePosts.setLayoutManager(new GridLayoutManager(this, 3));
        rvProfilePosts.setAdapter(postsAdapter);
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());
        postViewModel = new PostViewModel(getApplication());
        followViewModel = new FollowViewModel(getApplication());

        // obsereve data
        profileViewModel.getData().observe(this, profile -> {
            if (profile == null) {
                return;
            }

            setTitle(profile.getUsername());

            tvProfileUsername.setText(profile.getUsername());
            tvBio.setText(profile.getBio());
            tvFollowingCount.setText(String.valueOf(profile.getFollowingCount()));
            tvFollowersCount.setText(String.valueOf(profile.getFollowersCount()));

            if (profile.getProfilePicture() != null) {
                Glide.with(getApplicationContext()).load(profile.getProfilePicture()).into(ivProfilePicture);
            }

            if (getUserId().equals(profile.getId())) {
                profileButtons.setVisibility(View.GONE);
                profileButtonsOwn.setVisibility(View.VISIBLE);
            } else {
                profileButtons.setVisibility(View.VISIBLE);
                profileButtonsOwn.setVisibility(View.GONE);
            }

            followersCount = profile.getFollowersCount();
            isFollowed = profile.isFollowed();

            if (isFollowed) {
                setFollowTrue();
            } else {
                setFollowFalse();
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

            postsAdapter.setItems(posts);
        });

        followViewModel.getActionData().observe(this, success -> {
            if (!success) {
                toggleFollow();
            }
        });

        followViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getProfile(userId);
        postViewModel.getUserPosts(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileViewModel.getProfile(userId);
        postViewModel.getUserPosts(userId);
    }

    private void toggleFollow() {
        if (isFollowed) {
            setFollowFalse();
            isFollowed = false;
            followersCount -= 1;
            tvFollowersCount.setText(String.valueOf(followersCount));
        } else {
            setFollowTrue();
            isFollowed = true;
            followersCount += 1;
            tvFollowersCount.setText(String.valueOf(followersCount));
        }
    }

    private void setFollowFalse() {
        btnProfileFollow.setBackgroundResource(R.drawable.primary_button_bg);
        btnProfileFollow.setTextColor(getResources().getColor(R.color.white));
        btnProfileFollow.setText("Follow");
    }

    private void setFollowTrue() {
        btnProfileFollow.setBackgroundResource(R.drawable.secondary_button_bg);
        btnProfileFollow.setTextColor(getResources().getColor(R.color.gray3));
        btnProfileFollow.setText("Following");
    }
}
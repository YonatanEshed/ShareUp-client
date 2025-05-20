package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.ProfileAdapter;
import com.shareup.application.R;
import com.shareup.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileListActivity extends BaseActivity {
    public static final String LIKES_LIST_TAG = "likes_list";
    public static final String FOLLOWERS_LIST_TAG = "followers_list";
    public static final String FOLLOWINGS_LIST_TAG = "followings_list";

    private ProfileViewModel profileViewModel;

    private ProfileAdapter profileAdapter;

    RecyclerView rvProfiles;

    String listType;
    String userId;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile_list);
        getLayoutInflater().inflate(R.layout.activity_profile_list, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set header
        listType = getIntent().getStringExtra("listType");
        switch (listType) {
            case LIKES_LIST_TAG:
                setTitle("Likes");
                break;
            case FOLLOWERS_LIST_TAG:
                setTitle("Followers");
                break;
            case FOLLOWINGS_LIST_TAG:
                setTitle("Followings");
                break;
            default:
                Log.d("ProfileList", "Invalid list type");
                finish();
        }
        showBackButton();

        userId = getIntent().getStringExtra("userId");
        postId = getIntent().getStringExtra("postId");
        if (userId == null && (listType.equals(FOLLOWERS_LIST_TAG) || listType.equals(FOLLOWINGS_LIST_TAG))) {
            Log.d("ProfileList", "Invalid userId");
            finish();
        } else if (postId == null && listType.equals(LIKES_LIST_TAG)) {
            Log.d("ProfileList", "Invalid postId");
            finish();
        }

        initializeViews();
        setViewModel();
        setAdapters();
        setListeners();
    }

    @Override
    protected void initializeViews() {
        // RecyclerViews
        rvProfiles = findViewById(R.id.rvProfiles);
    }

    @Override
    protected void setListeners() {

    }

    protected void setAdapters() {
        profileAdapter = new ProfileAdapter(new ArrayList<>(), R.layout.single_profile,
                holder -> {
                    holder.putView("ivSingleProfilePicture", holder.itemView.findViewById(R.id.ivSingleProfilePicture));
                    holder.putView("tvSingleProfileUsername", holder.itemView.findViewById(R.id.tvSingleProfileUsername));
                },
                (holder, item, position) -> {

                    ((TextView)holder.getView("tvSingleProfileUsername")).setText(item.getUsername());

                    ImageView ivProfilePicture = holder.getView("ivSingleProfilePicture");
                    if (item.getProfilePicture() != null)
                        Glide.with(getApplicationContext()).load(item.getProfilePicture()).into(ivProfilePicture);
                    else
                        ivProfilePicture.setImageResource(R.drawable.ic_deafult_profile_picture);
                });

        profileAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userId", profileAdapter.getItem(position).getId());
            startActivity(intent);
        });

        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvProfiles.setAdapter(profileAdapter);
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());
        profileViewModel.getDataList().observe(this, profiles -> {
            profileAdapter.setItems(profiles);
        });

        switch (listType) {
            case LIKES_LIST_TAG:
                profileViewModel.getPostLikes(postId);
                break;
            case FOLLOWERS_LIST_TAG:
                profileViewModel.getUserFollowers(userId);
                break;
            case FOLLOWINGS_LIST_TAG:
                profileViewModel.getUserFollowings(userId);
                break;
            default:
                Log.d("ProfileList", "Invalid list type");
                finish();
        }
    }
}
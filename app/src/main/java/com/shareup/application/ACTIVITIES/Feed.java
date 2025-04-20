package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.PostAdapter;
import com.shareup.application.R;
import com.shareup.model.Post;
import com.shareup.viewmodel.LikeViewModel;
import com.shareup.viewmodel.PostViewModel;

import java.util.ArrayList;

public class Feed extends BaseActivity {
    public static final String HOME_FEED_TAG = "home_feed";
    public static final String SEARCH_FEED_TAG = "search_feed";

    PostViewModel postViewModel;
    LikeViewModel likeViewModel;

    PostAdapter postsAdapter;

    RecyclerView rvFeedPosts;

    Post post; // to hold the post of the post that was liked or unliked(while waiting for successful response)
    TextView tvLikedPostLikesCount; // to hold the likes count of the post that was liked or unliked(while waiting for successful response)
    ImageButton ibLikedPostLike; // to hold the like button of the post that was liked or unliked(while waiting for successful response)

    String feedType;

    boolean holdLike = false; // to hold the like state while like request is in progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_feed);
        getLayoutInflater().inflate(R.layout.activity_feed, findViewById(R.id.content_frame));
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
        rvFeedPosts = findViewById(R.id.rvFeedPosts);

        feedType = getIntent().getStringExtra("feedType");
        feedType = feedType == null ? SEARCH_FEED_TAG : feedType; // default to home feed
    }

    @Override
    protected void setListeners() {

    }

    protected void setAdapters() {
        postsAdapter = new PostAdapter(new ArrayList<>(), R.layout.single_post,
                holder -> {
                    // Initialize ViewHolder
                    holder.putView("tvPostUsername", holder.itemView.findViewById(R.id.tvPostUsername));
                    holder.putView("tvPostLikesCount", holder.itemView.findViewById(R.id.tvPostLikesCount));
                    holder.putView("tvPostDescription", holder.itemView.findViewById(R.id.tvPostDescription));

                    holder.putView("ivProfilePicture", holder.itemView.findViewById(R.id.ivProfilePicture));
                    holder.putView("ivPostPicture", holder.itemView.findViewById(R.id.ivPostPicture));

                    holder.putView("ibPostLike", holder.itemView.findViewById(R.id.ibPostLike));
                    holder.putView("ibPostComment", holder.itemView.findViewById(R.id.ibPostComment));
                    holder.putView("ibPostLike", holder.itemView.findViewById(R.id.ibPostLike));

                    holder.putView("btnViewComments", holder.itemView.findViewById(R.id.btnViewComments));

                    holder.putView("llProfileContainer", holder.itemView.findViewById(R.id.llProfileContainer));
                },
                (holder, item, position) -> {
                    ((TextView)holder.getView("tvPostUsername")).setText(item.getUser().getUsername());
                    ((TextView)holder.getView("tvPostDescription")).setText(item.getCaption());

                    setLikeCount(holder.getView("tvPostLikesCount"), item.getLikesCount());

                    // Load profile picture
                    ImageView ivProfilePicture = holder.getView("ivProfilePicture");
                    if (item.getUser().getProfilePicture() != null)
                        Glide.with(getApplicationContext()).load(item.getUser().getProfilePicture()).into(ivProfilePicture);

                    // Load post picture
                    ImageView ivPostPicture = holder.getView("ivPostPicture");
                    Glide.with(getApplicationContext()).load(item.getMediaURL()).into(ivPostPicture);

                    // Set like button state
                    ImageButton ibPostLike = holder.getView("ibPostLike");
                    if (item.isLiked()) {
                        setLikeOn(ibPostLike);
                    } else {
                        setLikeOff(ibPostLike);
                    }

                    ibPostLike.setOnClickListener(view -> {
                        // Check if like request is already in progress
                        if (holdLike) {
                            return;
                        }

                        // Perform like/unlike action
                        if (item.isLiked()) {
                            likeViewModel.unlikePost(item.getId());
                        } else {
                            likeViewModel.likePost(item.getId());
                        }

                        // Toggle like state
                        toggleLike(item, ibPostLike, holder.getView("tvPostLikesCount"));

                        post = item; // Store the post
                        tvLikedPostLikesCount = holder.getView("tvPostLikesCount"); // Store the likes count
                        ibLikedPostLike = holder.getView("ibPostLike"); // Store the like button
                    });

                    // assign listener to profile container
                    LinearLayout llProfileContainer = holder.getView("llProfileContainer");
                    llProfileContainer.setOnClickListener(view -> {
                        // Open Profile Activity
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userId", item.getUser().getId());
                        startActivity(intent);
                    });
                });

        postsAdapter.setOnItemClickListener((item, position) -> {
            // Open SinglePost activity`

        });

        rvFeedPosts.setLayoutManager(new LinearLayoutManager(this));
        rvFeedPosts.setAdapter(postsAdapter);
    }

    @Override
    protected void setViewModel() {
        postViewModel = new PostViewModel(getApplication());
        likeViewModel = new LikeViewModel(getApplication());

        postViewModel.getDataList().observe(this, posts -> {
            postsAdapter.setItems(posts);
        });

        likeViewModel.getActionData().observe(this, success -> {
            if (!success) {
                toggleLike(post, ibLikedPostLike, tvLikedPostLikesCount);
            }
        });

        likeViewModel.getIsLoading().observe(this, isLoading -> {
            holdLike = isLoading;
        });

        postViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        likeViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Set the feed type based on the intent extra
        if (feedType.equals(HOME_FEED_TAG)) {
            postViewModel.getPostsFollowingFeed();
        } else if (feedType.equals(SEARCH_FEED_TAG)) {
            postViewModel.getPostsFeed();
        } else {
            // default to search feed(all posts)
            postViewModel.getPostsFeed();
        }
    }

    private void toggleLike(Post post, ImageButton ibPostLike, TextView tvPostLikesCount) {
        if (post.isLiked()) {
            setLikeOff(ibPostLike);
            post.setLiked(false);
            post.setLikesCount(post.getLikesCount() - 1);
            setLikeCount(tvPostLikesCount, post.getLikesCount());
        } else {
            setLikeOn(ibPostLike);
            post.setLiked(true);
            post.setLikesCount(post.getLikesCount() + 1);
            setLikeCount(tvPostLikesCount, post.getLikesCount());
        }
    }

    private void setLikeOn(ImageButton ibPostLike) {
        ibPostLike.setImageResource(R.drawable.favorite_fill_24px);
    }

    private void setLikeOff(ImageButton ibPostLike) {
        ibPostLike.setImageResource(R.drawable.favorite_24px);
    }

    private void setLikeCount(TextView tvPostLikesCount,int count) {
        tvPostLikesCount.setText(count + " Likes");
    }
}
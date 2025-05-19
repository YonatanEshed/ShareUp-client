package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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

public class SearchActivity extends BaseActivity {
    private ProfileViewModel profileViewModel;

    private ProfileAdapter profileAdapter;

    private EditText etSearch;
    private RecyclerView rvSearch;

    // Debounce variables
    Handler handler = new Handler();
    long debounceDelay = 1000; // 1s delay
    Runnable searchRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_search);
        getLayoutInflater().inflate(R.layout.activity_search, findViewById(R.id.content_frame));
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
        // EditTexts
        etSearch = findViewById(R.id.etSearch);

        // RecyclerViews
        rvSearch = findViewById(R.id.rvSearch);
    }

    @Override
    protected void setListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel the previous search task
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Schedule a new search task
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        profileViewModel.searchUsers(query);
                    }
                };
                handler.postDelayed(searchRunnable, debounceDelay);
            }
        });
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
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userId", profileAdapter.getItem(position).getId());
            startActivity(intent);
        });

        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(profileAdapter);
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());
        profileViewModel.getDataList().observe(this, profiles -> {
            Log.d("jsdlkfjadslkj", profiles.toString());
            profileAdapter.setItems(profiles);
        });
    }
}
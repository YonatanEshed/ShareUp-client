package com.shareup.application.ACTIVITIES;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.ADPTERS.ActivityAdapter;
import com.shareup.application.ADPTERS.ProfileAdapter;
import com.shareup.application.R;
import com.shareup.viewmodel.ActivityViewModel;

import java.util.ArrayList;

public class ActivityList extends BaseActivity {

    private ActivityViewModel activityViewModel;

    private ActivityAdapter activityAdapter;

    private RecyclerView rvActivityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_activity_list);
        getLayoutInflater().inflate(R.layout.activity_activity_list, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Activity");

        initializeViews();
        setViewModel();
        setAdapters();
        setListeners();
    }

    @Override
    protected void initializeViews() {
        rvActivityList = findViewById(R.id.rvActivityList);
    }

    @Override
    protected void setListeners() {

    }

    protected void setAdapters() {
        activityAdapter = new ActivityAdapter(new ArrayList<>(), R.layout.single_activity_item,
                holder -> {
                    holder.putView("ivActivityIcon", holder.itemView.findViewById(R.id.ivActivityIcon));
                    holder.putView("tvActivityMessage", holder.itemView.findViewById(R.id.tvActivityMessage));
                },
                (holder, item, position) -> {

                    ((TextView)holder.getView("tvActivityMessage")).setText(item.getMessage());

                    ImageView ivActivityIcon = holder.getView("ivActivityIcon");
                    switch (item.getType()) {
                        case 0: // follow
                            ivActivityIcon.setImageResource(R.drawable.person_check_24px);
                            break;
                        case 1: // like
                            ivActivityIcon.setImageResource(R.drawable.favorite_24px);
                            break;
                        case 2: // new post
                            ivActivityIcon.setImageResource(R.drawable.imagesmode_24px);
                            break;
                        case 3: // comment
                            ivActivityIcon.setImageResource(R.drawable.chat_bubble_24px);
                            break;
                        default:
                            ivActivityIcon.setImageResource(R.drawable.notifications_24px);
                    }
                });

        rvActivityList.setLayoutManager(new LinearLayoutManager(this));
        rvActivityList.setAdapter(activityAdapter);
    }

    @Override
    protected void setViewModel() {
        activityViewModel = new ActivityViewModel(getApplication());

        activityViewModel.getDataList().observe(this, activityList -> {
            if (activityList != null) {
                activityAdapter.setItems(activityList);
            }
        });

        activityViewModel.getUserActivity(getUserId());
    }
}
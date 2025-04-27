package com.shareup.application.ACTIVITIES.BASE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.shareup.application.ACTIVITIES.Feed;
import com.shareup.application.ACTIVITIES.Login;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shareup.application.ACTIVITIES.Profile;
import com.shareup.application.ACTIVITIES.UploadPost;
import com.shareup.application.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setMenu();
    }

    protected abstract void initializeViews();
    protected abstract void setListeners();
    protected abstract void setViewModel();

    //region Loading
    public void showLoading() {
        LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(LinearLayout.VISIBLE);
    }

    public void hideLoading() {
        LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(LinearLayout.GONE);
    }
    //endregion

    //region NAV_BAR
    protected BottomNavigationView bottomNavigationView;

    private void setMenu(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home){
                    Intent intent = new Intent(BaseActivity.this, Feed.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("feedType", Feed.HOME_FEED_TAG);
                    startActivity(intent);

                } else if(itemId == R.id.navigation_search){
                    Intent intent = new Intent(BaseActivity.this, Feed.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("feedType", Feed.SEARCH_FEED_TAG);
                    startActivity(intent);

                } else if(itemId == R.id.navigation_upload){
                    Intent intent = new Intent(BaseActivity.this, UploadPost.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else if(itemId == R.id.navigation_activity){
                    Toast.makeText(BaseActivity.this, "Not Implement", Toast.LENGTH_SHORT).show();
                } else if(itemId == R.id.navigation_profile){
                    Intent intent = new Intent(BaseActivity.this, Profile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userId", getUserId());
                    startActivity(intent);
                }

                return false;
            }
        });
    }

    protected void hideMenu(){
        bottomNavigationView.setVisibility(BottomNavigationView.GONE);
    }


    protected void setTitle(String title){
        TextView tvPageTitle = findViewById(R.id.tvPageTitle);
        tvPageTitle.setText(title);
        showHeader();
    }

    protected void showHeader() {
        LinearLayout header = findViewById(R.id.llPageHeader);
        header.setVisibility(LinearLayout.VISIBLE);
    }

    protected void hideHeader() {
        LinearLayout header = findViewById(R.id.llPageHeader);
        header.setVisibility(LinearLayout.GONE);
    }

    protected void showBackButton() {
        ImageButton backButton = findViewById(R.id.ibBackButton);
        backButton.setVisibility(LinearLayout.VISIBLE);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    protected void setSelectedNavigationItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }

    protected String getUserId() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    protected void logout() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");
        editor.remove("user_id");
        editor.apply();

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
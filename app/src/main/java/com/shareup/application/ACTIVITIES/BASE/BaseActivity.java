package com.shareup.application.ACTIVITIES.BASE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.il.yonatan.core.SessionManager;
import com.shareup.application.ACTIVITIES.ActivityList;
import com.shareup.application.ACTIVITIES.Feed;
import com.shareup.application.ACTIVITIES.Login;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shareup.application.ACTIVITIES.Profile;
import com.shareup.application.ACTIVITIES.UploadPost;
import com.shareup.application.R;
import com.shareup.viewmodel.AuthViewModel;
import com.shareup.viewmodel.FcmTokenViewModel;

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
        
        // handle invalid tokens
        SessionManager.getInstance().getForceLogout().observe(this, event -> {
            if (event != null && event.markHandled()) {
                Log.d("force logout", "Force logout triggered");
                logout(true);
            }
        });
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
                    Intent intent = new Intent(BaseActivity.this, ActivityList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

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

    // Region Header
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

    protected void showHeaderButton(int resId) {
        ImageButton headerButton = findViewById(R.id.ibHeaderButton);
        headerButton.setVisibility(LinearLayout.VISIBLE);
        headerButton.setImageResource(resId);
    }

    protected void setHeaderButtonOnClickListener(View.OnClickListener onClickListener) {
        ImageButton headerButton = findViewById(R.id.ibHeaderButton);
        headerButton.setOnClickListener(onClickListener);
    }
    //endregion

    protected String getUserId() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    protected void logout() {
        logout(false);
    }

    protected void logout(boolean isForced) {
        // delete current FCM token
        FirebaseMessaging.getInstance().deleteToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "FCM token deleted on logout");
                    } else {
                        Log.e("FCM", "Failed to delete FCM token", task.getException());
                    }
                });

        // clear FCM token from server
        FcmTokenViewModel fcmTokenViewModel = new FcmTokenViewModel(getApplication());

        // if the logout is forced, we don't need to clear the token from the server
        if (!isForced)
            fcmTokenViewModel.clearFcmToken();

        fcmTokenViewModel.getDeleteData().observe(this, success -> {
            if (success) {
                // clear user data
                SharedPreferences sharedPreferences = getApplication().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("jwt_token");
                editor.remove("user_id");
                editor.apply();

                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Log.e("FCM", "Failed to clear FCM token from server");
            }
        });
    }

    protected void login(String token, String userId) {

        AuthViewModel authViewModel = new AuthViewModel(getApplication());

        authViewModel.saveLogin(token, userId);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String newToken = task.getResult();
                        Log.d("FCM", "New FCM token on login: " + newToken);

                        // Send this token to your server
                        FcmTokenViewModel fcmTokenViewModel = new FcmTokenViewModel(getApplication());
                        fcmTokenViewModel.setFcmToken(token);
                    } else {
                        Log.e("FCM", "Failed to get FCM token", task.getException());
                    }
                });

        requestPermission(Manifest.permission.POST_NOTIFICATIONS, () -> {
            Intent intent = new Intent(this, Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

    }

    // Region Permissions
    private Runnable permissionCallback;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted",Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications without granting permission",
                            Toast.LENGTH_LONG).show();
                }

                if (permissionCallback != null) {
                    permissionCallback.run();
                    permissionCallback = null; // Clear the callback after execution
                }
            });

    protected void requestPermission(String permission) {
        requestPermission(permission, null);
    }

    protected void requestPermission(String permission, Runnable onPermissionGranted) {
        this.permissionCallback = onPermissionGranted;

        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Log.d("Permission", "Permission already granted: " + permission);

                if (permissionCallback != null) {
                    permissionCallback.run();
                    permissionCallback = null; // Clear the callback after execution
                }
            } else {
                // Directly ask for the permission
                Log.d("Permission", "Requesting permission: " + permission);
                requestPermissionLauncher.launch(permission);
            }
        } else {
            if (permissionCallback != null) {
                permissionCallback.run();
                permissionCallback = null; // Clear the callback after execution
            }
        }
    }
    //endregion
}
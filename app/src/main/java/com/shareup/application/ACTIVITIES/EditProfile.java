package com.shareup.application.ACTIVITIES;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.R;
import com.shareup.viewmodel.ProfileViewModel;

public class EditProfile extends BaseActivity {
    ProfileViewModel profileViewModel;

    Button btnEditProfileSave;
    EditText etEditProfileUsername, etEditProfileBio;
    ImageButton ibEditProfilePicture;
    TextView tvEditProfileError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_edit_profile);
        getLayoutInflater().inflate(R.layout.activity_edit_profile, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }

    @Override
    protected void initializeViews() {
        // Buttons
        btnEditProfileSave = findViewById(R.id.btnEditProfileSave);

        // EditTexts
        etEditProfileUsername = findViewById(R.id.etEditProfileUsername);
        etEditProfileBio = findViewById(R.id.etEditProfileBio);

        // ImageButtons
//        ibEditProfilePicture = findViewById(R.id.ibEditProfilePicture);

        // TextViews
        tvEditProfileError = findViewById(R.id.tvEditProfileError);


        setViewModel();
        setListeners();
    }

    @Override
    protected void setListeners() {
        btnEditProfileSave.setOnClickListener(v -> {
            String username = etEditProfileUsername.getText().toString();
            String bio = etEditProfileBio.getText().toString();

            profileViewModel.updateProfile(username, bio);
        });
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());

        profileViewModel.getData().observe(this, profile -> {
            if (profile != null) {
                if (profile.getServerMessage() != null) {
                    tvEditProfileError.setText(profile.getServerMessage());
                    return;
                }

                etEditProfileUsername.setText(profile.getUsername());
                etEditProfileBio.setText(profile.getBio());

            } else {
                tvEditProfileError.setText("An error occurred");
            }
        });

        profileViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        profileViewModel.getProfile(getUserId());
    }
}
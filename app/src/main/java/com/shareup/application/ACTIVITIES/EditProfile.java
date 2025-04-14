package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.media.MediaParser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.R;
import com.shareup.helper.FileUtil;
import com.shareup.viewmodel.ProfileViewModel;

import java.io.File;
import java.io.IOException;

public class EditProfile extends BaseActivity {
    ProfileViewModel profileViewModel;

    Button btnEditProfileSave;
    EditText etEditProfileUsername, etEditProfileBio;
    ImageButton ibEditProfilePicture;
    TextView tvEditProfileError;

    Uri newProfilePictureUri;

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
        ibEditProfilePicture = findViewById(R.id.ibEditProfilePicture);

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

            if (newProfilePictureUri != null) {
                try {
                    File image = FileUtil.convertUriToPngFile(this, newProfilePictureUri);
                    profileViewModel.updateProfile(username, bio, image);
                } catch (IOException e) {
                    tvEditProfileError.setText("Error getting image");
                }
            } else {
                profileViewModel.updateProfile(username, bio);
            }
        });

        ibEditProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());

        profileViewModel.getData().observe(this, profile -> {
            if (profile != null) {
                etEditProfileUsername.setText(profile.getUsername());
                etEditProfileBio.setText(profile.getBio());

                if (profile.getProfilePicture() != null) {
                    Glide.with(getApplicationContext()).load(profile.getProfilePicture()).into(ibEditProfilePicture);
                }
            }
        });

        profileViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                tvEditProfileError.setText(message);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Handle the image selection
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ibEditProfilePicture.setImageURI(selectedImageUri);
                newProfilePictureUri = selectedImageUri;
            }
        }
    }
}
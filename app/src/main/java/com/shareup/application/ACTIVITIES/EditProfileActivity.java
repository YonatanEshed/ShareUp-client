package com.shareup.application.ACTIVITIES;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
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

public class EditProfileActivity extends BaseActivity {
    ProfileViewModel profileViewModel;

    Button btnEditProfileSave;
    EditText etEditProfileUsername, etEditProfileBio;
    ImageButton ibEditProfilePicture;
    TextView tvEditProfileError;

    Uri newProfilePictureUri;
    Uri cameraImageUri;

    ActivityResultLauncher<String> galleryLauncher;
    ActivityResultLauncher<Uri> cameraLauncher;

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

        setTitle("Edit Profile");

        initializeViews();
        setViewModel();
        setListeners();
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


        // Initialize ActivityResultLaunchers
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                ibEditProfilePicture.setImageURI(uri);
                newProfilePictureUri = uri;
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success && cameraImageUri != null) {
                ibEditProfilePicture.setImageURI(cameraImageUri);
                newProfilePictureUri = cameraImageUri;
            }
        });
    }

    @Override
    protected void setListeners() {
        btnEditProfileSave.setOnClickListener(v -> {
            String username = etEditProfileUsername.getText().toString();
            String bio = etEditProfileBio.getText().toString();

            if (username.isEmpty() || !username.matches(".*[a-zA-Z0-9].*")) {
                tvEditProfileError.setText("Username cannot be empty");
                return;
            }

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
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("Gallery");
            popupMenu.getMenu().add("Camera");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Gallery")) {
                    requestPermission("android.permission.READ_EXTERNAL_STORAGE", () -> {
                        galleryLauncher.launch("image/*");
                    });
                } else if (item.getTitle().equals("Camera")) {
                    requestPermission("android.permission.CAMERA", () -> {
                        File imageFile = new File(getExternalFilesDir(null), "temp_image.jpg");
                        cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
                        cameraLauncher.launch(cameraImageUri);
                    });
                }
                return true;
            });

            popupMenu.show();
        });
    }

    @Override
    protected void setViewModel() {
        profileViewModel = new ProfileViewModel(getApplication());

        profileViewModel.getData().observe(this, profile -> {
            if (profile != null) {
                etEditProfileUsername.setText(profile.getUsername());
                etEditProfileBio.setText(profile.getBio());

                if (profile.getProfilePicture() != null)
                    Glide.with(getApplicationContext()).load(profile.getProfilePicture()).into(ibEditProfilePicture);
            }
        });

        profileViewModel.getUpdateData().observe(this, profile -> {
            if (profile != null) {
                finish();
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
}
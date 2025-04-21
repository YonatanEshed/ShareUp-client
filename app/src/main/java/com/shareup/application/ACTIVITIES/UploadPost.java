package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;
import com.shareup.application.R;
import com.shareup.helper.FileUtil;
import com.shareup.viewmodel.PostViewModel;

import java.io.File;
import java.io.IOException;

public class UploadPost extends BaseActivity {
    PostViewModel postViewModel;

    ImageView ibUploadPostPicture;
    EditText etUploadPostDescription;
    Button btnUploadPostUpload, btnUploadPostPublish;
    TextView tvUploadPostError;

    String postId;

    Uri postPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_upload_post);
        getLayoutInflater().inflate(R.layout.activity_upload_post, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setViewModel();
        setListeners();
    }

    @Override
    protected void initializeViews() {
        // ImageViews
        ibUploadPostPicture = findViewById(R.id.ibUploadPostPicture);

        // EditTexts
        etUploadPostDescription = findViewById(R.id.etUploadPostDescription);

        // Buttons
        btnUploadPostUpload = findViewById(R.id.btnUploadPostUpload);
        btnUploadPostPublish = findViewById(R.id.btnUploadPostPublish);

        // TextViews
        tvUploadPostError = findViewById(R.id.tvUploadPostError);

        postId = getIntent().getStringExtra("postId");

        // Set the title based on whether it's an update or a new post
        if (postId != null) {
            btnUploadPostPublish.setText("Update");
            btnUploadPostUpload.setVisibility(View.GONE);
        } else {
            btnUploadPostPublish.setText("Publish");
            btnUploadPostUpload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListeners() {
        btnUploadPostUpload.setOnClickListener(v -> {
             Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        btnUploadPostPublish.setOnClickListener(v -> {
            // Handle publish button click
            String caption = etUploadPostDescription.getText().toString();

            if (postId != null) {
                postViewModel.updatePost(postId, caption);
            } else {
                if (postPicture == null) {
                    tvUploadPostError.setText("You must upload image to update post");
                    return;
                }

                try {
                    File image = FileUtil.convertUriToPngFile(this, postPicture);
                    postViewModel.uploadPost(caption, image);
                } catch (IOException e) {
                    tvUploadPostError.setText("Error getting image");
                }
            }
        });
    }

    @Override
    protected void setViewModel() {
        postViewModel = new PostViewModel(getApplication());

        postViewModel.getData().observe(this, post -> {
            if (post != null) {
                // Update UI with post data
                etUploadPostDescription.setText(post.getCaption());

                // Load image using Glide or any other image loading library
                 Glide.with(this).load(post.getMediaURL()).into(ibUploadPostPicture);
            }
        });

        postViewModel.getPostData(). observe(this, post -> {
            if (post != null) {
                finish();
            }
        });

        postViewModel.getUpdateData(). observe(this, post -> {
            if (post != null) {
                finish();
            }
        });

        postViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                tvUploadPostError.setText(message);
            }
        });

        if (postId != null) {
            postViewModel.getPost(postId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Handle the image selection
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ibUploadPostPicture.setImageURI(selectedImageUri);
                postPicture = selectedImageUri;
            }
        }
    }
}
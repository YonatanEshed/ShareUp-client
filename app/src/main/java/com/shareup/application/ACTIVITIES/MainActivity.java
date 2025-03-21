package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.shareup.application.R;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.shareup.application.ACTIVITIES.BASE.BaseActivity;

public class MainActivity extends BaseActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }

    @Override
    protected void initializeViews() {
        button = findViewById(R.id.button1);

        setListeners();
    }

    @Override
    protected void setListeners() {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        });
    }

    @Override
    protected void setViewModel() {

    }
}
package com.shareup.application.ACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shareup.application.R;
import com.shareup.viewmodel.AuthViewModel;

public class Register extends AppCompatActivity {
    AuthViewModel authViewModel;

    Button btnRegister, btnRegisterToLogin;
    EditText etRegisterEmail, etRegisterUsername, etRegisterPassword;
    TextView tvRegisterError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }


    protected void initializeViews() {
        // Buttons
        btnRegister = findViewById(R.id.btnRegister);
        btnRegisterToLogin = findViewById(R.id.btnRegisterToLogin);

        // EditTexts
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

        // TextViews
        tvRegisterError = findViewById(R.id.tvRegisterError);

        setViewModel();
        setListeners();
    }

    protected void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etRegisterEmail.getText().toString();
                String username = etRegisterUsername.getText().toString();
                String password = etRegisterPassword.getText().toString();

                // add validations


                authViewModel.register(email, username, password);
            }
        });
    }

    protected void setViewModel() {
        authViewModel = new AuthViewModel(getApplication());

        authViewModel.getData().observe(this, authResponse -> {
            Log.d("lskd", authResponse.toString());
            if (authResponse != null) {
                if (authResponse.getServerMessage() != null && authResponse.getToken() == null) {
                    tvRegisterError.setText(authResponse.getServerMessage());
                    return;
                }
                authViewModel.saveLogin(authResponse.getToken(), authResponse.getUserId());

                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                Log.d("Data", authResponse.getToken() + " - " + authResponse.getUserId());

            } else {
                tvRegisterError.setText("An Error Occurred. please try again"); // TODO: Show message returned by the API
            }
        });
    }
}
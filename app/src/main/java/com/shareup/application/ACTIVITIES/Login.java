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

public class Login extends AppCompatActivity {
    AuthViewModel authViewModel;

    Button btnLogin, btnLoginToRegister;
    EditText etLoginEmail, etLoginPassword;
    TextView tvLoginError;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }


    protected void initializeViews() {
        // Buttons
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginToRegister = findViewById(R.id.btnLoginToRegister);

        // EditTexts
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        // TextViews
        tvLoginError = findViewById(R.id.tvLoginError);

        setViewModel();
        setListeners();
    }

    protected void setListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etLoginEmail.getText().toString();
                String password = etLoginPassword.getText().toString();


                // add validations


                authViewModel.login(email, password);
            }
        });

        btnLoginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    protected void setViewModel() {
        authViewModel = new AuthViewModel(getApplication());

        authViewModel.getData().observe(this, authResponse -> {
            if (authResponse != null) {
                if (authResponse.getServerMessage() != null && authResponse.getToken() == null) {
                    tvLoginError.setText(authResponse.getServerMessage());
                    return;
                }
                authViewModel.saveLogin(authResponse.getToken(), authResponse.getUserId());

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            } else {
                tvLoginError.setText("An Error Occurred. please try again");
            }
        });

        // skip login if already logged in
        if (authViewModel.isLoggedIn()) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
package com.shop.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialCheckBox rememberMeCheckbox;
    private MaterialButton loginButton;
    private MaterialButton googleLoginButton;
    private MaterialButton appleLoginButton;
    private TextView forgotPasswordText;
    private TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        appleLoginButton = findViewById(R.id.appleLoginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signUpText = findViewById(R.id.signUpText);
    }

    private void setupClickListeners() {
        // Login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Forgot password click
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });

        // Sign up click
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Google login click
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGoogleLogin();
            }
        });

        // Apple login click
        appleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAppleLogin();
            }
        });
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        boolean rememberMe = rememberMeCheckbox.isChecked();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Please enter email or username");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Please enter password");
            passwordInput.requestFocus();
            return;
        }

        // TODO: Implement actual login logic here
        // For now, just show a success message
        Toast.makeText(this, "Login successful! Welcome to English Adventure!", 
                Toast.LENGTH_SHORT).show();

        // Navigate to HomeActivity after successful login
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        Toast.makeText(this, "Forgot Password clicked - Feature coming soon!", 
                Toast.LENGTH_SHORT).show();
    }

    private void handleSignUp() {
        // TODO: Navigate to sign up screen
        Toast.makeText(this, "Sign Up clicked - Feature coming soon!", 
                Toast.LENGTH_SHORT).show();
    }

    private void handleGoogleLogin() {
        // TODO: Implement Google Sign-In
        Toast.makeText(this, "Google Login - Feature coming soon!", 
                Toast.LENGTH_SHORT).show();
    }

    private void handleAppleLogin() {
        // TODO: Implement Apple Sign-In
        Toast.makeText(this, "Apple Login - Feature coming soon!", 
                Toast.LENGTH_SHORT).show();
    }
}

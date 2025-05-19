package com.example.taskmanagement;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.etResetEmail);
        resetPasswordButton = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);
        ImageView backButton = findViewById(R.id.backToLogin);

        resetPasswordButton.setOnClickListener(v -> resetPassword());
        backButton.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setVisibility(View.GONE);

        try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this,
                                    "Password reset link sent to your email",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ForgotPassword.this,
                                    "Failed to send reset email. " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("ForgotPassword", "Error: " + task.getException().getMessage());
                        }
                    });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            resetPasswordButton.setVisibility(View.VISIBLE);
            Toast.makeText(ForgotPassword.this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("ForgotPassword", "Exception: " + e.getMessage());
        }
    }
}
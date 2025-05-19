package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePassword extends AppCompatActivity {
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnUpdatePassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the EditText fields and Button
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        progressBar = findViewById(R.id.progressBar);

        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void updatePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Check if any field is empty
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if new password is at least 8 characters
        if (newPassword.length() < 8) {
            Toast.makeText(this, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnUpdatePassword.setVisibility(View.GONE);

        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Re-authenticate user before changing password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User re-authenticated, now update password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        btnUpdatePassword.setVisibility(View.VISIBLE);

                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(changePassword.this,
                                                    "Password updated successfully",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(changePassword.this, Userdetails.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(changePassword.this,
                                                    "Failed to update password: " + updateTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnUpdatePassword.setVisibility(View.VISIBLE);
                            Toast.makeText(changePassword.this,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

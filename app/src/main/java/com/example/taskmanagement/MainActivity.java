package com.example.taskmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText email,password;
    Button loginbtn;
    ProgressBar progressBar;
    TextView forgetPassword;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView signUpTextView = findViewById(R.id.signinpage);
        database = FirebaseDatabase.getInstance().getReference("Users");
        // Set click listener
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);

            }
        });
        loginbtn = findViewById(R.id.login);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar= findViewById(R.id.progress_bar);
        forgetPassword = findViewById(R.id.forgetpassword);

        loginbtn.setOnClickListener(v -> loginUser());

        forgetPassword.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MainActivity", "Error starting ForgotPassword: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void loginUser() {
        String Email = email.getText().toString();
        String Password = password.getText().toString();

        boolean isValidated = validateData(Email, Password);  //validation is true


        if(!isValidated) {
            return;
        }
       //login in firebase
        loginAccountInFirebase(Email,Password);

    }
void loginAccountInFirebase(String Email, String Password) {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    changeInProgress(true);

    firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            changeInProgress(false);

            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    if (user.isEmailVerified()) {
                        // Login successful

                        startActivity(new Intent(MainActivity.this, Homepage.class));
                        Utility.isfreshlogin = true;
                        finish();
                    }
                    else {
                        // Resend verification email
                        user.sendEmailVerification()
                                .addOnSuccessListener(aVoid -> {
                                    Utility.showToast(MainActivity.this, "Email not verified. Verification link sent again.");
                                })
                                .addOnFailureListener(e -> {
                                    Utility.showToast(MainActivity.this,"Failed to send verification email: " + e.getMessage());
                                });
                    }
                }
            } else {
                Utility.showToast(MainActivity.this, task.getException().getLocalizedMessage());
            }
        }
    });

}
    void changeInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData (String Email,String Password) {

        if(Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Email is invalid. Please, provide a valid email.");
            email.requestFocus();
            return false;
        }

        if (Password.isEmpty() || password.length() < 8) {
            password.setError("Invalid password");
            password.requestFocus();
            return false;
        }

        return true;
    }


}
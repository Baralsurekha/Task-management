package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Userdetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_userdetails);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI components
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvFirstName = findViewById(R.id.tvfirstname);
        TextView tvLastName = findViewById(R.id.tvlastname);
        TextView tvAddress = findViewById(R.id.tvaddress);
        TextView tvEmail = findViewById(R.id.tvemail);

        // Getting user info from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            tvEmail.setText(email);

            // Getting additional user details from Firestore
            FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Set the user's full name, first name, last name, and address from Firestore
                            tvUserName.setText(documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName"));
                            tvFirstName.setText(documentSnapshot.getString("firstName"));
                            tvLastName.setText(documentSnapshot.getString("lastName"));
                            tvAddress.setText(documentSnapshot.getString("address"));
                        }
                    });
        }

        // Edit Profile TextView click
        TextView tvEditProfile = findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(v -> {
            // Passing user details to Edit profile activity using Intent extras
            if (currentUser != null) {
                Intent intent = new Intent(Userdetails.this, Editprofile.class);

                // Passing user info as Intent extras
                intent.putExtra("firstName", tvFirstName.getText().toString());
                intent.putExtra("lastName", tvLastName.getText().toString());
                intent.putExtra("address", tvAddress.getText().toString());
                intent.putExtra("email", currentUser.getEmail());

                // Start Edit profile activity
                startActivity(intent);
            }
        });

        // Change Password Button click
        Button btnChangePassword = findViewById(R.id.btnUpdateDetails);
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(Userdetails.this, changePassword.class);
            startActivity(intent);
        });
    }
}

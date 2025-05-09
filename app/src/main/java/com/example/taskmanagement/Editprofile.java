package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Editprofile extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editAddress;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // making email (non-editable)
        editEmail.setEnabled(false);
        editEmail.setFocusable(false);

        // for  Firebase instances
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // fields from Intent
        Intent intent = getIntent();
        editFirstName.setText(intent.getStringExtra("firstName"));
        editLastName.setText(intent.getStringExtra("lastName"));
        editAddress.setText(intent.getStringExtra("address"));
        editEmail.setText(intent.getStringExtra("email"));

        // Save button
        btnSaveProfile.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String address = editAddress.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser != null) {
                String userId = currentUser.getUid();
                Map<String, Object> updates = new HashMap<>();
                updates.put("firstName", firstName);
                updates.put("lastName", lastName);
                updates.put("address", address);

                firestore.collection("users").document(userId)
                        .update(updates)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Userdetails.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });
    }
}

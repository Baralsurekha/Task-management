package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Userdetails extends AppCompatActivity {

    private TextView tvUserName, tvFirstName, tvLastName, tvAddress, tvEmail;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;

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

        tvUserName = findViewById(R.id.tvUserName);
        tvFirstName = findViewById(R.id.tvfirstname);
        tvLastName = findViewById(R.id.tvlastname);
        tvAddress = findViewById(R.id.tvaddress);
        tvEmail = findViewById(R.id.tvemail);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        loadUserData();

        findViewById(R.id.tvEditProfile).setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(Userdetails.this, Editprofile.class);
                intent.putExtra("firstName", tvFirstName.getText().toString());
                intent.putExtra("lastName", tvLastName.getText().toString());
                intent.putExtra("address", tvAddress.getText().toString());
                intent.putExtra("email", currentUser.getEmail());
                startActivity(intent);
            }
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Userdetails.this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());

            firestore.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String firstName = document.getString("firstName");
                                    String lastName = document.getString("lastName");
                                    String address = document.getString("address");

                                    tvUserName.setText(firstName + " " + lastName);
                                    tvFirstName.setText(firstName);
                                    tvLastName.setText(lastName);
                                    tvAddress.setText(address);
                                }
                            } else {
                                Toast.makeText(Userdetails.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

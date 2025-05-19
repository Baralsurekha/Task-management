package com.example.taskmanagement;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Debug;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {

    EditText firstName,lastName,email,password,confirmPassword;
    Button createAccountbtn;
    ProgressBar progressBar;

    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView loginTextView = findViewById(R.id.loginpage);
        database = FirebaseDatabase.getInstance().getReference("Users");
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

        });

        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        email= findViewById(R.id.email);
        password = findViewById(R.id.pword);
        confirmPassword = findViewById(R.id.cpword);
        createAccountbtn= findViewById(R.id.button);
        progressBar= findViewById(R.id.progress_bar);

        createAccountbtn.setOnClickListener(v -> createAccount());
    }

    void createAccount() {
        //logic
        String FirstName = firstName.getText().toString();
        String LastName = lastName.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        String ConfirmPassword = confirmPassword.getText().toString();

        boolean isValidated = validateData(FirstName, LastName, Email, Password, ConfirmPassword);  //validation is true

        //if validation is false, it will return.
        if(!isValidated) {
            return;
        }
        //creating Account in firebase
        createAccountInFirebase(FirstName,LastName,Email,Password);


    }
    void createAccountInFirebase(String FirstName,String LastName,String Email,String Password){

        changeInProgress(true);

        //accessing class for authentication
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(Registration.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

            Utility.showToast(Registration.this,"Account created successfully. Please, verify your email.");

                            firebaseAuth.getCurrentUser().sendEmailVerification();

                            User newUser = new User(Email,FirstName,LastName,"");

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(firebaseAuth.getCurrentUser().getUid()).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> tasks) {

                                            if(tasks.isSuccessful()) {
                                                changeInProgress(false);
                                                firebaseAuth.signOut();
                                                startActivity(new Intent(Registration.this, MainActivity.class));
                                                finish();

                                            } else {

                                                Log.e("Sucess","DBError... "+tasks.getException());
                                            }
                                        }
                                    });

                        } else {
                            Utility.showToast(Registration.this,task.getException().getLocalizedMessage());
                        }

                    }
                });

    }

    //method to show progress when we create account.
    void changeInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountbtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData (String FirstName,String LastName,String Email,String Password,String ConfirmPassword) {

        if (FirstName.isEmpty()) {
            firstName.setError("First Name is required");
            firstName.requestFocus();
            return false;
        }

        if (LastName.isEmpty()) {
            lastName.setError("Last Name is required");
            lastName.requestFocus();
            return false;
        }
        if(Email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Email is invalid. Please, provide a valid email.");
          email.requestFocus();
            return false;
        }

        if (Password.isEmpty() || password.length() < 8) {
            password.setError("Password length is invalid");
            password.requestFocus();
            return false;
        }

        if (!Password.equals(ConfirmPassword)) {
            confirmPassword.setError("Password does not match");
            confirmPassword.requestFocus();
            return false;
        }
return true;
    }

    public static class User {
        public String email;
        public String firstname;
        public String lastname;
        public String address;

        public User() {}  // Needed for Firebase

        public User(String email,String firstname, String Lastname , String address) {
            this.firstname = firstname;
            this.lastname = Lastname;
            this.email = email;
            this.address = address;
        }
    }

}


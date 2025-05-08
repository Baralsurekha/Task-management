package com.example.taskmanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.net.ssl.ManagerFactoryParameters;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.USE_EXACT_ALARM}, 102);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.USE_EXACT_ALARM,Manifest.permission.SCHEDULE_EXACT_ALARM}, 101);
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // check user login or not
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        //if user didn't login
                        if (currentUser == null) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, Homepage.class));
                        }

                        finish();
                    }
                }, 2000);
            }
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // check user login or not
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    //if user didn't login
                    if (currentUser == null) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, Homepage.class));
                    }

                    finish();
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // check user login or not
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                //if user didn't login
                if (currentUser == null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, Homepage.class));
                }

                finish();
            }
        }, 2000);
    }
}
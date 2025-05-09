package com.example.taskmanagement;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlarmManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Homepage extends AppCompatActivity {
    FloatingActionButton addTaskbtn;
    RecyclerView recyclerView;

    public List<taskModel> taskModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        FloatingActionButton addTaskFloatingButton = findViewById(R.id.floatingAction);
        ImageView userDetails = findViewById(R.id.homeUser);
        ImageView notifications = findViewById(R.id.homeNotification);
        TextView userName = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recycler_view);

        addTaskFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Registration Activity
                Intent intent = new Intent(Homepage.this, CreateTask.class);
                startActivity(intent);
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Notification.class);
                startActivity(intent);
            }
        });

        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, Userdetails.class);
                startActivity(intent);
            }
        });
        setupRecyclerView();

      //  setUptaskModels();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("users")
                    .document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                Registration.User user = task.getResult().toObject(Registration.User.class);
                                if (user != null)
                                    userName.setText(user.firstname);

                            }
                        }
                    });
          /*  database.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String email = task.getResult().child("email").getValue(String.class);
                        String Firstname = task.getResult().child("firstname").getValue(String.class);


                        userName.setText(Firstname);

                        Toast.makeText(this, "User email: " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });*/
        }
        GetTaskList();

    }

    private void GetTaskList() {


        Utility.getCollectionReferenceForTasks()

                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snap = task.getResult();

                        taskModels = task.getResult().toObjects(taskModel.class);
                        if (taskModels != null) {
                            Log.e("Sucess", "List fetch sucessfully " + taskModels.size());
                            recyclerView.setAdapter(new RVadapter( this ,taskModels ));
                            SetUpAlaram();
                        }
                        // Use retrievedTask
                    } else {
                        Log.e("Sucess", "document fetch failed");  // Document doesn't exist
                    }

                });
    }

    private void SetUpAlaram() {
        for (taskModel model : taskModels
        ) {


            Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
            intent.putExtra("taskId", model.getTaskName());
            intent.putExtra("TaskDesc", model.getTaskDescription());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    model.getTaskName().hashCode(), // unique id
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    model.getDeadlineMillis() - (60 * 60 * 1000), // Notify 60 minutes before deadline
                    pendingIntent
            );

        }
    }


    void setupRecyclerView() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.MARCH, 31);
        long deadlineMillis = calendar.getTimeInMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetTaskList();
    }


}


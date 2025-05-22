package com.example.taskmanagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.google.type.DateTime;

import org.w3c.dom.Text;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Homepage extends AppCompatActivity {

    RecyclerView recyclerView;
    SearchView searchView;
    ImageView historyButton;
    public List<taskModel> taskModels;
    public List<taskModel> filteredTaskModels;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "taskChannel",                 // Channel ID
                    "Task Reminders",              // Channel Name
                    NotificationManager.IMPORTANCE_HIGH // Importance
            );
            channel.setDescription("Channel for task deadline reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        FloatingActionButton addTaskFloatingButton = findViewById(R.id.floatingAction);
        ImageView userDetails = findViewById(R.id.homeUser);
        ImageView notifications = findViewById(R.id.homeNotification);
        TextView userName = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.searchView);
        historyButton = findViewById(R.id.historyButton);

        // Set up search functionality
        setupSearchView();

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

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Homepage.this, TaskHistory.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("Homepage", "Error starting TaskHistory: " + e.getMessage());
                    Toast.makeText(Homepage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupRecyclerView();


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

        }
        GetTaskList();

    }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTasks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });
    }
    private void filterTasks(String query) {
        if (taskModels == null) return;

        filteredTaskModels = new ArrayList<>();

        if (query.isEmpty()) {
            filteredTaskModels.addAll(taskModels);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (taskModel task : taskModels) {
                if (task.getTaskName().toLowerCase().contains(lowerCaseQuery) ||
                        task.getTaskDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredTaskModels.add(task);
                }
            }
        }

        // Update the adapter with filtered tasks
        RVadapter adapter = new RVadapter(this, filteredTaskModels);
        recyclerView.setAdapter(adapter);
    }
    private void GetTaskList() {


        Utility.getCollectionReferenceForTasks()

                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snap = task.getResult();

                        if (snap != null) {
                             taskModels = snap.toObjects(taskModel.class);  // Convert to taskModel list

                            for (int i = 0; i < taskModels.size(); i++) {
                                taskModels.get(i).setDocumentId(snap.getDocuments().get(i).getId()); // Store document ID

                            }
                            // Sort tasks by deadline in ascending order
                            Collections.sort(taskModels, new Comparator<taskModel>() {
                                @Override
                                public int compare(taskModel task1, taskModel task2) {
                                    return Long.compare(task1.getDeadlineMillis(), task2.getDeadlineMillis());
                                }
                            });

                            Log.e("Success", "List fetched successfully: " + taskModels.size());
                            RVadapter adapter = new RVadapter(this, taskModels);
                            recyclerView.setAdapter(adapter); // Set the updated adapter
                            adapter.notifyDataSetChanged();  // Notify adapter that data has changed

                            if (!taskModels.isEmpty()) {
                                SetUpAlaram();
                            }
                        }  else {
                            Log.e("Failure", "No tasks found");
                        }
                    } else {
                        Log.e("Failure", "Error fetching tasks: " + task.getException());
                    }
                });
    }

    private void SetUpAlaram() {
        long currentTimeMillis = System.currentTimeMillis();

        for (taskModel model : taskModels) {
            long notificationTime = model.getDeadlineMillis() - (30 * 1000); // 30 seconds before deadline

            if (notificationTime > currentTimeMillis) {  // Only schedule if time is in the future
                Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
                intent.putExtra("taskId", model.getTaskName());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        model.getTaskName().hashCode(), // unique id
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                Log.e("Success:", "Time = " + model.getDeadlineMillis());
                Log.d("Success:", "Time at notification = " + notificationTime);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            pendingIntent
                    );
                }
            } else {
                Log.w("Skipped:", "Notification time already passed for task: " + model.getTaskName());
            }
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


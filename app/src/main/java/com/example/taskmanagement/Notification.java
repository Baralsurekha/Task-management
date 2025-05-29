package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Notification extends AppCompatActivity {
    public List<taskModel> taskModel;// = new List<taskModel>() ;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        taskModel =  new ArrayList<>();
        ImageView backArrow = findViewById(R.id.imageView5);
        recyclerView = findViewById(R.id.notificationView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        });
        GetTaskList();
    }
    private void GetTaskList() {

         ;
        Utility.getCollectionReferenceForTasks()

                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snap = task.getResult();

                        if (snap != null) {
                            List<taskModel> taskModels = snap.toObjects(taskModel.class);  // Convert to taskModel list
                            long currentTimeMillis = System.currentTimeMillis();
                            for (int i = 0; i < taskModels.size(); i++) {
                                taskModels.get(i).setDocumentId(snap.getDocuments().get(i).getId()); // Store document ID

                                   if(currentTimeMillis>taskModels.get(i).getnotideadlineMillis()){

                                       taskModel.add(taskModels.get(i));

                                   }

                                }
                            // Sort tasks by deadline in ascending order
                            Collections.sort(taskModels, new Comparator<taskModel>() {
                                @Override
                                public int compare(taskModel task1, taskModel task2) {
                                    return Long.compare(task1.getDeadlineMillis(), task2.getDeadlineMillis());
                                }
                            });

                            Log.e("Success", "List fetched successfully: " + taskModels.size());
                            if(taskModel.size()>0) {
                                RVadapter adapter = new RVadapter(this, taskModel);
                                recyclerView.setAdapter(adapter); // Set the updated adapter

                                adapter.notifyDataSetChanged();  // Notify adapter that data has changed
                            }

                        }  else {
                            Log.e("Failure", "No tasks found");
                        }
                    } else {
                        Log.e("Failure", "Error fetching tasks: " + task.getException());
                    }
                });
    }

}
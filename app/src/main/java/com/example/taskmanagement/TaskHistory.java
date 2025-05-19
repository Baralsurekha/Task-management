package com.example.taskmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaskHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    List<taskModel> historyTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        TextView emptyHistoryText = findViewById(R.id.emptyHistoryText);

        // Get history tasks
        try {
            // First check if there are any tasks in history
            Utility.getCollectionReferenceForTaskHistory()
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snap = task.getResult();

                            if (snap != null && !snap.isEmpty()) {
                                historyTasks = snap.toObjects(taskModel.class);

                                for (int i = 0; i < historyTasks.size(); i++) {
                                    historyTasks.get(i).setDocumentId(snap.getDocuments().get(i).getId());
                                }

                                Log.d("TaskHistory", "History list fetched: " + historyTasks.size());

                                // Use the regular RVadapter for now to ensure it works
                                RVadapter adapter = new RVadapter(this, historyTasks);
                                recyclerView.setAdapter(adapter);

                                emptyHistoryText.setVisibility(View.GONE);
                                Toast.makeText(this, "Found " + historyTasks.size() + " history items", Toast.LENGTH_SHORT).show();
                            } else {
                                // If no history items, check completed tasks in the main collection
                                checkCompletedTasksInMainCollection(emptyHistoryText);
                            }
                        } else {
                            checkCompletedTasksInMainCollection(emptyHistoryText);
                            Log.e("Failure", "Error fetching history: " + task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e("TaskHistory", "Error: " + e.getMessage());
            emptyHistoryText.setVisibility(View.VISIBLE);
            emptyHistoryText.setText("Error loading history: " + e.getMessage());
        }
    }

    private void checkCompletedTasksInMainCollection(TextView emptyHistoryText) {
        // If no history items found, look for completed or failed tasks in the main collection
        Utility.getCollectionReferenceForTasks()
                .get().addOnCompleteListener(mainTask -> {
                    if (mainTask.isSuccessful()) {
                        QuerySnapshot mainSnap = mainTask.getResult();

                        if (mainSnap != null && !mainSnap.isEmpty()) {
                            List<taskModel> allTasks = mainSnap.toObjects(taskModel.class);
                            List<taskModel> completedTasks = new ArrayList<>();

                            for (int i = 0; i < allTasks.size(); i++) {
                                taskModel currentTask = allTasks.get(i);
                                String status = currentTask.getTaskStatus();

                                if ("Failed".equals(status) || "Finished".equals(status) || currentTask.isCompleted()) {
                                    currentTask.setDocumentId(mainSnap.getDocuments().get(i).getId());
                                    completedTasks.add(currentTask);

                                    // Move this task to history collection
                                    Utility.moveTaskToHistory(currentTask);
                                }
                            }

                            if (!completedTasks.isEmpty()) {
                                RVadapter adapter = new RVadapter(this, completedTasks);
                                recyclerView.setAdapter(adapter);
                                emptyHistoryText.setVisibility(View.GONE);
                                Toast.makeText(this, "Found " + completedTasks.size() + " completed tasks", Toast.LENGTH_SHORT).show();
                            } else {
                                emptyHistoryText.setVisibility(View.VISIBLE);
                                emptyHistoryText.setText("No completed or failed tasks found");
                            }
                        } else {
                            emptyHistoryText.setVisibility(View.VISIBLE);
                            emptyHistoryText.setText("No tasks found");
                        }
                    } else {
                        emptyHistoryText.setVisibility(View.VISIBLE);
                        emptyHistoryText.setText("Error loading tasks");
                    }
                });
    }
}
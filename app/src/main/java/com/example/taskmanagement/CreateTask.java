package com.example.taskmanagement;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTask extends AppCompatActivity {

    EditText taskName, taskDescription, taskStatus, deadlineDate, deadlineTime;
    Button addTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        taskName = findViewById(R.id.editTextname);
        taskDescription = findViewById(R.id.editTextdescription);
        taskStatus = findViewById(R.id.editStatus);
        deadlineDate = findViewById(R.id.editTextDate);
        deadlineTime = findViewById(R.id.editTextTime);
        addTaskBtn = findViewById(R.id.addbutton);


        addTaskBtn.setOnClickListener(v -> saveNote());
    }
    void saveNote(){
        String TaskName = taskName.getText().toString();
        String TaskDescription = taskDescription.getText().toString();
        String TaskStatus = taskStatus.getText().toString();
        String DeadlineDate =deadlineDate.getText().toString();
        String DeadlineTime = deadlineTime.getText().toString();

        boolean isValidated = validateData(TaskName, TaskDescription,TaskStatus, DeadlineDate, DeadlineTime);  //validation is true

        //if validation is false, it will return.
        if(!isValidated) {
            return;
        }
        // Combine and parse date + time
        String dateTimeString = DeadlineDate + " " + DeadlineTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        long deadlineMillis;
        try {
            // in format yyyy-mm-dd
            Date date = sdf.parse(dateTimeString);
            deadlineMillis = date.getTime();
        } catch (ParseException e) {
            deadlineDate.setError("Invalid date or time format");
            deadlineDate.requestFocus();
            return;
        }

        // adding task using taskModel
        taskModel task = new taskModel();
        task.setTaskName(TaskName);
        task.setTaskDescription(TaskDescription);
        task.setTaskStatus(TaskStatus);
        task.setDeadlineMillis(deadlineMillis);

        //saving task to firebase
        saveTaskToFirebase(task);

    }
    void saveTaskToFirebase(taskModel task) {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForTasks().document();

        documentReference.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> tasks) {

                if(tasks.isSuccessful()) {
                    Utility.showToast(CreateTask.this,"Task added successfully");
                    finish();

                } else {

                    Utility.showToast(CreateTask.this,tasks.getException().getLocalizedMessage());
                }
            }
        });

    }

boolean validateData (String TaskName,String TaskDescription,String TaskStatus,String DeadlineDate,String DeadlineTime) {

    if (TaskName.isEmpty()) {
        taskName.setError("Task Name is required");
        taskName.requestFocus();
        return false;
    }

    if (TaskDescription.isEmpty()) {
        taskDescription.setError("Task Description is required");
        taskDescription.requestFocus();
        return false;
    }

    if (TaskStatus.isEmpty()) {
        taskStatus.setError("Task Status is required");
        taskStatus.requestFocus();
        return false;
    }
    if(DeadlineDate.isEmpty()) {
        deadlineDate.setError("Please, add task deadline date.");
        deadlineDate.requestFocus();
        return false;
    }

    if(DeadlineTime.isEmpty()){
        deadlineTime.setError("Please, add task deadline time.");
        deadlineTime.requestFocus();
        return false;
    }
    return true;
}


}
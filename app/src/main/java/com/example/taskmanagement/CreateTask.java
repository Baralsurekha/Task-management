package com.example.taskmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTask extends AppCompatActivity {
    String TaskStatus ;
    EditText taskName, taskDescription;
    TextView deadlineDate, deadlineTime, pageTitleTV;
    Spinner taskStatus;
    Button addTaskBtn;
    CheckBox taskCompletedCheckbox;
    Calendar calendar = Calendar.getInstance();
    String docId, TaskName, TaskDescription;
    boolean isCompleted = false;
    boolean isEditMode = false;
    Button deleteTaskBtn;

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
        taskStatus = findViewById(R.id.spinner);
        deadlineDate = findViewById(R.id.editTextDate);
        deadlineTime = findViewById(R.id.editTextTime);
        addTaskBtn = findViewById(R.id.addbutton);
        pageTitleTV = findViewById(R.id.textView7);
        deleteTaskBtn = findViewById(R.id.delete_button);
        taskCompletedCheckbox = findViewById(R.id.taskCompletedCheckbox);

        //receive data
        docId = getIntent().getStringExtra("docId");  // Retrieve docId from Intent
        if(docId!=null && !docId.isEmpty()) {
            isEditMode = true;
        }

//set up spinner
        String[] options = {"TODO", "Progress", "Failed","Finished"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskStatus.setAdapter(adapter);


        // ─── Load all incoming intent data (including existing date/time) ───
        loadExistingTaskData(adapter);

        // Auto‐check “Finished”
        taskStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaskStatus = parent.getItemAtPosition(position).toString();
                if ("Finished".equals(TaskStatus)) {
                    taskCompletedCheckbox.setChecked(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional
            }
        });


        deadlineDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                deadlineDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        //time picker
        deadlineTime.setOnClickListener(v ->
            new TimePickerDialog(this, (tp, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                deadlineTime.setText(
                        new SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(calendar.getTime())
                );
            },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show());

        if(isEditMode){
            pageTitleTV.setText("Edit Task");
            deleteTaskBtn.setVisibility(View.VISIBLE);

        }

        addTaskBtn.setOnClickListener(v -> saveTask());
        deleteTaskBtn.setOnClickListener(v -> deleteTasktoFirebase());
    }
    private void loadExistingTaskData(ArrayAdapter<String> adapter) {
        if (!isEditMode) return;

        // Basic fields
        TaskName        = getIntent().getStringExtra("taskName");
        TaskDescription = getIntent().getStringExtra("taskDescription");
        TaskStatus      = getIntent().getStringExtra("taskStatus");
        isCompleted     = getIntent().getBooleanExtra("isCompleted", false);

        taskName.setText(TaskName);
        taskDescription.setText(TaskDescription);
        taskCompletedCheckbox.setChecked(isCompleted);

        // Spinner selection
        if (TaskStatus != null) {
            int pos = adapter.getPosition(TaskStatus);
            if (pos >= 0) taskStatus.setSelection(pos);
        }

        // **Use the same key your adapter used!**
        long deadlineMillis = getIntent().getLongExtra("deadlineMillis", -1);
        if (deadlineMillis != -1) {
            Date existing = new Date(deadlineMillis);
            calendar.setTime(existing);
            deadlineDate.setText(
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(existing)
            );
            deadlineTime.setText(
                    new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(existing)
            );
        }
    }
    void saveTask(){
        String TaskName = taskName.getText().toString().trim();
        String TaskDescription = taskDescription.getText().toString().trim();
        String DeadlineDate =deadlineDate.getText().toString().trim();
        String DeadlineTime = deadlineTime.getText().toString().trim();
        boolean completed      = taskCompletedCheckbox.isChecked();

        boolean isValidated = validateData(TaskName, TaskDescription,TaskStatus, DeadlineDate, DeadlineTime);  //validation is true
        //if validation is false, it will return.
        if(!isValidated) {
            return;
        }

        long millis;
        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .parse(DeadlineDate + " " + DeadlineTime);
            millis = dt.getTime();
        } catch (ParseException e) {
            deadlineDate.setError("Invalid date/time");
            return;
        }

        // adding task using taskModel
        taskModel task = new taskModel();
        task.setTaskName(TaskName);
        task.setTaskDescription(TaskDescription);
        task.setTaskStatus(TaskStatus);
        task.setDeadlineMillis(millis);
        task.setCompleted(completed);


        /** //saving task to firebase
        saveTaskToFirebase(task);

    void    saveTaskToFirebase(taskModel task) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForTasks().document(docId);
        } else {  
            documentReference = Utility.getCollectionReferenceForTasks().document();
        }

        documentReference.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> tasks) {

                if(tasks.isSuccessful()) {
                    Utility.showToast(CreateTask.this,"Task updatedd successfully");
                    finish();

                } else {

                    Utility.showToast(CreateTask.this,tasks.getException().getLocalizedMessage());
                }
            }
        });
    } */
    // If finished → history
        if (completed && "Finished".equals(TaskStatus)) {
        if (isEditMode) {
            task.setDocumentId(docId);
            Utility.moveTaskToHistory(task);
            finish();
        } else {
            DocumentReference hr = Utility.getCollectionReferenceForTaskHistory().document();
            hr.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override public void onComplete(Task<Void> t) {
                    finish();
                }
            });
        }
    } else {
        // Save/update
        DocumentReference dr = isEditMode
                ? Utility.getCollectionReferenceForTasks().document(docId)
                : Utility.getCollectionReferenceForTasks().document();
        dr.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override public void onComplete(Task<Void> t) {
                finish();
            }
        });
    }
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
/**
void deleteTasktoFirebase(){
    DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForTasks().document(docId);

    documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> tasks) {

            if(tasks.isSuccessful()) {
                Utility.showToast(CreateTask.this,"Task deleted successfully");
                finish();

            } else {

                Utility.showToast(CreateTask.this,tasks.getException().getLocalizedMessage());
            }
        }
    });

} */
void deleteTasktoFirebase() {
    if (!isEditMode) return;
    taskModel task = new taskModel();
    task.setDocumentId(docId);
    task.setTaskName(taskName.getText().toString());
    task.setTaskDescription(taskDescription.getText().toString());
    task.setTaskStatus("Deleted");
    try {
        Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .parse(deadlineDate.getText() + " " + deadlineTime.getText());
        task.setDeadlineMillis(dt.getTime());
    } catch (Exception e) {
        task.setDeadlineMillis(System.currentTimeMillis());
    }
    Utility.moveTaskToHistory(task);
    finish();
}

}
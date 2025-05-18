package com.example.taskmanagement;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RVadapter extends RecyclerView.Adapter<RVadapter.TaskViewHolder> {

Context context;
    private List<taskModel> taskList;


    public RVadapter(Context context, List<taskModel> taskList) {
        this.context = context;
        this.taskList =taskList;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        taskModel task = taskList.get(position);

        holder.textTaskName.setText(task.getTaskName());
        holder.textTaskDescription.setText(task.getTaskDescription());
        holder.textTaskStatus.setText("Status: " + task.getTaskStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String formattedDeadline = sdf.format(new Date(task.getDeadlineMillis()));
        // Set calendar to show the deadline date
        holder.calendarView.setText(formattedDeadline);

        // Disable interaction with calendar since this is frontend only
        holder.calendarView.setEnabled(false);

        //going to create task activity to edit
        holder.itemView.setOnClickListener((V)->{
            Intent intent = new Intent(context, CreateTask.class);
            intent.putExtra("taskName",task.getTaskName());
            intent.putExtra("taskDescription",task.getTaskDescription());
            intent.putExtra("taskStatus",task.getTaskStatus());
            intent.putExtra("deadlineMillis", task.getDeadlineMillis());

            // log docID value
            String docId = task.getDocumentId();
            Log.e("docId", "docId is: " + docId);


            intent.putExtra("docId",task.getDocumentId()); //pass docID
            context.startActivity(intent);
        });

    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskName, textTaskDescription, textTaskStatus;
        TextView calendarView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
           textTaskName = itemView.findViewById(R.id.textTaskName);
            textTaskDescription = itemView.findViewById(R.id.textTaskDescription);
            textTaskStatus = itemView.findViewById(R.id.textTaskStatus);
            calendarView = itemView.findViewById(R.id.calendarView);

        }
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
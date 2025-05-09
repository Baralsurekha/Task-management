package com.example.taskmanagement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVadapter extends RecyclerView.Adapter<RVadapter.TaskViewHolder> {

Context context;
    private List<taskModel> taskList;


    public RVadapter(Context context, List<taskModel> taskList) {
        this.context = context;
        this.taskList =taskList;
    }

    // Constructor

  /*  @NonNull
    @Override
    public taskModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new TaskViewHolder(view);
    }*/

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

        // Set calendar to show the deadline date
        holder.calendarView.setDate(task.getDeadlineMillis(), true, true);

        // Disable interaction with calendar since this is frontend only
        holder.calendarView.setEnabled(false);
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskName, textTaskDescription, textTaskStatus, timestampTextView;
        CalendarView calendarView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
           textTaskName = itemView.findViewById(R.id.textTaskName);
            textTaskDescription = itemView.findViewById(R.id.textTaskDescription);
            textTaskStatus = itemView.findViewById(R.id.textTaskStatus);
            calendarView = itemView.findViewById(R.id.calendarView);
            timestampTextView = itemView.findViewById(R.id.timestamp_textView);
        }
    }


  /*  @Override
    protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull taskModel model) {
        taskModel task = taskList.get(position);

        holder.textTaskName.setText(task.getTaskName());
        holder.textTaskDescription.setText(task.getTaskDescription());
        holder.textTaskStatus.setText("Status: " + task.getTaskStatus());

        // Set calendar to show the deadline date
        holder.calendarView.setDate(task.getDeadlineMillis(), true, true);

        // Disable interaction with calendar since this is frontend only
        holder.calendarView.setEnabled(false);
    }
*/
    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
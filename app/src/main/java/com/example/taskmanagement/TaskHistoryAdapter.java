package com.example.taskmanagement;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.HistoryViewHolder> {

    Context context;
    private List<taskModel> taskList;

    public TaskHistoryAdapter(Context context, List<taskModel> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        taskModel task = taskList.get(position);

        holder.textTaskName.setText(task.getTaskName());
        holder.textTaskDescription.setText(task.getTaskDescription());

        // Format the date nicely
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(task.getDeadlineMillis()));
        holder.textDeadline.setText("Deadline: " + formattedDate);

        // Set status with appropriate color
        String status = task.getTaskStatus();
        holder.textTaskStatus.setText("Status: " + status);

        if ("Failed".equals(status)) {
            holder.textTaskStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        } else if ("Finished".equals(status)) {
            holder.textTaskStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            // Add strikethrough for completed tasks
            holder.textTaskName.setPaintFlags(holder.textTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textTaskDescription.setPaintFlags(holder.textTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskName, textTaskDescription, textTaskStatus, textDeadline;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskName = itemView.findViewById(R.id.historyTaskName);
            textTaskDescription = itemView.findViewById(R.id.historyTaskDescription);
            textTaskStatus = itemView.findViewById(R.id.historyTaskStatus);
            textDeadline = itemView.findViewById(R.id.historyTaskDeadline);
        }
    }
}


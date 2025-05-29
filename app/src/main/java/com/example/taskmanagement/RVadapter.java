package com.example.taskmanagement;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        Date dl = new Date(task.getDeadlineMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dl);

        holder.monthText.setText(new SimpleDateFormat("MMM", Locale.getDefault())
                .format(dl).toUpperCase());
        holder.dayText.setText(String.format(Locale.getDefault(), "%02d",
                cal.get(Calendar.DAY_OF_MONTH)));
        holder.yearText.setText(String.valueOf(cal.get(Calendar.YEAR)));
        holder.timeText.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(dl));

        // urgency coloring
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        if (isSameDay(cal, today)) {
            holder.monthText.setBackgroundColor(
                    context.getResources().getColor(android.R.color.holo_red_light));
        } else if (isSameDay(cal, tomorrow)) {
            holder.monthText.setBackgroundColor(
                    context.getResources().getColor(android.R.color.holo_orange_light));
        } else if (cal.before(today)) {
            holder.monthText.setBackgroundColor(
                    context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.monthText.setBackgroundColor(
                    context.getResources().getColor(android.R.color.holo_green_light));
        }

        updateCompletionUI(holder, task);

        holder.completedIcon.setOnClickListener(v -> toggleTaskCompletion(task, holder));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateTask.class);
            intent.putExtra("taskName", task.getTaskName());
            intent.putExtra("taskDescription", task.getTaskDescription());
            intent.putExtra("taskStatus", task.getTaskStatus());
            intent.putExtra("isCompleted", task.isCompleted());
            intent.putExtra("deadlineMillis", task.getDeadlineMillis());
            intent.putExtra("notiMillis", task.getnotideadlineMillis());
            intent.putExtra("docId", task.getDocumentId());
            context.startActivity(intent);
        });
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private void updateCompletionUI(TaskViewHolder h, taskModel t) {
        if (t.isCompleted()) {
            h.completedIcon.setImageResource(android.R.drawable.checkbox_on_background);
            h.textTaskName.setPaintFlags(
                    h.textTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.textTaskDescription.setPaintFlags(
                    h.textTaskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.completedIcon.setImageResource(android.R.drawable.checkbox_off_background);
            h.textTaskName.setPaintFlags(
                    h.textTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            h.textTaskDescription.setPaintFlags(
                    h.textTaskDescription.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void toggleTaskCompletion(taskModel task, TaskViewHolder holder) {
        boolean newStatus = !task.isCompleted();
        task.setCompleted(newStatus);

        if (newStatus) {
            task.setTaskStatus("Finished");
            updateCompletionUI(holder, task);
            holder.textTaskStatus.setText("Status: Finished");
            Utility.moveTaskToHistory(task);
            Toast.makeText(context, "Task completed and moved to history", Toast.LENGTH_SHORT).show();
            int pos = taskList.indexOf(task);
            if (pos != -1) {
                taskList.remove(pos);
                notifyItemRemoved(pos);
            }
        } else {
            DocumentReference docRef = Utility.getCollectionReferenceForTasks()
                    .document(task.getDocumentId());
            docRef.update("completed", newStatus)
                    .addOnSuccessListener(aVoid -> updateCompletionUI(holder, task))
                    .addOnFailureListener(e -> {
                        task.setCompleted(!newStatus);
                        updateCompletionUI(holder, task);
                        Toast.makeText(context, "Failed to update task: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override public int getItemCount() { return taskList.size(); }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTaskName, textTaskDescription, textTaskStatus;
        TextView monthText, dayText, yearText, timeText;
        ImageView completedIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTaskName        = itemView.findViewById(R.id.textTaskName);
            textTaskDescription = itemView.findViewById(R.id.textTaskDescription);
            textTaskStatus      = itemView.findViewById(R.id.textTaskStatus);
            monthText           = itemView.findViewById(R.id.monthText);
            dayText             = itemView.findViewById(R.id.dayText);
            yearText            = itemView.findViewById(R.id.yearText);
            timeText            = itemView.findViewById(R.id.timeText);
            completedIcon       = itemView.findViewById(R.id.completedIcon);
        }
    }
}

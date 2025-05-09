package com.example.taskmanagement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RVadapter extends FirestoreRecyclerAdapter<taskModel, RVadapter.TaskViewHolder> {

Context context;
    private List<taskModel> taskList;


    public RVadapter(@NonNull FirestoreRecyclerOptions<taskModel> options, Context context) {
        super(options);
        this.context = context;
    }

    // Constructor

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new TaskViewHolder(view);
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


    @Override
    protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull taskModel model) {
        
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
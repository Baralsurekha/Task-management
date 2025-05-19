package com.example.taskmanagement;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {
    public static boolean isfreshlogin;
    static void showToast(Context context, String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    static CollectionReference getCollectionReferenceForTasks(){
        FirebaseUser curreUser = FirebaseAuth.getInstance().getCurrentUser();
      return  FirebaseFirestore.getInstance().collection("tasks")
                .document(curreUser.getUid()).collection("my_tasks");
    }
    static CollectionReference getCollectionReferenceForTaskHistory() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance()
                .collection("task_history")
                .document(currentUser.getUid())
                .collection("my_history");
    }

    static void moveTaskToHistory(taskModel task) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference historyRef = FirebaseFirestore.getInstance()
                    .collection("task_history")
                    .document(currentUser.getUid())
                    .collection("my_history")
                    .document();

            if ("Finished".equals(task.getTaskStatus())) {
                task.setCompleted(true);
            }

            historyRef.set(task)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Utility", "Task moved to history");
                        if (task.getDocumentId() != null && !task.getDocumentId().isEmpty()) {
                            getCollectionReferenceForTasks()
                                    .document(task.getDocumentId())
                                    .delete();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Utility", "Error moving to history", e));
        } catch (Exception e) {
            Log.e("Utility", "Exception in moveTaskToHistory", e);
        }
    }
}


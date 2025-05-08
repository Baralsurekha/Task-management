package com.example.taskmanagement;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
}

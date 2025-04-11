package com.example.taskmanagement;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    public static FirebaseManager instance;
   private FirebaseDatabase database;
    public  FirebaseManager() {

        database  = FirebaseDatabase.getInstance();
        instance = this ;
    }
    public void login (String username,String password){
    if(password ==    database.getReference("Users").child(username).toString()){
//       TODo Login sucess
    }
    }
}

package com.example.andr2app.notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    public MyFirebaseIdService() {
    }

   @Override
    public void onNewToken(String s){
        super.onNewToken(s);
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       String token = FirebaseInstanceId.getInstance().getToken();
       if (user != null){
           updateToken(token);
       }

   }

   void updateToken(String token){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Token token1 = new Token(token);
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
   }
}
